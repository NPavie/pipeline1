package org.daisy.util.fileset.validation;

import java.net.URI;
import java.util.List;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotRecognizedException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;

public interface Validator {

	/**
	 * Is validation of the inparam fileset supported by this Validator?
	 */
	public boolean isFilesetTypeSupported(FilesetType type);
	
	/**
	 * Retrieve a list of fileset types that this Validator supports validation of.
	 */
	public List getSupportedFilesetTypes();
	
	/**
	 * Register a ValidatorListener with this validator.	 
	 */
	public void setReportListener(ValidatorListener listener);
	
	/**
	 * @return a registered ValidatorListener, or null if none is registered.
	 */
	public ValidatorListener getValidatorListener();
	
	/**
	 * Validate a fileset.
	 * <p>This method is typically used when a fileset instance has already been created.</p>
	 * <p>Any user of this method should make sure to reroute errors from FilesetErrorHandler during 
	 * fileset instantiation (prior to the validator doing its own
	 * job) to the ValidatorListener.</p>
	 * @param fileset Fileset to validate
	 * @throws ValidatorException if the validation did not complete due to a nonrecoverable problem
	 * @throws ValidatorNotSupportedException if this validator does not support validation of the inparam fileset type
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException;
	
	/**
	 * Validate a fileset.
	 * <p>This method is typically used when a fileset instance has not yet been created.</p>
	 * <p>Errors reported during fileset instantiation (prior to the validator doing its own
	 * job) will be rerouted to the ValidatorListener.</p>
	 * @param manifest URI to main file of the fileset to validate
	 * @throws ValidatorException if the validation did not complete due to a nonrecoverable problem
	 * @throws ValidatorNotSupportedException if this validator does not support validation of the inparam fileset type
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException;
	
	/**
	 * Reset this Validator to its initial state. A validator that
	 * has been reset can be reused.
	 */
	public void reset();

	/**
	 * Retrieve a handle to the fileset currently registered with this Validator.
	 */
	public Fileset getFileset();
	
	public void setFeature(String name, boolean value) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;
	
	public boolean getFeature(String name) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;
    	
	public void setProperty(String name, Object object) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;

	public Object getProperty(String name) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;

	
}
