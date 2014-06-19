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

import static playn.core.PlayN.assets;

import java.util.List;

import playn.core.util.Callback;

import com.hitsujun.core.util.InvalidFileFormatException;
import com.hitsujun.core.util.Pair;
import com.hitsujun.core.util.svg.SvgParser;

public class KanjiStrokesLoaderFromTxtImpl implements KanjiStrokesLoader
{
	private final int level ;
	private final String kanji ;
	private final String prefix = "images/kanji/" ;
	
	
	public KanjiStrokesLoaderFromTxtImpl (int level, String kanji)
	{
		super () ;
		this.level = level ;
		this.kanji = kanji ;
	}
	

	@Override
	public void load(final Callback<Pair<Pair<Float, Float>, List<String> > > callback) 
	{
		final String svgFileName = AssetsUtils.getInfoTxtFilePath(level, kanji, prefix) ;
		assets().getText(svgFileName, new Callback<String>(){

			@Override
			public void onSuccess(String result) {
				
				if (result == null)
					return ;
				
				final String begin = kanji + "<svg " ;
				final String end = "</svg>" ;
				
				int indexBegin = result.indexOf(begin) ;
				if (indexBegin < 0)
					throw new InvalidFileFormatException ("Invalid format (failed to determine the beginning of the svg description): " + svgFileName + " (kanji: " + kanji + ")") ;
				int indexEnd = result.indexOf(end, indexBegin) ;
				if (indexEnd < 0)
					throw new InvalidFileFormatException ("Invalid format (failed to determine the end of the svg description): " + svgFileName + " (kanji: " + kanji + ")") ;
				
				result = result.substring(indexBegin + kanji.length(), indexEnd + end.length()) ;
				
				SvgParser svgParser = new SvgParser (result) ;
				final Pair<Float, Float> widthHeight = svgParser.getWidthHeight (109, 109) ;
				final List<String> paths = svgParser.getPaths() ;
				callback.onSuccess(Pair.newPair(widthHeight, paths)) ;
			}

			@Override
			public void onFailure(Throwable cause) {
				callback.onFailure(cause) ;
			}}) ;
	}
}
