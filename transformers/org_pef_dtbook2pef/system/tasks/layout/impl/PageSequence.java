package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.flow.Row;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;


public class PageSequence {
	private Stack<Page> pages;
	private LayoutMaster master;
	private int pagesOffset;
	
	public PageSequence(LayoutMaster master, int pagesOffset) {
		this.pages = new Stack<Page>();
		this.master = master;
		this.pagesOffset = pagesOffset;
	}
	
	public Stack<Page> getPages() {
		return pages;
	}

	public int rowsOnCurrentPage() {
		return currentPage().rowsOnPage();
	}
	
	public void newPage() {
		pages.push(new Page(this, pages.size()+pagesOffset));
	}
	
	public Page currentPage() {
		return pages.peek();
	}
	
	public void newRow(Row row) {
		//TODO: clean
		//Template t = master.getTemplate(currentPage().getPageIndex()+1);
		//master.getPageHeight()-t.getHeaderHeight()-t.getFooterHeight()
		if (currentPage().rowsOnPage()>=currentPage().getFlowHeight()) {
			newPage();
		}
		currentPage().newRow(row);
	}
	
	public LayoutMaster getLayoutMaster() {
		return master;
	}

}
