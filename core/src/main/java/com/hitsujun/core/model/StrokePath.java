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

import java.util.List;

import com.hitsujun.core.util.Pair;

import playn.core.Path;

public interface StrokePath extends Path {

	public void replay(Path path);
	public List<List<Pair<Float, Float>>> getDiscretePath();
	public void replayDiscrete(Path path, int step, float percent);
	public boolean neighbors(float x, float y, float threshold);
}
