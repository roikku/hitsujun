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

package com.hitsujun.core.gui;

import static playn.core.PlayN.graphics;
import playn.core.Color;
import playn.core.Font;
import playn.core.PlayN;
import react.UnitSlot;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Scroller;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;

import com.hitsujun.core.HitsujunScreenActivator;
import com.hitsujun.core.controller.HasActionListener;
import com.hitsujun.core.model.KanjiInfo;

public class HitsujunKanjiInfoPopupScreen extends HitsujunPopupScreen {

	private final KanjiInfo kanjiInfo ;
	private final HitsujunScreenActivator hitsujunScreenActivator ;
	
	public HitsujunKanjiInfoPopupScreen (KanjiInfo kanjiInfo, HitsujunScreenActivator hitsujunScreenActivator)
	{
		super () ;
		if (kanjiInfo == null || hitsujunScreenActivator == null)
			throw new NullPointerException () ;
		this.hitsujunScreenActivator = hitsujunScreenActivator ;
		this.kanjiInfo = kanjiInfo ;
	}
	
	
	public HitsujunKanjiInfoPopupScreen (KanjiInfo kanjiInfo, HitsujunScreenActivator hitsujunScreenActivator, HasActionListener actionListener)
	{
		this (kanjiInfo, hitsujunScreenActivator) ;
		this.actionListener = actionListener ;
	}
	
	
	private void init() 
	{
		Root root = iface.createRoot(AxisLayout.vertical().gap(15),
				SimpleStyles.newSheet());
		root.setSize(graphics().width(), graphics().height());
		root.addStyles(Style.BACKGROUND.is(Background.solid(HitsujunColorConstants.MENU_BACKGROUND_COLOR)
				.inset(5)));
		layer.add(root.layer);
		
        Font valueFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, UiUtils.scaleSize (24));
        Styles valueStyles = Styles.make(Style.FONT.is(valueFont),
                                     Style.TEXT_EFFECT.is(Style.TextEffect.NONE), 
                                     Style.COLOR.is(Color.rgb(240, 10, 10)));

        Font kanjiFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, UiUtils.scaleSize (64));
        Styles kanjiStyle = Styles.make(Style.FONT.is(kanjiFont),
                Style.TEXT_EFFECT.is(Style.TextEffect.GRADIENT), 
                Style.COLOR.is(HitsujunColorConstants.FONT_TITLE_COLOR));
        
        Font keyFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, UiUtils.scaleSize (20));
        Styles keyStyle = Styles.make(Style.FONT.is(keyFont),
                Style.TEXT_EFFECT.is(Style.TextEffect.NONE), 
                Style.COLOR.is(Color.rgb(90, 90, 90)));
        
        //final String sep = "; " ;

        String kanji = kanjiInfo.getKanji() ;
        
		Group buttons;
		Group tableBasicInfo;
		Scroller scroll ;
		root.add(UiUtils.getEmptyLabel ());
		root.add(new Label(kanji).setStyles(kanjiStyle));
		root.add (scroll = new Scroller(tableBasicInfo = new Group(new TableLayout(TableLayout.COL.alignRight(), TableLayout.COL.alignLeft()).gaps(5, 15)).setConstraint(AxisLayout.stretched()))) ;
		root.add (buttons = new Group(AxisLayout.vertical().offStretch())) ;
		root.add(UiUtils.getEmptyLabel ());
		
		scroll.setConstraint(AxisLayout.stretched()) ;
		
		boolean added = false ;
		added = UiUtils.addToTableGroup (tableBasicInfo, "kun-yomi", keyStyle, kanjiInfo.getKunYomiReadings(), valueStyles/*, sep*/) || added ;
		added = UiUtils.addToTableGroup (tableBasicInfo, "on-yomi", keyStyle, kanjiInfo.getOnYomiReadings(), valueStyles/*, sep*/) || added ;
		added = UiUtils.addToTableGroup (tableBasicInfo, "Meaning", keyStyle, kanjiInfo.getMeaning(), valueStyles/*, sep*/) || added ;
		if (kanjiInfo.getStrokeCountList() != null && !kanjiInfo.getStrokeCountList().isEmpty())
			added = UiUtils.addToTableGroup (tableBasicInfo, "stroke count", keyStyle, kanjiInfo.getStrokeCountList().get(0), valueStyles/*, sep*/) || added ;
		
		if (!added)
		{
			root.add(UiUtils.getEmptyLabel ());
			root.add(new Label("There is no further information about this kanji").setStyles(keyStyle));
		}
		
		//Button button = new Button("Back");
		Button button = UiUtils.getButton("Back");
		buttons.add(button);
		button.clicked().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				hitsujunScreenActivator.hidePopupScreen();
			}
		});
	}
		

    @Override 
    public void wasAdded () {
        super.wasAdded();
        init() ;
    }
    
    
    @Override 
    public void wasRemoved () {
		if (iface != null) {
			iface.destroyRoots() ;
		}
    }
}
