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

package com.hitsujun.core.factory;

import playn.core.PlayN;
import playn.core.util.Callback;

import com.hitsujun.core.controller.KanjiController;
import com.hitsujun.core.gui.KanjiView;
import com.hitsujun.core.model.Kanji;
import com.hitsujun.core.model.KanjiInfo;
import com.hitsujun.core.util.Triplet;
import com.hitsujun.core.util.kanji.KanjiInfoLoader;
import com.hitsujun.core.util.kanji.KanjiInfoLoaderFromTxtImpl;
import com.hitsujun.core.util.kanji.KanjiStrokesLoader;
import com.hitsujun.core.util.kanji.KanjiStrokesLoaderFromSvgImpl;
import com.hitsujun.core.util.kanji.KanjiStrokesLoaderFromTxtImpl;

public class KanjiFactoryImpl implements KanjiFactory{

	private final float x ;
	private final float y ;
	private final float width ;
	private final float height ;
	
	public KanjiFactoryImpl(float x, float y, float width, float height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}


	@Override
	public void getKanji(String kanjiStr, int level, final Callback<Triplet<Kanji, KanjiView, KanjiController> > callback) {
		final Kanji kanji = new Kanji.Builder (kanjiStr).setX(x).setY(y).setWidth(width).setHeight(height).build() ;
		KanjiStrokesLoader loader = new KanjiStrokesLoaderFromTxtImpl (level, kanjiStr) ;
		kanji.init (loader, new Callback<Void> () {
			@Override
			public void onSuccess(Void result) {
				KanjiView kv = new KanjiView (kanji) ;
				callback.onSuccess(Triplet.newTriplet(kanji, kv, new KanjiController (kanji, kv)));
			}

			@Override
			public void onFailure(Throwable cause) {
				PlayN.log().error("Error occurred while initializing the kanji " + kanji.getKanji(), cause) ;
				callback.onFailure(cause);
			}}) ;
	}


	@Override
	public void getKanji(String kanjiStr, final Callback<Triplet<Kanji, KanjiView, KanjiController> > callback) {
		final Kanji kanji = new Kanji.Builder (kanjiStr).setX(x).setY(y).setWidth(width).setHeight(height).build() ;
		KanjiStrokesLoader loader = new KanjiStrokesLoaderFromSvgImpl (kanjiStr) ;
		kanji.init (loader, new Callback<Void> () {
			@Override
			public void onSuccess(Void result) {
				KanjiView kv = new KanjiView (kanji) ;
				callback.onSuccess(Triplet.newTriplet(kanji, kv, new KanjiController (kanji, kv)));
			}

			@Override
			public void onFailure(Throwable cause) {
				PlayN.log().error("Error occurred while initializing the kanji " + kanji.getKanji(), cause) ;
				callback.onFailure(cause);
			}}) ;
	}


	@Override
	public void getKanjiInfo(final String kanjiStr, int level, final Callback<KanjiInfo> callback) {
		KanjiInfoLoader loader = new KanjiInfoLoaderFromTxtImpl (level, kanjiStr) ;
		loader.load(new Callback<String> () {

			@Override
			public void onSuccess(String result) {
				if (result == null || result.isEmpty())
					throw new IllegalStateException ("The JSON description of a kanji cannot be null or empty") ;
				callback.onSuccess(new KanjiInfo (result));
			}

			@Override
			public void onFailure(Throwable cause) {
				PlayN.log().error("Error occurred while getting the kanji's info" + kanjiStr, cause) ;
				callback.onFailure(cause);
			}});
	}
}
