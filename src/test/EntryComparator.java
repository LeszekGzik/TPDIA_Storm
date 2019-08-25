package test;

import java.util.Comparator;

import model.PSRCATEntry;

public class EntryComparator implements Comparator<PSRCATEntry> {

	@Override
	public int compare(PSRCATEntry o1, PSRCATEntry o2) {
		
		if(o1.getRefSep() > o2.getRefSep()) {
			return 0;
		}
		if(o1.getRefSep() < o2.getRefSep()) {
			return -1;
		}else
			return 0;
	}

}
