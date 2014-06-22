package com.hitsujun.core.gui;

import java.util.List;

import playn.core.Font;
import playn.core.PlayN;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AxisLayout;

public class UiUtils {
	
	private UiUtils () { super () ; throw new UnsupportedOperationException () ; } 

	
	public static float scaleSize (float size)
	{
		return size ;//* graphics().scaleFactor() ;
	}
	
	
    private static Font buttonFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, UiUtils.scaleSize (18));
    private static Styles buttonStyle = Styles.make(Style.FONT.is(buttonFont));
	
    
	public static Button getButton (String txt)
	{
		Button button = new Button(txt);
		button.addStyles(buttonStyle) ;
		return button ;
	}
	
	
	public static Label getEmptyLabel ()
	{
		return new Label(" ").setStyles(Styles.make(Style.FONT.is(PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, UiUtils.scaleSize (10))))) ;
	}
	
	
	public static <T> boolean addToTableGroup (Group table, String desc, Styles styleDesc, List<T> list, Styles styleVal)
	{
		if (list == null)
			return false ;
		boolean first = true ;
    	for (T str : list) {
    		table.add(new Label (((first)?(desc):(" "))).setStyles(styleDesc), new Label (str.toString()).setStyles(styleVal)) ;
    		first = false ;
    	}
    	return !first ;
	}
	
	
	public static <T> boolean addToTableGroup (Group table, String desc, Styles styleDesc, T value, Styles styleVal)
	{
		if (value == null)
			return false;
    	table.add(new Label (desc).setStyles(styleDesc), new Label (value.toString()).setStyles(styleVal)) ;
    	return true ;
	}
	
	
	public static <T> boolean addToTableGroup (Group table, String desc, Styles styleDesc, List<T> list, Styles styleVal, String sep)
	{
		String lstr = listToString(list, sep) ;
		if (lstr == null || lstr.isEmpty())
			return false ;
		table.add(new Label (desc).setStyles(styleDesc), new Label (lstr).setStyles(styleVal)) ;
		return true ;
	}
	
	
	public static <T> String listToString (List<T> list, String sep)
	{
		if (list == null || list.isEmpty())
			return null ;
    	int size = list.size() ;
    	StringBuilder sb = new StringBuilder () ;
    	int count = 0 ;
    	for (T str : list) {
    		sb.append (str.toString()) ;
    		if (count < size - 1)
    			sb.append (sep) ;
    		++count ;
    	}
    	return sb.toString() ;
	}
	

	public static Group getLabelGroup (String str1, Styles style1, String str2, Styles style2)
	{
		return new Group(AxisLayout.horizontal().offStretch()).add(
				new Label (str1).setStyles(style1), 
				new Label (str2).setStyles(style2)) ;
	}
}
