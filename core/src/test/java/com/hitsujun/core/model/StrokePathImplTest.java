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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import playn.core.Path;

public class StrokePathImplTest {

    private StrokePath strokePath;
	
    @Before
    public void init() {
    	strokePath = new StrokePathImpl () ; 
    	
    	strokePath.moveTo (0, 0) ;
    	strokePath.lineTo (0, 1) ;
    	strokePath.lineTo (1, 1) ;
    	strokePath.lineTo (1, 0) ;
    }
    
    
    @Test
    public void shouldBeNeighbors ()
    {
    	assertTrue(strokePath.neighbors (-0.1f, 0.6f, 0.15f)) ;
    }
    
    
    @Test
    public void shouldNotBeNeighbors ()
    {
    	assertFalse(strokePath.neighbors (-0.2f, 0.6f, 0.1f)) ;
    }
    
    
    @Test
    public void shouldReplayDiscrete ()
    {
    	Path path = Mockito.mock(Path.class);
    	strokePath.replayDiscrete (path, 1, 0.8f) ;
    	Mockito.verify (path, Mockito.times(1)).reset () ;
    	Mockito.verify (path, Mockito.atLeastOnce ()).moveTo (Mockito.anyFloat(), Mockito.anyFloat()) ;
    	Mockito.verify (path, Mockito.atLeastOnce ()).lineTo (Mockito.anyFloat(), Mockito.anyFloat()) ;
    }
    
    
    @Test
    public void shouldReplay ()
    {
    	Path path = Mockito.mock(Path.class);
    	strokePath.replay (path) ;
    	Mockito.verify (path, Mockito.times(1)).reset () ;
    	Mockito.verify (path, Mockito.atLeastOnce ()).moveTo (Mockito.anyFloat(), Mockito.anyFloat()) ;
    	Mockito.verify (path, Mockito.atLeastOnce ()).lineTo (Mockito.anyFloat(), Mockito.anyFloat()) ;
    }
}
