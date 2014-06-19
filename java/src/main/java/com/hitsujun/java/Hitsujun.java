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
 * This file includes some code from the Java Rabbit Engine Project (http://code.google.com/p/java-rabbit-engine/), 
 * which is distributed under the New BSD License:
 * 
 * Copyright 2011 Christopher Molini. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list 
 * of conditions and the following disclaimer in the documentation and/or other materials provided 
 * with the distribution.
 * 
 * 3. Neither the name of 'The Java Rabbit Engine,' 'jRabbit,' nor the names of its contributors may 
 * be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY CHRISTOPHER MOLINI "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL CHRIS MOLINI OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * More precisely, the following file has been used:
 * http://jrabbit.minds-eye-games.com/javadoc/index.html?org/jrabbit/base/graphics/misc/package-summary.html
 * method convertToByteBuffer
 */


package com.hitsujun.java;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import org.lwjgl.opengl.Display;

public class Hitsujun {

	// http://code.google.com/p/java-rabbit-engine/
	public static ByteBuffer convertToByteBuffer(BufferedImage image) {
		byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
		int counter = 0;
		for (int i = 0; i < image.getHeight(); i++)
			for (int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);
				buffer[counter + 0] = (byte) ((colorSpace << 8) >> 24);
				buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				buffer[counter + 3] = (byte) (colorSpace >> 24);
				counter += 4;
			}
		return ByteBuffer.wrap(buffer);
	}
	

	public static ByteBuffer getIcon(String string) throws IOException {
		BufferedImage in = null;
		in = ImageIO.read(Hitsujun.class.getResource(
				"/icons/" + string));
		return convertToByteBuffer(in);
	}
	
	
	public static void main(String[] args) {		
		JavaPlatform.Config config = new JavaPlatform.Config();
		// use config to customize the Java platform, if needed

		// https://github.com/threerings/playn/blob/master/java/src/playn/java/JavaPlatform.java
		config.appName = "Hitsujun";
		config.width = 800;
		config.height = 600;

		JavaPlatform.register(config);
		
		// set the icons
		try
		{
			final String [] iconsList = {"hitsujun_icon_128.png", "hitsujun_icon_64.png", "hitsujun_icon_32.png", "hitsujun_icon_16.png"} ;
			ByteBuffer[] icons = new ByteBuffer[iconsList.length];
			boolean isIconsSetValid = true ;
			for (int i = 0 ; i < icons.length ; ++i) {
				icons[i] = getIcon (iconsList[i]) ;
				if (icons[i] == null) {
					isIconsSetValid = false ;
					break ;
				}
			}
			if (isIconsSetValid) {
				Display.setIcon(icons) ;
			}
		} catch (IOException e) {
			// we do nothing
		}

		PlayN.run(new com.hitsujun.core.Hitsujun());
	}
}
