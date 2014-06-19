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

import com.hitsujun.core.util.Pair;
import com.hitsujun.core.util.svg.SvgParser;


public class KanjiStrokesLoaderFromSvgImpl implements KanjiStrokesLoader
{
	private final String kanji ;
	
	public KanjiStrokesLoaderFromSvgImpl (String kanji)
	{
		super () ;
		this.kanji = kanji ;
	}
	
	
	private final String getSvgFileName (String kanji)
	{
		// Note: String.format is not supported by GWT
		StringBuilder svgFileNameBuilder = new StringBuilder ();
		svgFileNameBuilder.append("images/kanji/0") ;
		svgFileNameBuilder.append(Integer.toHexString(kanji.charAt(0) | 0x10000).substring(1)) ;
		svgFileNameBuilder.append(".svg") ;
		return svgFileNameBuilder.toString() ;
	}
	

	@Override
	public void load(final Callback<Pair<Pair<Float, Float>, List<String> > > callback) 
	{
		String svgFileName = getSvgFileName (kanji) ;
		assets().getText(svgFileName, new Callback<String>(){

			@Override
			public void onSuccess(String result) {
				
				if (result == null)
					return ;
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
