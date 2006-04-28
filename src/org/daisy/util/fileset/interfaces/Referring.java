package org.daisy.util.fileset.interfaces;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;



/**
 * Interface for FilesetFiles that can 
 * refer to other entities (local or remote) via URIs
 * @author Markus Gylling
 */
public interface Referring  {
	
	public Iterator getUriIterator();
	
	public boolean hasUris();
	
	void putReferencedMember(URI uri, FilesetFile file);
	
	/**
	 * @param uri absolute URI of a fileset member 
	 * @return the corresponding member {@link org.daisy.util.FilesetImpl.FilesetFile} object if it is referenced from this member, null otherwise
	 */
	public FilesetFile getReferencedLocalMember(URI uri);
		
	/**
	 * @return a collection&lt;FilesetFile&gt; of the members referenced from this member; ordered as appearing in document order 
	 */
	public Collection getReferencedLocalMembers();
	
	/**
	 * Gives access to methods of java.io.File
	 */
	public File getFile();
	
//	/**
//	 * @return an iterator&lt;URI&gt; for the collection of members referenced from this member; ordered as appearing in document order 
//	 */
//	public Iterator getReferencedLocalMembersURIIterator();

}