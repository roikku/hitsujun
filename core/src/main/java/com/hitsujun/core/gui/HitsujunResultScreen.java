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
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;

import com.hitsujun.core.HitsujunScreenActivator;

public class HitsujunResultScreen extends HitsujunScreen {

	private final HitsujunScreenActivator hitsujunScreenActivator ;
	private boolean hasWon = false ;
	private int level = 1 ;
	private int score = 0 ;
	private float percentageAchieved = 0f ;

	
	public HitsujunResultScreen (HitsujunScreenActivator hitsujunScreenActivator)
	{
		super () ;
		this.hitsujunScreenActivator = hitsujunScreenActivator ;	
	}
	
	
	public HitsujunResultScreen setLevel (int level)
	{
		this.level = level ;
		return this ;
	}
	
	
	public HitsujunResultScreen setHasWon (boolean hasWon)
	{
		this.hasWon = hasWon ;
		return this ;
	}
	
	
	public HitsujunResultScreen setScore (int score)
	{
		this.score = score ;
		return this ;
	}
	
	
	public HitsujunResultScreen setPercentageAchieved (float percentageAchieved)
	{
		this.percentageAchieved = percentageAchieved ;
		return this ;
	}
	
	
	private void init() 
	{
		Root root = iface.createRoot(AxisLayout.vertical().gap(15),
				SimpleStyles.newSheet());
		root.setSize(graphics().width(), graphics().height());
		root.addStyles(Style.BACKGROUND.is(Background.solid(HitsujunColorConstants.RESULT_BACKGROUND_COLOR)
				.inset(5)));
		layer.add(root.layer);
		
        Font font = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, 32);
        Styles tstyles = Styles.make(Style.FONT.is(font),
                                     Style.TEXT_EFFECT.is(Style.TextEffect.GRADIENT), Style.COLOR.is(HitsujunColorConstants.FONT_TITLE_COLOR));

        Font valueFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, 24);
        Styles valueStyles = Styles.make(Style.FONT.is(valueFont),
                                     Style.TEXT_EFFECT.is(Style.TextEffect.NONE), 
                                     Style.COLOR.is(Color.rgb(240, 10, 10)));
        
        Font keyFont = PlayN.graphics().createFont("Helvetica", Font.Style.PLAIN, 20);
        Styles keyStyle = Styles.make(Style.FONT.is(keyFont),
                Style.TEXT_EFFECT.is(Style.TextEffect.NONE), 
                Style.COLOR.is(Color.rgb(90, 90, 90)));
         
		Group buttons;
		Group tableBasicInfo;		
		root.add(new Label((hasWon)?("Congratulations"):("Game Over")).setStyles(tstyles), 
				UiUtils.getEmptyLabel (),
				tableBasicInfo = new Group(new TableLayout(TableLayout.COL.alignRight(), TableLayout.COL.alignLeft()).gaps(5, 15)),
				UiUtils.getEmptyLabel () ,
				buttons = new Group(AxisLayout.vertical().offStretch()), 
				UiUtils.getEmptyLabel ());
		
		UiUtils.addToTableGroup (tableBasicInfo, "Level", keyStyle, String.valueOf(level), valueStyles) ;
		UiUtils.addToTableGroup (tableBasicInfo, "Score", keyStyle, String.valueOf(score), valueStyles) ;
		UiUtils.addToTableGroup (tableBasicInfo, "Achieved", keyStyle, String.valueOf(Math.floor(percentageAchieved * 100f) / 100f) + " %", valueStyles) ;
		
		Button button = new Button("Main Menu");
		buttons.add(button);
		button.clicked().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				hitsujunScreenActivator.activateMenuScreen();
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
