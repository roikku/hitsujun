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

package com.hitsujun.core.util;

import java.util.ArrayList;
import java.util.List;

public class MathUtils {
	
	private MathUtils () {} ;
	
	
	public static List<Pair<Float, Float> > cubicBezier (int npoints, float x0, float y0, float c1x, float c1y, float c2x, float c2y, float x, float y)
	{
		List<Pair<Float, Float> > ret = new ArrayList<Pair<Float, Float> > () ;
		for (int i = 0 ; i < npoints ; ++i)
		{
			float t = (i+1) * 1f / (float)(npoints) ;
			float xf = cubicBezier (t, x0, c1x, c2x, x) ;
			float yf = cubicBezier (t, y0, c1y, c2y, y) ;
			ret.add(Pair.newPair(xf, yf)) ;
		}
		return ret ;
	}
	
	
	private static float cubicBezier (float t, float f0, float f1, float f2, float f)
	{
		return (float) (Math.pow((1 - t), 3) * f0 + 3 * Math.pow((1 - t), 2) * t * f1 + 3 * (1 - t) * Math.pow(t, 2) * f2 + Math.pow(t, 3) * f) ;
	}
	
	
	public static List<Pair<Float, Float> > quadraticBezier (int npoints, float x0, float y0, float cpx, float cpy, float x, float y)
	{
		List<Pair<Float, Float> > ret = new ArrayList<Pair<Float, Float> > () ;
		for (int i = 0 ; i < npoints ; ++i)
		{
			float t = (i+1) * 1f / (float)(npoints) ;
			float xf = quadraticBezier (t, x0, cpx, x) ;
			float yf = quadraticBezier (t, y0, cpy, y) ;
			ret.add(Pair.newPair(xf, yf)) ;
		}
		return ret ;
	}
	

	private static float quadraticBezier (float t, float f0, float f1, float f)
	{
		return (float) (Math.pow((1 - t), 2) * f0 + 2 * (1 - t) * t * f1 + Math.pow(t, 2) * f) ;
	}
	
	
	public static float distancePointToLineSegment (Pair<Float, Float> s1, Pair<Float, Float> s2, Pair<Float, Float> p)
	{
		float distSqS1S2 = distanceSquared (s1, s2) ;
		if (Math.abs(distSqS1S2) < Float.MIN_NORMAL)
			return (float) Math.sqrt(distanceSquared (s1, p)) ;
		float dotS1PS2P = (p.getFirst() - s1.getFirst()) * (s2.getFirst() - s1.getFirst()) 
				+ (p.getSecond() - s1.getSecond()) * (s2.getSecond() - s1.getSecond())  ;
		float vecS1PProj = dotS1PS2P / distSqS1S2 ;
		if (vecS1PProj < 0)
			return (float) Math.sqrt(distanceSquared (s1, p)) ;
		if (vecS1PProj > 1)
			return (float) Math.sqrt(distanceSquared (s2, p)) ;
		float xinter = s1.getFirst() + vecS1PProj * (s2.getFirst() - s1.getFirst()) ;
		float yinter = s1.getSecond() + vecS1PProj * (s2.getSecond() - s1.getSecond()) ;
		return (float) Math.sqrt(distanceSquared (Pair.newPair(xinter, yinter), p)) ;
	}
	
	
	public static float distanceSquared (Pair<Float, Float> p1, Pair<Float, Float> p2)
	{
		return (float) (Math.pow((p1.getFirst() - p2.getFirst()), 2) + Math.pow((p1.getSecond() - p2.getSecond()), 2)) ;
	}
	
	
	public static boolean arePointsEqual (Pair<Float, Float> p1, Pair<Float, Float> p2)
	{
		if (p1 == null && p2 == null)
			return true ;
		if (p1 == null || p2 == null)
			return false ;
		final float epsilon = Float.MIN_NORMAL ;
		return Math.abs(p1.getFirst() - p2.getFirst()) <= epsilon 
				&& Math.abs(p1.getSecond() - p2.getSecond()) <= epsilon ;
	}
	
	
	public static Integer getNearestPointIndexInList (Pair<Float, Float> p, List<Pair<Float, Float> > list, float threshold)
	{
		if (list == null || p == null)
			throw new NullPointerException () ;
		if (list.isEmpty())
			return null ;
		float distance2 = Float.MAX_VALUE ;
		float threshold2 = threshold * threshold ;
		Integer ret = null ;
		int currIndex = 0 ;
		for (Pair<Float, Float> pt : list)
		{
			float dist2 = distanceSquared (p, pt) ;
			if (dist2 <= threshold2) {
				if (dist2 < distance2) {
					distance2 = dist2 ;
					ret = Integer.valueOf(currIndex) ;
				}
			}
			++currIndex ;
		}
		return ret ;
	}
	
	
	public static float getAngleBetweenVectors (Pair<Float, Float> v1, Pair<Float, Float> v2)
	{
		float vxref = v1.getFirst() ;
		float vyref = v1.getSecond() ;

		float vx = v2.getFirst() ;
		float vy = v2.getSecond() ;
		
		double nref = (float) Math.sqrt(vxref * vxref + vyref * vyref) ;
		double n = (float) Math.sqrt(vx * vx + vy * vy) ;
		double dotP = vxref * vx + vyref * vy ;
		double alpha = Math.acos(dotP / (nref * n)) ;
		
		return (float) Math.abs(alpha) ;	
	}
}
