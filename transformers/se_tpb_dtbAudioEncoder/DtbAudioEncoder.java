/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_dtbAudioEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetImpl;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * @author Linus Ericson
 */
public class DtbAudioEncoder extends Transformer {
    
    private LinkChanger linkChanger = null;
    private Wav2Mp3 wav2Mp3 = null;

    public DtbAudioEncoder(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);        
    }
    
    protected boolean execute(Map parameters) throws TransformerRunException {
        String manifest = (String)parameters.remove("manifest");
        String outDir = (String)parameters.remove("outDir");
        String filesetType = (String)parameters.remove("filesetType");
        String bitrate = (String)parameters.remove("bitrate");
        String stereo = (String)parameters.remove("stereo");
        String freq = (String)parameters.remove("freq");        
        
        sendMessage(Level.FINER, i18n("USING_MANIFEST", manifest));
        sendMessage(Level.FINER, i18n("USING_OUTDIR", outDir));
        sendMessage(Level.FINER, i18n("USING_FILESET_TYPE", filesetType));
        sendMessage(Level.FINER, i18n("USING_BITRATE", bitrate));
        sendMessage(Level.FINER, i18n("USING_STEREO", stereo));
        sendMessage(Level.FINER, i18n("USING_FREQ", freq));
        
        try {
            linkChanger = new LinkChanger();
            wav2Mp3 = new LameEncoder(Integer.parseInt(bitrate), Integer.parseInt(freq), "stereo".equals(stereo));
            
            // Build fileset
            sendMessage(Level.FINER, "Building fileset...");        
            Fileset fileset = this.buildFileSet(manifest);
            
            this.progress(0.02);
            this.checkAbort();
        
            // Make sure output dir exists
            File outputDirectory = FileUtils.createDirectory(new File(outDir));
            
            // Identify audio files and files that need to be updated
            sendMessage(Level.FINER, "Identifying files to encode or update...");
            Set alreadyDone = new HashSet();
            
            // Calculate total size of and number of wav files
            long totalSize = 0;
            int totalNum = 0;
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = (FilesetFile)it.next();                
                if (fsf instanceof AudioFile && fsf.getName().matches(".+\\.[Ww][Aa][Vv]")) {
                    totalSize += fsf.getFile().length();
                    ++totalNum;
                }                
            }
            
            this.progress(0.03);
            long currentSize = 0;
            int currentNum = 0;
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = (FilesetFile)it.next();
                
                if (fsf instanceof AudioFile && fsf.getName().matches(".+\\.[Ww][Aa][Vv]")) {
                    // Encode audio files
                    ++currentNum;
                    Object[] params = {new Integer(totalNum), new Integer(currentNum), fsf.getName()};
                    this.sendMessage(Level.FINER, i18n("ENCODING", params));
                    this.encodeFile(fsf.getFile(), outputDirectory, fileset.getRelativeURI(fsf));
                    alreadyDone.add(fsf.getName());
                    currentSize += fsf.getFile().length();
                    this.progress(0.03 + (0.95-0.03)*((double)currentSize/totalSize));
                    this.checkAbort();
            
                    // Modify files that reference the (old) audio files
                    for (Iterator it2 = fsf.getReferringLocalMembers().iterator(); it2.hasNext(); ) {
                        FilesetFile fsf2 = (FilesetFile)it2.next();
                        if (!alreadyDone.contains(fsf2.getName())) {
                            this.relinkFile(fsf2, outputDirectory, fileset.getRelativeURI(fsf2));                            
                        }
                        alreadyDone.add(fsf2.getName());
                    }
                }
                this.checkAbort();
            }
            
            this.progress(0.95);
            // Copy other files to destination
            for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
                FilesetFile fsf = (FilesetFile)it.next();
                
                this.checkAbort();
                
                if (!alreadyDone.contains(fsf.getName())) {                    
                    this.copyFile(fsf.getFile(), outputDirectory, fileset.getRelativeURI(fsf));
                }
                
            }
            this.progress(0.99);
                   
        } catch (FilesetException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (EncodingException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
        
        return true;
    }

    private Fileset buildFileSet(String manifest) throws FilesetException {
        return new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), false, true);
    }
    
    private void encodeFile(File wavFile, File outDir, URI relativeURI) throws EncodingException {
        URI out = outDir.toURI().resolve(relativeURI);
        String path = out.toString();
        int index = path.lastIndexOf(".");
        path = path.substring(0, index) + ".mp3";
        out = URI.create(path);
        wav2Mp3.encode(wavFile, new File(out));
    }
    
    private void copyFile(File inFile, File outDir, URI relativeURI) throws IOException {
        System.err.println("Copying " + inFile);
        URI out = outDir.toURI().resolve(relativeURI);
        //File outFile = new File(outDir, relativeURI.toString());
        FileUtils.copy(inFile, new File(out));
    }
    
    private void relinkFile(FilesetFile fsf, File outDir, URI relativeURI) throws FileNotFoundException, XMLStreamException {
        File in = fsf.getFile();
        File out = new File(outDir.toURI().resolve(relativeURI));
        System.err.println("Relinking " + fsf.getFile());        
        if (fsf instanceof OpfFile) {
            linkChanger.changeLinksOpf(in, out, "[Ww][Aa][Vv]", "mp3", "audio/mpeg");
        } else {
            linkChanger.changeLinks(in, out, "[Ww][Aa][Vv]", "mp3");
        }
    }
    
    public static boolean isSupported() {
        String lamePath = System.getProperty("dmfc.lame.path");
        //System.err.println("Lame path: " + lamePath);
        return LameEncoder.setCommand(lamePath);
    }
}