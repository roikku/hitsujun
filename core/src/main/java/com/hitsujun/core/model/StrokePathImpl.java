/**
 * Copyright 2014 Loic Merckel
 * Copyright 2010 The PlayN Authors
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
 * 
 * This code derives from 
 * https://github.com/threerings/playn/blob/master/html/src/playn/html/HtmlPath.java
 * 
 */

package com.hitsujun.core.model;

import java.util.ArrayList;
import java.util.List;

import com.hitsujun.core.util.MathUtils;
import com.hitsujun.core.util.Pair;

import playn.core.Path;

class StrokePathImpl implements StrokePath {

	private static final int CMD_BEZIER = 3;
	private static final int CMD_CLOSE = 4;
	private static final int CMD_LINE = 1;
	private static final int CMD_MOVE = 0;
	private static final int CMD_QUAD = 2;

	private final List<Float> list = new ArrayList<Float>();
	private List<List<Pair<Float, Float> > > pointsList = null ;
	private int nPoints = 0 ;
	private int step = 10 ;


	@Override
	public Path bezierTo(float c1x, float c1y, float c2x, float c2y, float x,
			float y) {
		list.add((float) CMD_BEZIER);
		list.add(c1x);
		list.add(c1y);
		list.add(c2x);
		list.add(c2y);
		list.add(x);
		list.add(y);
		return this;
	}

	
	@Override
	public Path close() {
		list.add((float) CMD_CLOSE);
		return this;
	}

	
	@Override
	public Path lineTo(float x, float y) {
		list.add((float) CMD_LINE);
		list.add(x);
		list.add(y);
		return this;
	}

	
	@Override
	public Path moveTo(float x, float y) {
		list.add((float) CMD_MOVE);
		list.add(x);
		list.add(y);
		return this;
	}

	
	@Override
	public Path quadraticCurveTo(float cpx, float cpy, float x, float y) {
		list.add((float) CMD_QUAD);
		list.add(cpx);
		list.add(cpy);
		list.add(x);
		list.add(y);
		return this;
	}

	
	@Override
	public Path reset() {
		list.clear();
		return this;
	}


