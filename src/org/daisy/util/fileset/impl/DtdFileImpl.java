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

package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.SchemaFile;
import org.daisy.util.fileset.interfaces.schema.DtdFile;


final class DtdFileImpl extends FilesetFileImpl implements DtdFile, SchemaFile {

	DtdFileImpl(URI uri, String mimeString) throws IOException, FileNotFoundException {
		super(uri, mimeString);
	}

	DtdFileImpl(URI uri) throws IOException, FileNotFoundException {
		super(uri, DtdFile.mimeStringConstant);
	}

	public void parse()  {
		//could use wutka here		
	}

	private static final long serialVersionUID = 5139283453997192932L;
	
}
