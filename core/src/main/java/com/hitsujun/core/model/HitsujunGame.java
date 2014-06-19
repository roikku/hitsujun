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

package com.hitsujun.core.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import playn.core.util.Callback;

import com.hitsujun.core.controller.KanjiController;
import com.hitsujun.core.factory.KanjiFactory;
import com.hitsujun.core.gui.KanjiView;
import com.hitsujun.core.util.Triplet;

public class HitsujunGame {

	public static final int NUMBER_OF_LEVEL = 5 ;
		
	static interface GameContext
	{
		public int getLevel () ;
		public Kanji getKanji () ;
		public void setKanji (Kanji kanji) ;
		public int getScore () ;
		public void incrementScore () ;
		public void start (int level) ;
		public boolean isPlaying () ;
		public void gameOver (boolean hasWon) ;
		public boolean hasWon () ;
	}
	
	
	static interface GameTurnState
	{
		public void go (GameContext gc) ;
	}
	
	
	private static class HitsujunGameContext implements GameContext
	{
		private int level = 0 ;
		private Kanji kanji ;
		private volatile boolean isPlaying = false ;
		private int score = -1 ;
		private boolean hasWon = false ;
		
		public HitsujunGameContext ()
		{
			super () ;
		}
		
		@Override
		public synchronized int getLevel() {
			return level;
		}

		@Override
		public synchronized Kanji getKanji() {
			return kanji;
		}

		@Override
		public synchronized boolean isPlaying() {
			return isPlaying;
		}

		@Override
		public synchronized int getScore() {
			return score;
		}

		@Override
		public synchronized void start(int level) {
			if (level < 0 || level >= NUMBER_OF_LEVEL)
				throw new IllegalArgumentException () ;
			this.level = level ;
			isPlaying = true ;
			hasWon = false ;
			score = -1 ;
		}
		
		@Override
		public synchronized void incrementScore ()
		{
			++score ;
		}

		@Override
		public void setKanji(Kanji kanji) {
			this.kanji = kanji ;
		}

		@Override
		public void gameOver(boolean hasWon) {
			this.isPlaying = false ;
			this.hasWon = hasWon ;
		}

		@Override
		public boolean hasWon() {
			return hasWon ;
		}
	}
	
	
	private static class FailTurn implements GameTurnState
	{
		@Override
		public void go(GameContext gc) {
			if (gc == null)
				throw new NullPointerException () ;
			gc.gameOver (false) ;
		}
	}
	
	
	private static class GiveUpTurn implements GameTurnState
	{
		@Override
		public void go(GameContext gc) {
			if (gc == null)
				throw new NullPointerException () ;
			gc.gameOver (false) ;
		}
	}
	
	
	private static class NextTurn implements GameTurnState
	{

		private final Kanji kanji ;
		
		public NextTurn (Kanji kanji)
		{
			super () ;
			this.kanji = kanji ;
		}
		
		@Override
		public void go(GameContext gc) {
			if (gc == null)
				throw new NullPointerException () ;
			gc.incrementScore();
			gc.setKanji(kanji);
		}
	}

	
	private final KanjiFactory kanjiFactory ;
	private final GameContext gameContext ;
	private GameTurnState gameTurn = null ;
	private Set<String> kanjiNotPickedUpSet = new HashSet<String> () ;
	private int totalNumberOfKanjiInCurrentLevel = 0 ;

	
	public HitsujunGame (KanjiFactory kanjiFactory)
	{
		super () ;
		this.kanjiFactory = kanjiFactory ;
		this.gameContext = new HitsujunGameContext () ;
	}
	
	
	public synchronized void getKanjiInfo (final Callback<KanjiInfo> callback)
	{
		if (gameContext.getKanji() == null)
			throw new IllegalStateException () ;
		kanjiFactory.getKanjiInfo(gameContext.getKanji().getKanji(), gameContext.getLevel(), callback);
	}
	
	
	public synchronized void start (int level)
	{
		if (gameContext.isPlaying())
			giveUp () ;
		gameContext.start(level);
		kanjiNotPickedUpSet.clear() ;
		kanjiNotPickedUpSet.addAll (KanjiClassification.getKanjiSet(level)) ;
		totalNumberOfKanjiInCurrentLevel = kanjiNotPickedUpSet.size() ;
	}
	
	
	public synchronized boolean isLevelCompleted ()
	{
		if (!gameContext.isPlaying())
			throw new IllegalStateException () ;
		return kanjiNotPickedUpSet.isEmpty() ;
	}
	
	
	public synchronized boolean hasWon ()
	{
		if (gameContext.isPlaying())
			throw new IllegalStateException () ;
		return gameContext.hasWon () ;
	}
	
	
	public synchronized float getPercentageAchieved ()
	{
		return ((float)gameContext.getScore()) / ((float)totalNumberOfKanjiInCurrentLevel) * 100f ;
	}
	
	
	public synchronized void nextTurn (final Callback<Triplet<Kanji, KanjiView, KanjiController> > callback)
	{
		if (!gameContext.isPlaying())
			throw new IllegalStateException () ;
		
		if (isLevelCompleted ())
		{
			gameContext.gameOver(true) ;
			callback.onSuccess(null) ;
			return ;
		}
		
		kanjiFactory.getKanji(pickUpKanji (), gameContext.getLevel(), new Callback<Triplet<Kanji, KanjiView, KanjiController> > (){

			@Override
			public void onSuccess(Triplet<Kanji, KanjiView, KanjiController> result) {
				gameTurn = new NextTurn (result.getFirst()) ;
				gameTurn.go(gameContext) ;
				if (callback != null)
					callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				if (callback != null)
					callback.onFailure(cause);
			}}) ;
	}
	
	
	public synchronized void hasLost ()
	{
		if (!gameContext.isPlaying())
			throw new IllegalStateException () ;
		
		gameTurn = new FailTurn () ;
		gameTurn.go(gameContext) ;
	}
	
	public boolean isPlaying() {
		return gameContext.isPlaying();
	}
	
	
	public synchronized int getScore ()
	{
		return gameContext.getScore() ;
	}
	
	
	public synchronized int getLevel ()
	{
		return gameContext.getLevel() + 1;
	}
	
	
	public synchronized Kanji getKanji ()
	{
		return gameContext.getKanji() ;
	}

	
	public synchronized void giveUp ()
	{
		if (!gameContext.isPlaying())
			throw new IllegalStateException () ;
		
		gameTurn = new GiveUpTurn () ;
		gameTurn.go(gameContext) ;
	}

	
	private String pickUpKanji ()
	{	
		if (kanjiNotPickedUpSet.isEmpty())
			throw new IllegalStateException () ;

		int index = (int) ((Math.random() * 31 * kanjiNotPickedUpSet.size()) % kanjiNotPickedUpSet.size()) ;
		String k = null ;
		int count = 0 ;
		Iterator<String> it = kanjiNotPickedUpSet.iterator() ;
		while (it.hasNext())
		{
			k = it.next() ;
			if (count == index)
				break ;
			++count ;
		}
		kanjiNotPickedUpSet.remove(k) ;
		return k ;
	}
}
