/*
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

package com.hitsujun.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tripleplay.util.Logger;

public class MathUtilsTest {

	//private static final Logger logger = new Logger("MathUtilsTest");
	
	private static final float epsilon = Float.MIN_NORMAL ;
	
	
	private boolean isInTheVicinityOfZero (float f)
	{
		return Math.abs(f) <= epsilon ;
	}
	
	
    @Test
    public void shouldComputeCubicBezier() {
    	
    	int npoints = 5 ; 
    	
    	float x0 = 0f ; 
    	float y0 = 0f ; 
    	float c1x = 0.5f ;
    	float c1y = 0.5f ; 
    	float c2x = 0.8f ;
    	float c2y = 0.8f ; 
    	float x = 1f ; 
    	float y = 1f ;
    	
    	float expectedRet [] = {0.2768f, 0.51040006f, 0.7056f, 0.8672f, 1.0f} ;
    	
    	List<Pair<Float, Float> >  ret = MathUtils.cubicBezier (npoints, x0, y0, c1x, c1y, c2x, c2y, x, y) ;
    	assertEquals (npoints, ret.size ()) ;
    	int index = 0 ;
    	for (Pair<Float, Float> p : ret) {
    		assertEquals (expectedRet[index], p.getFirst (), epsilon) ;
    		assertEquals (expectedRet[index], p.getSecond (), epsilon) ;
    		++index ;
    	}
    }
  
    
    @Test
    public void shouldComputeQuadraticBezier() {

    	int npoints = 5 ; 
    	
    	float x0 = 0f ; 
    	float y0 = 0f ; 
    	float cpx = 0.5f ;
    	float cpy = 0.5f ; 
    	float x = 1f ; 
    	float y = 1f ;
    	
    	float expectedRet [] = {0.20000002f, 0.4f, 0.6f, 0.8f, 1.0f} ;
    	
    	List<Pair<Float, Float> >  ret = MathUtils.quadraticBezier (npoints, x0, y0, cpx, cpy, x, y) ;
    	assertEquals (npoints, ret.size ()) ;
    	int index = 0 ;
    	for (Pair<Float, Float> p : ret) {
    		assertEquals (expectedRet[index], p.getFirst (), epsilon) ;
    		assertEquals (expectedRet[index], p.getSecond (), epsilon) ;
    		++index ;
    	}
    }
    
	
    @Test
    public void shouldComputeDistancePointToLineSegment() {
    	
    	Pair<Float, Float> s1 ;
    	Pair<Float, Float> s2 ;
    	Pair<Float, Float> p ;
    	float d ;
    	
    	s1 = Pair.newPair (0f,0f) ; 
    	s2 = Pair.newPair (0f,0f) ;
    	p = Pair.newPair (0f,0f) ;
    	d = 0f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distancePointToLineSegment (s1, s2, p) - d)) ;
    	
    	s1 = Pair.newPair (0f,0f) ; 
    	s2 = Pair.newPair (0f,0f) ;
    	p = Pair.newPair (10f,0f) ;
    	d = 10f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distancePointToLineSegment (s1, s2, p) - d)) ;
    	
    	s1 = Pair.newPair (10f,0f) ; 
    	s2 = Pair.newPair (-10f,0f) ;
    	p = Pair.newPair (0f,10f) ;
    	d = 10f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distancePointToLineSegment (s1, s2, p) - d)) ;
    	
    	s1 = Pair.newPair (0f,-10f) ; 
    	s2 = Pair.newPair (0f,10f) ;
    	p = Pair.newPair (10f,0f) ;
    	d = 10f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distancePointToLineSegment (s1, s2, p) - d)) ;
    	
    	s1 = Pair.newPair (0f,-10f) ; 
    	s2 = Pair.newPair (0f,10f) ;
    	p = Pair.newPair (0f,20f) ;
    	d = 10f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distancePointToLineSegment (s1, s2, p) - d)) ;
    	
    	s1 = Pair.newPair (-10f,-10f) ; 
    	s2 = Pair.newPair (10f,10f) ;
    	p = Pair.newPair (10f,-10f) ;
    	d = (float) Math.sqrt(10f*10f + 10f*10f) ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distancePointToLineSegment (s1, s2, p) - d)) ;
    	
    	s1 = Pair.newPair (-10f,-10f) ; 
    	s2 = Pair.newPair (10f,10f) ;
    	p = Pair.newPair (10f,10f+10f) ;
    	d = 10f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distancePointToLineSegment (s1, s2, p) - d)) ;   	
    }
	
    
    @Test
    public void shouldComputeDistanceSquared ()
    {
    	Pair<Float, Float> p1 ;
    	Pair<Float, Float> p2 ;
    	float d ;
    	
    	p1 = Pair.newPair (0f,0f) ; 
    	p2 = Pair.newPair (0f,0f) ;
    	d = 0f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distanceSquared (p1, p2) - d)) ;
    	
    	p1 = Pair.newPair (10f,0f) ; 
    	p2 = Pair.newPair (0f,0f) ;
    	d = 100f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distanceSquared (p1, p2) - d)) ;
    	
    	p1 = Pair.newPair (10f,10f) ; 
    	p2 = Pair.newPair (-10f,-10f) ;
    	d = 20f*20f + 20f*20f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.distanceSquared (p1, p2) - d)) ;
    }
    
    
    @Test
    public void shouldComparePoints ()
    {
    	Pair<Float, Float> p1 ;
    	Pair<Float, Float> p2 ;
    	
    	p1 = Pair.newPair (10f,0f) ; 
    	p2 = Pair.newPair (10f,0f) ;
    	assertTrue (MathUtils.arePointsEqual (p1, p2)) ;
    	
    	p1 = Pair.newPair (10f,0f) ; 
    	p2 = Pair.newPair (0f,0f) ;
    	assertFalse (MathUtils.arePointsEqual (p1, p2)) ;
    }
    
    
    @Test
    public void shouldGetNearestPointIndexInList ()
    {
    	Pair<Float, Float> p ; 
    	List<Pair<Float, Float> > list = new ArrayList<Pair<Float, Float> > () ; 
    	float threshold = 4f ;
    	
    	p = Pair.newPair (0f,0f) ; 
    	assertEquals (null, MathUtils.getNearestPointIndexInList (p, list, threshold)) ;
    	
    	list.add (Pair.newPair (10f,10f)) ;
    	list.add (Pair.newPair (20f,20f)) ;
    	list.add (Pair.newPair (30f,30f)) ;
    	p = Pair.newPair (21f, 23f) ;
    	assertEquals (Integer.valueOf (1), MathUtils.getNearestPointIndexInList (p, list, threshold)) ;
    }
    
    
    @Test
    public void shouldGetAngleBetweenVectors ()
    {
    	Pair<Float, Float> v1 ; 
    	Pair<Float, Float> v2 ;
    	float a ;
    	
    	v1 = Pair.newPair (10f,0f) ; 
    	v2 = Pair.newPair (10f,0f) ;
    	a = 0f ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.getAngleBetweenVectors (v1, v2) - a)) ;
    	
    	v1 = Pair.newPair (10f,10f) ; 
    	v2 = Pair.newPair (-10f,-10f) ;
    	a = (float) (Math.PI) ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.getAngleBetweenVectors (v1, v2) - a)) ;
    	
    	v1 = Pair.newPair (10f,0f) ; 
    	v2 = Pair.newPair (0f,10f) ;
    	a = (float) (Math.PI / 2) ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.getAngleBetweenVectors (v1, v2) - a)) ;
    	
    	v1 = Pair.newPair (10f,10f) ; 
    	v2 = Pair.newPair (10f,0f) ;
    	a = (float) (Math.PI / 4) ;
    	assertTrue (isInTheVicinityOfZero(MathUtils.getAngleBetweenVectors (v1, v2) - a)) ;
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetAngleBetweenVectors ()
    {
    	MathUtils.getAngleBetweenVectors (Pair.newPair (0f,0f),  Pair.newPair (10f,0f)) ;
    }
}
