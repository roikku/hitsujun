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

import java.util.ArrayList;
import java.util.List;

import playn.core.Canvas;
import playn.core.Path;

import com.hitsujun.core.util.MathUtils;
import com.hitsujun.core.util.Pair;
import com.hitsujun.core.util.Transform;
import com.hitsujun.core.util.svg.SvgUtils;


public class Stroke 
{
	private final StrokePath strokePath = new StrokePath () ;

	
	public Stroke (String path, Transform tx, Transform ty)
	{
		super () ;
		SvgUtils.stringToPath(this.strokePath, path, tx, ty) ;
	}
	
	
	public boolean isSimilar (List<Pair<Float, Float> > points, float threshold)
	{
		if (points == null || points.size() < 3)
			return false ;
		int numInNeighborhood = 0 ;
		for (Pair<Float, Float> pt : points)
			numInNeighborhood += (neighbors ( pt.getFirst(),  pt.getSecond(), threshold)) ? (1) : (0) ;
		return ((float)numInNeighborhood) / ((float)points.size()) > 0.7f ;
	}
	
	
	public boolean isDrawingDirectionCorrect (List<Pair<Float, Float> > points)
	{
		// TODO: ...
		// temporary solution for tests
		
		List<Pair<Float, Float> > userPoints = new ArrayList<Pair<Float, Float> > () ;
		List<Integer> correspondingStrokePointsIndexes = new ArrayList<Integer> () ;
		
		List<Pair<Float, Float> > allStrokePoints = new ArrayList<Pair<Float, Float> > () ;
		for (List<Pair<Float, Float> > listPt : strokePath.getDiscretePath ()) {
			allStrokePoints.addAll(listPt) ;
		}
		
		float threshold = 20f ; 
		Integer prevCorrPtIndex = null ;
		for (Pair<Float, Float> userPt : points)
		{
			Integer index = MathUtils.getNearestPointIndexInList (userPt, allStrokePoints, threshold) ;
			if (index == null)
				continue ;
			if (index.equals(prevCorrPtIndex))
				continue ;
			userPoints.add (userPt) ;
			correspondingStrokePointsIndexes.add (index) ;
			prevCorrPtIndex = index ;			
		}
		if (userPoints.size() >= 2)
		{
			int count = 0 ;
			for (int i = 0 ; i < correspondingStrokePointsIndexes.size() - 1 ; ++i) {
				count = count + ((correspondingStrokePointsIndexes.get(i).compareTo(correspondingStrokePointsIndexes.get(i+1)) > 0) ? (0) : (1)) ;
			}
			return ((float)count) / ((float)userPoints.size() - 1.0f) >= 0.7f  ;
		}
		else
		{
			Pair<Float, Float> firstRefPt = allStrokePoints.get(0) ;
			Pair<Float, Float> nextRefPt = allStrokePoints.get(allStrokePoints.size() - 1) ;
			float vxref = nextRefPt.getFirst() - firstRefPt.getFirst() ;
			float vyref = nextRefPt.getSecond() - firstRefPt.getSecond() ;
			
			Pair<Float, Float> firstPt = points.get(0) ;
			Pair<Float, Float> nextPt = points.get(points.size() - 1) ;
			float vx = nextPt.getFirst() - firstPt.getFirst() ;
			float vy = nextPt.getSecond() - firstPt.getSecond() ;

			return MathUtils.getAngleBetweenVectors(Pair.newPair(vxref, vyref), Pair.newPair(vx, vy)) * 180 / Math.PI < 80 ;
		}
	}
	
	
	public boolean neighbors (float x, float y, float threshold)
	{
		return strokePath.neighbors(x, y, threshold) ;
	}
	
	
	public void stroke (Canvas canvas) 
	{
		Path p = canvas.createPath();
		strokePath.replay(p);
		canvas.strokePath(p);
		
		/*
		if (true)
		{
			List<Pair<Float, Float> > allStrokePoints = new ArrayList<Pair<Float, Float> > () ;
			for (List<Pair<Float, Float> > listPt : strokePath.getDiscretePath ()) {
				allStrokePoints.addAll(listPt) ;
			}
			canvas.setStrokeColor(Color.rgb (10, 200, 200)) ;
			for (Pair<Float, Float> pt : allStrokePoints)
			{
				//canvas.strokeCircle(pt.getFirst(), pt.getSecond(), 15f) ;
				canvas.drawPoint(pt.getFirst(), pt.getSecond()) ;
			}
		}*/
	}
	
	
	public void strokeDiscrete (Canvas canvas, float percent) 
	{
		Path p = canvas.createPath();
		strokePath.replayDiscrete (p, 10, percent) ;
		canvas.strokePath(p);
	}
}
