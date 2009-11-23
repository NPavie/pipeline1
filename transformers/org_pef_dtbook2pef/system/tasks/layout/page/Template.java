package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;

public interface Template {
	
	/**
	 * Get the header height.
	 * An implementation must ensure that getHeaderHeight()=getHeader().size() for all pagenum's
	 * @return returns the header height
	 */
	public int getHeaderHeight();

	/**
	 * Get the footer height.
	 * An implementation must ensure that getFooterHeight()=getFooter().size() for all pagenum's
	 * @return returns the footer height
	 */
	public int getFooterHeight();

	/**
	 * Get header rows for a page using this Template. Each ArrayList must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each ArrayList must
	 * be smaller than the flow width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @param page the page to get the header for
	 * @return returns an ArrayList containing an ArrayList of String
	 */
	public ArrayList<ArrayList<Object>> getHeader();
	
	/**
	 * Get footer rows for a page using this Template. Each ArrayList must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each ArrayList must
	 * be smaller than the flow width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @param page the page to get the header for
	 * @return returns an ArrayList containing an ArrayList of String
	 */
	public ArrayList<ArrayList<Object>> getFooter();
	
	/**
	 * Test if this Template applies to this pagenum.
	 * @param pagenum the pagenum to test
	 * @return returns true if the Template should be applied to the page
	 */
	public boolean appliesTo(int pagenum);

}
