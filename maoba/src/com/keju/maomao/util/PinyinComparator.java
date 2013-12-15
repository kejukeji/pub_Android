package com.keju.maomao.util;

import java.util.Comparator;

import com.keju.maomao.bean.SortModelBean;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<SortModelBean> {

	public int compare(SortModelBean o1, SortModelBean o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("热门")) {
			return -1;
		} else if (o1.getSortLetters().equals("热门")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
