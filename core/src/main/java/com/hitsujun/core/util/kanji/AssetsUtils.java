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

package com.hitsujun.core.util.kanji;

public class AssetsUtils {
	
	private AssetsUtils () { super () ; throw new UnsupportedOperationException () ; } ;
	
	private static final int [] NUM_OF_FILE_PER_LEVEL = {50, 100, 150, 200, 300} ;
	
	
	public static String getTxtFileName (int level, String kanji)
	{
		StringBuilder svgFileNameBuilder = new StringBuilder ();
		svgFileNameBuilder.append(level + 1) ;
		svgFileNameBuilder.append("_") ;
		svgFileNameBuilder.append(Integer.valueOf(kanji.charAt(0)) % NUM_OF_FILE_PER_LEVEL[level]) ;
		svgFileNameBuilder.append(".txt") ;
		return svgFileNameBuilder.toString() ;
	}
	
	
	public static String getInfoTxtFilePath (int level, String kanji, String prefix)
	{
		// Note: String.format is not supported by GWT
		StringBuilder svgFileNameBuilder = new StringBuilder ();
		svgFileNameBuilder.append(prefix) ;
		svgFileNameBuilder.append(getTxtFileName(level, kanji)) ;
		return svgFileNameBuilder.toString() ;
	}
}
