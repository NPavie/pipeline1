/*
 * org.daisy.util - The DAISY java utility library
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

package org.daisy.util.xml.validation.jaxp;

import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

/**
 * 
 * @author Markus Gylling
 */
public class RelaxNGSchema extends AbstractSchema {
	
	RelaxNGSchema(URL schema, SchemaFactory originator) {
		super(schema,originator);
	}
	
	RelaxNGSchema(Source[] sources, SchemaFactory originator){
		super(sources,originator);
	}
	
	public Validator newValidator()  {
		RelaxNGValidator validator = new RelaxNGValidator(this);
		validator.propagateHandlers(this.originator);	
		validator.initialize();
		return validator;
	}

	public ValidatorHandler newValidatorHandler() {
		return null; //TODO
	}

}
