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

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.Platform.Type;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Clock;
import react.UnitSlot;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;

import com.hitsujun.core.model.HitsujunGame;

public class HitsujunGameScreen extends HitsujunScreen {

	private GroupLayer base;
	private volatile KanjiView kanjiView = null ;
	private GroupLayer kanjiLayer ;
	
	private HitsujunGame game ;
	
	private final int depthTitle = 10 ;
	private final int depthMessage = 20 ;
	private final int depthKanji = 30 ;
	
	private Layer layerScore = null ;
	private Layer layerMessage = null ;
	private Group menu = null ;
			
	private Root root ;
	
	public HitsujunGameScreen (HitsujunGame game)
	{
		super ();
		if (game == null)
			throw new NullPointerException () ;
		this.game = game ;
	}
	
	
	private void init() {

		root = iface.createRoot(AxisLayout.vertical().offStretch(), SimpleStyles.newSheet())
				.setBounds(0, 0, graphics().width(), graphics().height());
		root.setSize(graphics().width(), graphics().height());
		layer.add(root.layer);
		
		base = layer ;	
		
		displayBackground () ;
		displayTextScore ("Score: 0") ;

		kanjiLayer = graphics().createGroupLayer();
		kanjiLayer.setDepth(depthKanji) ;
		base.add(kanjiLayer);
	}
	
	
	private void displayBackground ()
	{
		root.addStyles(Style.BACKGROUND.is(Background.image(assets().getImage("images/hitsujun_bg.png"))
				.inset(0)));
	}
	
	
	public interface MenuCallback 
	{
		public void onKanjiInfo () ;
	}
	
	
	public void createMenu (final MenuCallback callback)
	{
		// http://threerings.github.io/tripleplay/apidocs/tripleplay/ui/layout/AbsoluteLayout.html
		final int buttonWidth = 120 ;
		final int buttonHeight = 30 ;
		final int marginBottom = 20 ;
		
		Button buttonKanjiInfo = new Button("Kanji Info");
		buttonKanjiInfo.clicked().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				if (callback != null)
					callback.onKanjiInfo();
			}
		});
		
		menu = new Group(new AbsoluteLayout()).add(
			     AbsoluteLayout.at(buttonKanjiInfo, graphics().width() / 2 - buttonWidth / 2, graphics().height() - (buttonHeight + marginBottom) , buttonWidth, buttonHeight)
			 );
		
		root.add(menu) ;
	}
	
	
	public void showMenu (boolean val)
	{
		if (menu != null)
			menu.setVisible(val) ;
	}
	
	
	public void enableMenu (boolean val)
	{
		if (menu != null)
			menu.setEnabled(val) ;
	}
	
	
	private void displayTextScore (String text)
	{
		float xpos = graphics().width() / 2f; 
		float ypos = 15; 
		Font font = graphics().createFont("Helvetica", Font.Style.PLAIN, 25);
		TextFormat fmt = new TextFormat().withFont(font);
		TextLayout layout = graphics().layoutText(text, fmt);
		if (layerScore != null)
			base.remove(layerScore) ;
		layerScore = createTextLayer(layout, 0xFF000000);
		layerScore.setOrigin(layout.width() / 2f, layout.height() / 2f);
		layerScore.setTranslation(xpos, ypos);
		layerScore.setDepth(depthTitle) ;
		base.add(layerScore);
		
	}
	
	
	private void displayTextMessage (String text)
	{
		if (layerMessage != null)
			base.remove(layerMessage) ;
		layerMessage = null ;
		if (text == null || text.isEmpty())
			return ;
		
		float xpos = graphics().width() / 2f; 
		float ypos = 50; 
		
		Font font = graphics().createFont("Helvetica", Font.Style.PLAIN, 25);
		TextFormat fmt = new TextFormat().withFont(font);
		TextLayout layout = graphics().layoutText(text, fmt);
	
		if (layerMessage != null)
			base.remove(layerMessage) ;
		layerMessage = createTextLayer(layout, HitsujunColorConstants.FONT_MESSAGE_COLOR);
		layerMessage.setOrigin(layout.width() / 2f, layout.height() / 2f);
		layerMessage.setTranslation(xpos, ypos);
		layerMessage.setDepth(depthMessage) ;
		base.add(layerMessage);
	}
	
	
	public void displayScore ()
	{
		displayTextScore ("Score: " + game.getScore()) ;
	}
	
	
	public void setKanjiView (KanjiView kanjiView)
	{
		if (this.kanjiView != null)
			this.kanjiView.clearAll () ;
		this.kanjiView = kanjiView ;
		if (this.kanjiView != null)
			this.kanjiView.setGroupLayer(kanjiLayer);
		displayScore () ;
	}
	
	
    @Override 
    public void wasAdded () {
        super.wasAdded();
        init() ;
    }
    
    
    @Override 
    public void wasRemoved () {
		if (iface != null) {
			PlayN.pointer().setListener(null);
			PlayN.keyboard().setListener(null);
			iface.destroyRoots() ;
		}
		if (kanjiLayer != null)
		{
			kanjiLayer.destroyAll() ;
			kanjiLayer = null ;
		}
		kanjiView = null ;
		menu = null ;
    }
	
	
	protected Layer createTextLayer(TextLayout layout, int color) {
		CanvasImage image = graphics().createImage(
				(int) Math.ceil(layout.width()),
				(int) Math.ceil(layout.height()));
		image.canvas().setFillColor(color);
		image.canvas().fillText(layout, 0, 0);
		return graphics().createImageLayer(image);
	}
	

	@Override
	public void update(int delta) {
		
		if (kanjiView != null)
			kanjiView.update(delta);
	}
	
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock) ;
		if (kanjiView != null)
			kanjiView.paint(clock.dt());
	}

	
	public void showMessage(String string) {
		displayTextMessage (string) ;
	}
	

	public void drawKanji(DrawingStatus drawingStatus) {
		if (kanjiView != null)
			kanjiView.draw(drawingStatus);
	}
	
	
	public void drawExpectedStroke(DrawingStatus drawingStatus) {
		if (kanjiView != null)
			kanjiView.drawExpectedStroke(drawingStatus);
	}
	
	
	public void hideKanji() {
		if (kanjiView != null)
			kanjiView.hide();
	}

	
	public void drawKanjiBackground() {
		if (kanjiView != null)
			kanjiView.showBackgroung();
	}


	@Override
	public void wasShown() {
		super.wasShown();
		
		PlayN.platformType();
		if (PlayN.platformType() == Type.ANDROID) {
			drawKanjiBackground() ;
			if (kanjiView != null)
				kanjiView.showCurrentStatus(HitsujunColorConstants.KANJI_USER_DRAWN_STROKE_COLOR);
		}
	}
}
