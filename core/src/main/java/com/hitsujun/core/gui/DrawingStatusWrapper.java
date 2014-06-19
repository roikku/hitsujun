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

public class DrawingStatusWrapper implements DrawingStatus
{
	private final DrawingStatus drawingStatus ;
	
	public DrawingStatusWrapper (DrawingStatus drawingStatus)
	{
		super () ;
		this.drawingStatus = drawingStatus ;
	}
	
	@Override
	public void init() {
		if (drawingStatus != null)
			drawingStatus.init() ;
	}

	@Override
	public void drawing(float per) {
		if (drawingStatus != null)
			drawingStatus.drawing(per) ;
	}

	@Override
	public void stopped() {
		if (drawingStatus != null)
			drawingStatus.stopped() ;
	}

	@Override
	public void complete() {
		if (drawingStatus != null)
			drawingStatus.complete() ;
	}
}
