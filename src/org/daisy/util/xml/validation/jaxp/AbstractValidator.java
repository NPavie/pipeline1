package org.daisy.util.xml.validation.jaxp;

import javax.xml.validation.SchemaFactory;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.sax.SAXParseExceptionMessageFormatter;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author Markus Gylling
 */
public abstract class AbstractValidator extends javax.xml.validation.Validator implements ErrorHandler {
	protected ErrorHandler errorHandler = null;
	protected LSResourceResolver resourceResolver = null;
	protected EntityResolver entityResolver = null;
	boolean configSuccess = true;

	AbstractValidator(){
		super();
		errorHandler = this;
		try {
			//default value is not null 
			resourceResolver = CatalogEntityResolver.getInstance();
			entityResolver = CatalogEntityResolver.getInstance();
		} catch (CatalogExceptionNotRecoverable e) {
			
		}
	}
	/**
	 * Reset this Validator to its original configuration.
	 */
	public void reset() {
		errorHandler = this;
		try { 
			resourceResolver = CatalogEntityResolver.getInstance();
			entityResolver = CatalogEntityResolver.getInstance();
		} catch (CatalogExceptionNotRecoverable e) {
			
		}	
	}

	/**
	 * If they are not null, propagates ErrorHandler and ResourceResolver from the SchemaFactory that originated the Schema.
	 */
	public void propagateHandlers(SchemaFactory handlerCarrier) {
		if(handlerCarrier.getErrorHandler()!=null) this.setErrorHandler(handlerCarrier.getErrorHandler());
		if(handlerCarrier.getResourceResolver()!=null) this.setResourceResolver(handlerCarrier.getResourceResolver());
	}
	
		
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;		
	}

	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	public void setResourceResolver(LSResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;		
		if(this.resourceResolver instanceof EntityResolver){
			this.entityResolver = (EntityResolver)this.resourceResolver;
		}
	}

	public LSResourceResolver getResourceResolver() {
		return this.resourceResolver;		
	}

	/**
	 * Extension to javax.xml.validation.Validator
	 */
	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	/**
	 * Extension to javax.xml.validation.Validator
	 */
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	/**
	 * Extension to javax.xml.validation.Validator.
	 * This is the org.xml.sax.ErrorHandler impl
	 * that will recieve error notifications if the
	 * user has not registered another errorhandler.
	 */
	public void warning(SAXParseException spe) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Warning", spe));
	}

	/**
	 * Extension to javax.xml.validation.Validator.
	 * This is the org.xml.sax.ErrorHandler impl
	 * that will recieve error notifications if the
	 * user has not registered another errorhandler.
	 */	
	public void error(SAXParseException spe) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Error", spe));
	}

	/**
	 * Extension to javax.xml.validation.Validator.
	 * This is the org.xml.sax.ErrorHandler impl
	 * that will recieve error notifications if the
	 * user has not registered another errorhandler.
	 */
	public void fatalError(SAXParseException spe) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Fatal error", spe));		
	}
	
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new SAXNotRecognizedException(name);	                
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new SAXNotRecognizedException(name);	         
	}
	
	public void setProperty(String name, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");        
       	throw new SAXNotRecognizedException(name);	 
	}

	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new SAXNotRecognizedException(name);
    }
}