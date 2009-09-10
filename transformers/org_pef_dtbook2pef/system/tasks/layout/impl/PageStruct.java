package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.PageSequence;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;

public class PageStruct {
	private Stack<PageSequence> sequence;
	private HashMap<String, LayoutMaster> templates;

	public PageStruct(HashMap<String, LayoutMaster> templates) {
		this.sequence = new Stack<PageSequence>();
		this.templates = templates;
	}

	public void newSection(String masterName, LayoutMaster master) {
		sequence.push(new PageSequence(templates.get(masterName)));
	}
	
	private PageSequence currentSequence() {
		return sequence.peek();
	}

	public void newPage() {
		currentSequence().newPage();
	}
	
	public void newRow(Row row) {
		currentSequence().newRow(row);
	}
	
	public int countRowsOnCurrentPage() {
		return currentSequence().rowsOnCurrentPage();
	}
	
	public void insertMarkers(ArrayList<Marker> m) {
		currentSequence().currentPage().addMarkers(m);
	}
	
	public LayoutMaster getCurrentLayoutMaster() {
		return currentSequence().getLayoutMaster();
	}

	public PageSequence[] getSequenceArray() {
		PageSequence[] ret = new PageSequence[sequence.size()];
		return sequence.toArray(ret);
	}

}