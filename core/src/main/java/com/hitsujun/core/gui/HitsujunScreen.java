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
 * This file includes some code from the Atlantis project (https://github.com/threerings/atlantis). 
 * More precisely from the file: 
 * https://github.com/threerings/atlantis/blob/master/core/src/main/java/atlantis/client/AtlantisScreen.java
 */


package com.hitsujun.core.gui;

import com.hitsujun.core.Hitsujun;
import com.hitsujun.core.controller.HasActionListener;

import playn.core.PlayN;
import playn.core.util.Clock;
import tripleplay.anim.Animator;
import tripleplay.game.Screen;
import tripleplay.ui.Interface;

public abstract class HitsujunScreen extends Screen {

    /** Manages animations on this screen. */
	protected final Animator anim = new Animator ();


    /** Manages our user interfaces. */
    protected final Interface iface = new Interface();
    
    
    private final Object actionListenerLock = new Object () ;
    protected HasActionListener actionListener = null ;
    
    
    public HitsujunScreen setActionListener (HasActionListener actionListener)
    {
    	synchronized (actionListenerLock)
    	{
	    	this.actionListener = actionListener ;
	    	return this ;
    	}
    }
    

	@Override
	public void wasShown() {
    	synchronized (actionListenerLock)
    	{
			if (actionListener != null)
			{
				actionListener.initKeyboardListener() ;
				actionListener.initPointerListener() ;
				actionListener.initTouchListener() ;
			}
			else
			{
				PlayN.pointer().setListener(null) ;
				PlayN.keyboard().setListener(null) ;
				PlayN.touch().setListener(null) ;
			}
    	}
	}
	

	@Override
	public void wasHidden() {
	}

	@Override
	public void wasRemoved() {
		if (layer != null)
			layer.destroy();
	}

	@Override
	public void update(int delta) {
		_clock.update(delta);
		if (iface != null) {
			iface.update(delta);
		}
	}

	@Override
	public void paint(Clock clock) {
		if (iface != null) {
			iface.paint(_clock);
		}
		super.paint(clock);
	}

	public void paint(float alpha) {
		_clock.paint(alpha);
		paint (_clock) ;
	}
	
	public boolean isPopup ()
	{
		return false ;
	}
	
	protected final Clock.Source _clock = new Clock.Source(Hitsujun.UPDATE_RATE);
}
