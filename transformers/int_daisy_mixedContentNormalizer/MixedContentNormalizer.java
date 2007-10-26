package int_daisy_mixedContentNormalizer;

import int_daisy_mixedContentNormalizer.dom.DOMConfig;
import int_daisy_mixedContentNormalizer.dom.DOMConfigLoader;
import int_daisy_mixedContentNormalizer.dom.DOMNormalizer;
import int_daisy_mixedContentNormalizer.dom.DOMSyncPointLocator;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.Location;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.dom.Serializer;
import org.daisy.util.xml.pool.LSParserPool;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSParser;

/**
 * Perform XML mixed content normalization, optionally adding sync point markers to appropriate elements.
 * <p>See <code>doc/transformers/int_daisy_mixedContentNormalizer</code>.</p>
 * @author Markus Gylling
 */
public class MixedContentNormalizer extends Transformer implements TransformerDelegateListener, DOMErrorHandler, FilesetErrorHandler {

	public MixedContentNormalizer(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		try {
			File input = FilenameOrFileURI.toFile((String)parameters.remove("input"));
			File output = FilenameOrFileURI.toFile((String)parameters.remove("output"));	
			boolean addSyncPoints = parameters.remove("addSyncPoints").equals("true");			
			String implementation = (String) parameters.remove("implementation");
			
			long start = System.nanoTime();
			if(implementation.equals("dom")) {						

				Map<String, Object> domConfigMap = null;
				LSParser parser = null;
				try{
					/*
					 * Get an instance of the configuration
					 */
					URL configURL = this.getClass().getResource("config.xml");
					DOMConfig config = DOMConfigLoader.load(configURL);
					
					/*
					 * Get some input statistics
					 */
					StreamSource ss = new StreamSource(input);
					InputDocInfoProvider docInfo = new InputDocInfoProvider(ss);
					String message = i18n("INPUT_DOC_ELEMENT_COUNT", docInfo.getElementCount());			
					this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null);

					/*
					 * Check if we have explicit support for given namespaces
					 */
					for(String uri : docInfo.getNamespaces()) {
						if(!config.supportsNamespace(uri)) {
							message = i18n("INPUT_DOC_UNRECOGNIZED_NAMESPACE", uri);
							this.sendMessage(message, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);					
						}
					}
					
					/*
					 * Load the input as DOM
					 */
					domConfigMap = LSParserPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
					parser = LSParserPool.getInstance().acquire(domConfigMap);
					DOMConfiguration domConfig = parser.getDomConfig();						
					domConfig.setParameter("error-handler", this);
					domConfig.setParameter("entities", Boolean.FALSE);						
					Document doc = parser.parseURI(input.toURI().toString());
					
					
					/*
					 * Get an instance of the DOMNormalizer and invoke
					 */
					DOMNormalizer domNormalizer = new DOMNormalizer(this,config);
					domNormalizer.setInputDocElementCount(docInfo.getElementCount());
					DOMResult result = (DOMResult) domNormalizer.normalize(new DOMSource(doc,input.getAbsolutePath()));
					this.sendMessage(i18n("NORMALIZATION_MODIFICATIONS",domNormalizer.getNumberOfModifications()));			

					
					/*
					 * If feature is activated, get an instance of the DOMSyncPointLocator and invoke
					 */										
					if(addSyncPoints) {			
						DOMSyncPointLocator domSyncPointLocator = new DOMSyncPointLocator(this,config);
						domSyncPointLocator.setInputDocElementCount(domNormalizer.getFinalElementCount());
						result = (DOMResult) domSyncPointLocator.locate(new DOMSource(result.getNode()));
						this.sendMessage(i18n("SYNCPOINTS_ADDED",domSyncPointLocator.getNumberOfSyncPoints()));
					}
			
									
					/*
					 * Serialize the result.
					 */
					Map<String,Object> props = new HashMap<String,Object>();
					props.put("namespaces", Boolean.FALSE); //temp because of attributeNS bug(?) in Xerces DOM3LS					
					props.put("error-handler", this);	
					//props.put("format-pretty-print", Boolean.TRUE);					
					Serializer.serialize((Document)result.getNode(), output, "utf-8", props);					
									
				}finally{
					LSParserPool.getInstance().release(parser, domConfigMap);
				}
			}//	if(implementation.equals("dom"))
			
			long end = System.nanoTime();						
			System.err.println("normalized in " + (end-start)/1000000 + " milliseconds");
			
			/*
			 * Copy input fileset aux members, if any
			 */
			if(!input.getParentFile().equals(output.getParentFile())) {				
				EFolder out = null;
				FilesetImpl fileset = null;
				try{
					out = new EFolder(output.getParentFile());
					fileset = new FilesetImpl(input.toURI(),this);
					EFolder inputBaseDir = fileset.getManifestMember().getParentFolder();
					Iterator i = fileset.getLocalMembers().iterator();
					while(i.hasNext()) {
						File file = (File) i.next();
						if(!file.getCanonicalPath().equals(input.getCanonicalPath())) {
							if(file.getParentFile().getCanonicalPath().equals(inputBaseDir.getCanonicalPath())) {
								//file is in same dir as manifestfile
								out.addFile(file,true);
							}else{
								//file is in subdir
								URI relative = inputBaseDir.toURI().relativize(file.getParentFile().toURI());
								if(!relative.toString().startsWith("..")) { 
									EFolder subdir = new EFolder(out,relative.getPath());
									FileUtils.createDirectory(subdir);
									subdir.addFile(file,true);
								}
							}
						}
					}					
				}catch (Exception e) {
					this.sendMessage(i18n("AUX_COPY_FAILURE", e.getMessage()));
				}
			}
		} catch (Exception e) {
			throw new TransformerRunException(e.getMessage(),e);
		}		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.DOMErrorHandler#handleError(org.w3c.dom.DOMError)
	 */
	public boolean handleError(DOMError error) {
		Location loc = LocusTransformer.newLocation(error.getLocation());		
		MessageEvent.Type type = null;
		if(error.getSeverity()==DOMError.SEVERITY_WARNING) {
			type = MessageEvent.Type.WARNING;
		}else{
			type = MessageEvent.Type.ERROR;
		}		
		this.sendMessage(error.getMessage(), type, MessageEvent.Cause.INPUT, loc);	    		
		if(error.getSeverity()==DOMError.SEVERITY_WARNING) {
		   return true;
	    }
		//return false; //TODO reset
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateMessage(java.lang.String, org.daisy.pipeline.core.event.MessageEvent.Type, org.daisy.pipeline.core.event.MessageEvent.Cause, javax.xml.stream.Location)
	 */
	public void delegateMessage(Object delegate, String message, Type type, Cause cause, Location location) {
		this.sendMessage(message, type, cause, location);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateProgress(java.lang.Class, double)
	 */	
	public void delegateProgress(Object delegate, double progress) {
		if(delegate instanceof AbstractNormalizer) {
			this.sendMessage(progress/2);
		}else{
			double p = 0.5+(progress/2);
			if(p>1.0) p = 1.0; //hmm
			this.sendMessage(p);
		}						
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.TransformerDelegateListener#delegateLocalize(java.lang.String, java.lang.Object)
	 */	
	public String delegateLocalize(String message, Object param) {
		if(param==null)
			return i18n(message);
		return i18n(message,param);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);		
	}

}