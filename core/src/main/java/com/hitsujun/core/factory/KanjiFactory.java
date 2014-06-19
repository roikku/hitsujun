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

import playn.core.util.Callback;

import com.hitsujun.core.controller.KanjiController;
import com.hitsujun.core.gui.KanjiView;
import com.hitsujun.core.model.Kanji;
import com.hitsujun.core.model.KanjiInfo;
import com.hitsujun.core.util.Triplet;

public interface KanjiFactory {
	public void getKanji (String kanjiStr, int level, Callback <Triplet<Kanji, KanjiView, KanjiController> > callback) ;
	public void getKanji (String kanjiStr, Callback <Triplet<Kanji, KanjiView, KanjiController> > callback) ;
	public void getKanjiInfo (String kanjiStr, int level, Callback <KanjiInfo> callback) ;
}
