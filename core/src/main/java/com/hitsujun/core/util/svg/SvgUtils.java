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

/* This file incorporates work covered by the following copyright and  
 * permission notice:  
 * 
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Some code have been extracted from the SVG Android project 
 *  (http://code.google.com/p/svg-android/), and adapted to fit our needs
 *  
 *  - the function stringToPath is an adaptation of the method doPath
 *  in the class SVGParser (package com.larvalabs.svgandroid).
 */

package com.hitsujun.core.util.svg;

import playn.core.Path;

import com.hitsujun.core.util.Transform;

public class SvgUtils {

	private SvgUtils () { super () ; throw new UnsupportedOperationException () ; } 
	
	// some code are extracted from svg_android project and adapted to our needs
	public static void stringToPath(Path p, String pathStr, Transform tx, Transform ty) 
	{
		int n = pathStr.length();
		ParserHelper ph = new ParserHelper(pathStr, 0);
		ph.skipWhitespace();
		float lastX = 0;
		float lastY = 0;
		float lastX1 = 0;
		float lastY1 = 0;
		float subPathStartX = 0;
		float subPathStartY = 0;
		char prevCmd = 0;

		while (ph.pos < n) {
			char cmd = pathStr.charAt(ph.pos);
			switch (cmd) {
				case '-':
				case '+':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					if (prevCmd == 'm' || prevCmd == 'M') {
						cmd = (char) (((int) prevCmd) - 1);
						break;
					} else if (prevCmd == 'c' || prevCmd == 'C') {
						cmd = prevCmd;
						break;
					} else if (prevCmd == 'l' || prevCmd == 'L') {
						cmd = prevCmd;
						break;
					}
				default: {
					ph.advance();
					prevCmd = cmd;
				}
			}

			boolean wasCurve = false;
			switch (cmd) {
				case 'M':
				case 'm': {
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 'm') {
						subPathStartX += x;
						subPathStartY += y;
						p.moveTo(tx.transform(x), ty.transform(y));
						lastX += x;
						lastY += y;
					} else {
						subPathStartX = x;
						subPathStartY = y;
						p.moveTo(tx.transform(x), ty.transform(y));
						lastX = x;
						lastY = y;
					}
					break;
				}
				case 'Z':
				case 'z': {
					p.close();
					p.moveTo(tx.transform(subPathStartX),
							ty.transform(subPathStartY));
					lastX = subPathStartX;
					lastY = subPathStartY;
					lastX1 = subPathStartX;
					lastY1 = subPathStartY;
					wasCurve = true;
					break;
				}
				case 'L':
				case 'l': {
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 'l') {
						p.lineTo(tx.transform(x), ty.transform(y));
						lastX += x;
						lastY += y;
					} else {
						p.lineTo(tx.transform(x), ty.transform(y));
						lastX = x;
						lastY = y;
					}
					break;
				}
				case 'H':
				case 'h': {
					float x = ph.nextFloat();
					if (cmd == 'h') {
						p.lineTo(tx.transform(x), 0);
						lastX += x;
					} else {
						p.lineTo(tx.transform(x), ty.transform(lastY));
						lastX = x;
					}
					break;
				}
				case 'V':
				case 'v': {
					float y = ph.nextFloat();
					if (cmd == 'v') {
						p.lineTo(0, ty.transform(y));
						lastY += y;
					} else {
						p.lineTo(tx.transform(lastX), ty.transform(y));
						lastY = y;
					}
					break;
				}
				case 'C':
				case 'c': {
					wasCurve = true;
					float x1 = ph.nextFloat();
					float y1 = ph.nextFloat();
					float x2 = ph.nextFloat();
					float y2 = ph.nextFloat();
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 'c') {
						x1 += lastX;
						x2 += lastX;
						x += lastX;
						y1 += lastY;
						y2 += lastY;
						y += lastY;
					}
					p.bezierTo(tx.transform(x1), ty.transform(y1),
							tx.transform(x2), ty.transform(y2), tx.transform(x),
							ty.transform(y));
					lastX1 = x2;
					lastY1 = y2;
					lastX = x;
					lastY = y;
					break;
				}
				case 'S':
				case 's': {
					wasCurve = true;
					float x2 = ph.nextFloat();
					float y2 = ph.nextFloat();
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 's') {
						x2 += lastX;
						x += lastX;
						y2 += lastY;
						y += lastY;
					}
					float x1 = 2 * lastX - lastX1;
					float y1 = 2 * lastY - lastY1;
					p.bezierTo(tx.transform(x1), ty.transform(y1),
							tx.transform(x2), ty.transform(y2), tx.transform(x),
							ty.transform(y));
					lastX1 = x2;
					lastY1 = y2;
					lastX = x;
					lastY = y;
					break;
				}
				case 'A':
				case 'a': {
					throw new UnsupportedOperationException();
				}
			}
			if (!wasCurve) {
				lastX1 = lastX;
				lastY1 = lastY;
			}
			ph.skipWhitespace();
		}
	}
}