	@SuppressWarnings("unused")
	private float[] getVertices() {
		int len = list.size();
		assert len % 2 == 0;
		float[] vertices = new float[len];
		for (int v = 0; v < len;) {
			int cmd = list.get(v).intValue();
			if (v == vertices.length - 2) {
				assert cmd == CMD_CLOSE;
			} else {
				assert cmd == CMD_MOVE;
			}
			vertices[v] = (float) list.get(v + 1);
			vertices[v + 1] = (float) list.get(v + 2);
		}
		return vertices;
	}
	
	
	@Override
	public boolean neighbors (float x, float y, float threshold)
	{
		if (pointsList == null)
			pointsList = getDiscretePath (step) ;
		if (pointsList.isEmpty())
			return false ;
		for (List<Pair<Float, Float> >  list : pointsList){
			if (neighbors (list, x, y, threshold))
				return true ;
		}
		return false ;
	}
	
	
	private boolean neighbors (List<Pair<Float, Float> > list, float x, float y, float threshold)
	{
		Pair<Float, Float> p = Pair.newPair(x, y) ;
		if (list.isEmpty())
			return false ;
		if (list.size() < 2)
			return (MathUtils.distanceSquared(list.get(0), p) <= threshold * threshold) ;
		Pair<Float, Float> s1 = list.get(0) ;
		for (int i = 1 ; i < list.size() ; ++i)
		{
			Pair<Float, Float> s2 = list.get(i) ;
			if (MathUtils.distancePointToLineSegment(s1, s2, p) <= threshold)
				return true ;
			s1 = s2 ;
		}
		return false ;
	}
	
	
	@Override
	public void replayDiscrete(Path path, int step, float percent) 
	{
		path.reset();
		if (pointsList == null || this.step != step)
			pointsList = getDiscretePath (step) ;
		this.step = step ;
		int count = 0 ;
		for (List<Pair<Float, Float> >  list : pointsList)
		{
			boolean hasMovedToOrigin = false ;
			for (Pair<Float, Float> point : list)
			{
				if (!hasMovedToOrigin)
				{
					hasMovedToOrigin = true ;
					path.moveTo(point.getFirst(), point.getSecond()) ;
				}
				else
					path.lineTo(point.getFirst(), point.getSecond()) ;
				
				++count ;
				if (count / (float)(nPoints) >= percent)
					return ;
			}
		}
	}
	
	
	@Override
	public List<List<Pair<Float, Float> > > getDiscretePath ()
	{
		if (pointsList == null)
			pointsList = getDiscretePath (step) ;
		return pointsList ;
	}
	
	
	private List<List<Pair<Float, Float> > > getDiscretePath (int step)
	{
		List<List<Pair<Float, Float> > > ret = new ArrayList<List<Pair<Float, Float> > > () ;
		List<Pair<Float, Float> > currList = null ;
		nPoints = 0 ;
		
		int len = list.size(), i = 0;
		float x = 0, y = 0;
		while (i < len) 
		{
			switch (list.get(i++).intValue()) {
				case CMD_MOVE: {
					x = (float) list.get(i++);
					y = (float) list.get(i++);
					if (currList != null)
						nPoints += currList.size() ;
					currList = new ArrayList<Pair<Float, Float> > () ;
					currList.add(Pair.newPair(x, y));
					ret.add(currList);
					break;
				}
				case CMD_LINE: {
					x = (float) list.get(i++);
					y = (float) list.get(i++) ;
					currList.add(Pair.newPair(x, y));
					break;
				}
				case CMD_QUAD: {
					float cpx = (float) list.get(i++);
					float cpy = (float) list.get(i++);
					x = (float) list.get(i++);
					y = (float) list.get(i++);
					Pair<Float, Float> prev = currList.get(currList.size() - 1) ;
					currList.addAll(MathUtils.quadraticBezier(step, prev.getFirst(), prev.getSecond(), cpx, cpy, x, y)) ;
					break;
				}
				case CMD_BEZIER: {
					float c1x = (float) list.get(i++), c1y = (float) list.get(i++);
					float c2x = (float) list.get(i++), c2y = (float) list.get(i++);
					x = (float) list.get(i++);
					y = (float) list.get(i++);
					Pair<Float, Float> prev = currList.get(currList.size() - 1) ;
					currList.addAll(MathUtils.cubicBezier(step, prev.getFirst(), prev.getSecond(), c1x, c1y, c2x, c2y, x, y)) ;
					break;
				}
				case CMD_CLOSE: {
					break;
				}
				default:
					throw new AssertionError("Corrupt command list");
			}
		}
		if (currList != null)
			nPoints += currList.size() ;
		return ret ;
	}

	
	@Override
	public void replay(Path path) 
	{
		path.reset();

		int len = list.size(), i = 0;
		float x = 0, y = 0;
		while (i < len) {
			switch (list.get(i++).intValue()) {
			case CMD_MOVE: {
				x = (float) list.get(i++);
				y = (float) list.get(i++);
				path.moveTo(x, y);
				break;
			}
			case CMD_LINE: {
				x = (float) list.get(i++);
				y = (float) list.get(i++);
				path.lineTo(x, y);
				break;
			}
			case CMD_QUAD: {
				float cpx = (float) list.get(i++);
				float cpy = (float) list.get(i++);
				x = (float) list.get(i++);
				y = (float) list.get(i++);
				path.quadraticCurveTo(cpx, cpy, x, y);
				break;
			}
			case CMD_BEZIER: {
				float c1x = (float) list.get(i++), c1y = (float) list.get(i++);
				float c2x = (float) list.get(i++), c2y = (float) list.get(i++);
				x = (float) list.get(i++);
				y = (float) list.get(i++);
				path.bezierTo(c1x, c1y, c2x, c2y, x, y);
				break;
			}
			case CMD_CLOSE: {
				path.close();
				break;
			}

			default:
				throw new AssertionError("Corrupt command list");
			}
		}
	}
}
