/**
 * Copyright 2014 Loic Merckel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hitsujun.core.util.svg;

import java.util.ArrayList;
import java.util.List;

import com.hitsujun.core.util.Pair;

public class SvgParser {

	private final String svg ;
 
	private final String pathStart = "<path " ;
	private final String pathDefStart = " d=\"" ;
	private final String pathDefEnd = "\"" ;
	private final String viewBox = " viewBox=\"" ;
	private final String viewBoxEnd = "\"" ;
	
	private final List<String> paths = new ArrayList<String> () ;
	private Float width = null ;
	private Float height = null ;
	
	
	public SvgParser (String svg)
	{
		super () ;
		if (svg == null)
			throw new NullPointerException () ;
		this.svg = svg ;
	}
	
	
	public final List<String> getPaths ()
	{
		if (!paths.isEmpty())
			return new ArrayList<String> (paths) ;
		String svgStr = svg ;
		int index =  svgStr.indexOf(pathStart) ;
		while (index >= 0)
		{
			svgStr = svgStr.substring(index + pathStart.length()) ;
			index = svgStr.indexOf(pathDefStart) ;
			if (index >= 0)
			{
				svgStr = svgStr.substring(index + pathDefStart.length()) ;
				index = svgStr.indexOf(pathDefEnd) ;
				if (index >= 0)
				{
					String path = svgStr.substring(0, index) ;					
					paths.add (path) ;
					svgStr = svgStr.substring(index + pathDefEnd.length()) ;
				}
			}
			index =  svgStr.indexOf(pathStart) ;
		}
		return new ArrayList<String> (paths) ;
	}
	
	
	public final Pair<Float, Float> getWidthHeight (float defaultWidth, float defaultHeight)
	{
		if (width != null && height != null)
			return Pair.newPair(width, height) ;
		int indexBegin =  svg.indexOf(viewBox) ;
		int indexEnd =  svg.indexOf(viewBoxEnd, indexBegin) ;
		if (indexBegin < 0 || indexEnd < 0)
			return Pair.newPair(Float.valueOf(defaultWidth), Float.valueOf(defaultHeight)) ;
		String str = svg.substring(indexBegin, indexEnd) ;
		String [] splitted = ((str.contains(",")) ? (str.split(",")) : (str.split(" ")));
		if (splitted.length < 4)
			return Pair.newPair(Float.valueOf(defaultWidth), Float.valueOf(defaultHeight)) ;
		width = Float.valueOf(splitted[2].trim()) ;
		height = Float.valueOf(splitted[3].trim()) ;
		return Pair.newPair(width, height) ;
	}
}
