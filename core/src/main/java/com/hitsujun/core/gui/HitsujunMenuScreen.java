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

/* 
 * This file includes some code from the PlayN Sample Projects (http://code.google.com/p/playn/), 
 * which is distributed under the Apache v2 license.
 * 
 * Copyright 2011 The ForPlay Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * More precisely, the following file were used:
 * https://code.google.com/p/playn-samples/source/browse/showcase/core/src/main/java/playn/showcase/core/Menu.java
 */

package com.hitsujun.core.gui;

import com.hitsujun.core.HitsujunScreenActivator;
import com.hitsujun.core.model.HitsujunGame;

import playn.core.Font;
import playn.core.PlayN;
import react.UnitSlot;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AxisLayout;
import static playn.core.PlayN.*;

public class HitsujunMenuScreen extends HitsujunScreen {

	private final HitsujunScreenActivator hitsujunScreenActivator ;

	public HitsujunMenuScreen (HitsujunScreenActivator hitsujunScreenActivator)
	{
		super () ;
		this.hitsujunScreenActivator = hitsujunScreenActivator ;
	}
	
	
	private void init() 
	{
		Root root = iface.createRoot(AxisLayout.vertical().gap(15),
				SimpleStyles.newSheet());
		root.setSize(graphics().width(), graphics().height());
		root.addStyles(Style.BACKGROUND.is(Background.solid(HitsujunColorConstants.MENU_BACKGROUND_COLOR)
				.inset(5)));
		layer.add(root.layer);
		
        Font titleFont = PlayN.graphics().createFont("Helvetica", Font.Style.BOLD, UiUtils.scaleSize (36));
        Styles tstyles = Styles.make(Style.FONT.is(titleFont),
                                     Style.TEXT_EFFECT.is(Style.TextEffect.GRADIENT), 
                                     Style.COLOR.is(HitsujunColorConstants.FONT_TITLE_COLOR));

        Font labelFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, UiUtils.scaleSize (16));
        Styles labelStyle = Styles.make(Style.FONT.is(labelFont));
        
        root.add(new Shim(1,1).setConstraint(AxisLayout.stretched(0.5f))) ;
        
		Group buttons;
		root.add(/*new Label("UNDER CONSTRUCTION"),*/
				new Label("hitsujun").setStyles(tstyles), 
				buttons = new Group(AxisLayout.vertical().offStretch()), 
				new Label("ESC/BACK key or two-finger tap returns to menu from game").addStyles(labelStyle));

		int key = 1;
		for (int i = 0 ; i < HitsujunGame.NUMBER_OF_LEVEL ; ++i) {
			//Button button = new Button("Level " + key++);
			Button button = UiUtils.getButton("Level " + key++) ;
			buttons.add(button);
			final int screenLevel = i ;
			button.clicked().connect(new UnitSlot() {
				@Override
				public void onEmit() {
					hitsujunScreenActivator.activateGameScreen(screenLevel);
				}
			});
		}
		
		// add the link
		root.add(new Shim(1,1).setConstraint(AxisLayout.stretched(0.4f))) ;
		root.add(getLinkButton()) ;
		root.add(new Shim(1,1).setConstraint(AxisLayout.stretched(0.1f))) ;
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
    
    
	private Button getLinkButton ()
	{
	    Font buttonFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, UiUtils.scaleSize (16));
	    Styles buttonStyle = Styles.make(Style.FONT.is(buttonFont));
	    
		Button buttonKanjiInfo = new Button("www.hitsujun.org");
		buttonKanjiInfo.addStyles(buttonStyle) ;
		buttonKanjiInfo.clicked().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				PlayN.openURL("http://www.hitsujun.org");
		}});
		
		buttonKanjiInfo.addStyles(Styles.make(
				Style.UNDERLINE.on, 
				Style.COLOR.is(HitsujunColorConstants.FONT_LINK_COLOR), 
				Style.BACKGROUND.is(Background.solid(HitsujunColorConstants.MENU_BACKGROUND_COLOR)))) ;
		
		return buttonKanjiInfo ;
	}
}
