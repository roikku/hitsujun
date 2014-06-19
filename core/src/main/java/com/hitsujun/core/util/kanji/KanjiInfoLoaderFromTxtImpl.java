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

import com.hitsujun.core.util.InvalidFileFormatException;

import playn.core.util.Callback;

public class KanjiInfoLoaderFromTxtImpl implements KanjiInfoLoader {

	private final int level ;
	private final String kanji ;
	private final String prefix = "json/kanji/" ;

	
	public KanjiInfoLoaderFromTxtImpl (int level, String kanji)
	{
		super () ;
		this.level = level ;
		this.kanji = kanji ;
	}

	
	@Override
	public void load(final Callback<String> callback) {
		final String jsonFileName = AssetsUtils.getInfoTxtFilePath(level, kanji, prefix) ;
		assets().getText(jsonFileName, new Callback<String>(){

			@Override
			public void onSuccess(String result) {
				
				if (result == null)
					return ;

				final String begin = kanji + "{\"literal\"" ;
				String end = "}" + "\n" ;
				
				int indexBegin = result.indexOf(begin) ;
				if (indexBegin < 0)
					throw new InvalidFileFormatException ("Invalid format (failed to determine the beginning of the kanji description): " + jsonFileName + " (kanji: " + kanji + ")") ;
				int indexEnd = result.indexOf(end, indexBegin) ;
				// GWT does not know about System.lineSeparator()...
				if (indexEnd < 0)
				{
					end = "}" + "\r\n" ; // this should do it on Windows-based systems
					indexEnd = result.indexOf(end, indexBegin) ;
				}
				if (indexEnd < 0)
					throw new InvalidFileFormatException ("Invalid format (failed to determine the end of the kanji description): " + jsonFileName + " (kanji: " + kanji + ")") ;
				
				result = result.substring(indexBegin + kanji.length(), indexEnd + end.length()) ;
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				callback.onFailure(cause) ;
			}}) ;
	}
}
