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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hitsujun.core.util.Pair;

import playn.core.Canvas;
import playn.core.Path;

public class StrokeTest {

    private StrokePath strokePath;
	
    @Before
    public void init() {
    	strokePath = new StrokePathImpl () ; 
    	
    	strokePath.moveTo (0, 0) ;
    	strokePath.lineTo (0, 1) ;
    	strokePath.lineTo (1, 1) ;
    	strokePath.lineTo (1, 0) ;
    }
    
    
    private List<Pair<Float, Float> > getPathApproximation ()
    {
    	List<Pair<Float, Float> > points = new ArrayList<Pair<Float, Float> > ();
    	points.add (Pair.newPair (-0.1f, 0.0f)) ;
    	points.add (Pair.newPair (0.1f, 1.1f)) ;
    	points.add (Pair.newPair (1.1f, 0.9f)) ;
    	points.add (Pair.newPair (1.1f, 0.1f)) ;	
    	
    	return points ;
    }
    
    
    @Test
    public void shouldBeSimilar ()
    {
    	List<Pair<Float, Float> > points = getPathApproximation (); 
    	float threshold = 0.2f;
    	
    	Stroke stroke = new Stroke (strokePath) ; 
    	assertTrue(stroke.isSimilar (points, threshold)) ;
    }
    
    
    @Test
    public void shouldNotBeSimilar ()
    {
    	List<Pair<Float, Float> > points = new ArrayList<Pair<Float, Float> > (); 
    	float threshold = 0.2f;
    	
    	points.add (Pair.newPair (-1.1f, 0.0f)) ;
    	points.add (Pair.newPair (2.1f, 1.1f)) ;
    	points.add (Pair.newPair (1.1f, 0.9f)) ;
    	points.add (Pair.newPair (0.1f, 0.0f)) ;
    	
    	Stroke stroke = new Stroke (strokePath) ; 
    	assertFalse(stroke.isSimilar (points, threshold)) ;
    }
    
    
    @Test
    public void shouldBeNeighbors ()
    {
    	Stroke stroke = new Stroke (strokePath) ; 
    	assertTrue(stroke.neighbors (1.1f, 0.9f, 0.15f)) ;
    }
    
    
    @Test
    public void shouldNotBeNeighbors ()
    {
    	Stroke stroke = new Stroke (strokePath) ; 
    	assertFalse(stroke.neighbors (1.2f, 0.8f, 0.1f)) ;	
    }
    
    
    @Test
    public void shouldStroke ()
    {
    	Stroke stroke = new Stroke (strokePath) ; 
    	Canvas canvas = Mockito.mock(Canvas.class);
    	Mockito.when(canvas.createPath()).thenReturn(Mockito.mock (Path.class));
    	
    	stroke.stroke (canvas) ;
    	Mockito.verify (canvas, Mockito.times(1)).createPath () ;
    	Mockito.verify (canvas, Mockito.times(1)).strokePath (Mockito.any (Path.class)) ;
    }
    
    
    @Test
    public void shouldStrokeDiscrete ()
    {
    	Stroke stroke = new Stroke (strokePath) ; 
    	Canvas canvas = Mockito.mock(Canvas.class);
    	Mockito.when(canvas.createPath()).thenReturn(Mockito.mock (Path.class));
    	
    	stroke.strokeDiscrete (canvas, 0.5f) ;
    	Mockito.verify (canvas, Mockito.times(1)).createPath () ;
    	Mockito.verify (canvas, Mockito.times(1)).strokePath (Mockito.any (Path.class)) ;
    }
    
    
    @Test
    public void shouldDrawingDirectionBeCorrect ()
    {
    	List<Pair<Float, Float> > points = getPathApproximation (); 
    	
    	Stroke stroke = new Stroke (strokePath) ; 
    	assertTrue(stroke.isDrawingDirectionCorrect (points)) ;
    }
    
    
    @Test
    public void shouldDrawingDirectionNotBeCorrect ()
    {
    	List<Pair<Float, Float> > points = getPathApproximation (); 
    	Collections.reverse(points) ;
    	
    	Stroke stroke = new Stroke (strokePath) ; 
    	assertFalse(stroke.isDrawingDirectionCorrect (points)) ;
    }
}
