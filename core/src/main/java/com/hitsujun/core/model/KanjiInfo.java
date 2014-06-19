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
import java.util.Iterator;
import java.util.List;

import playn.core.Json;
import playn.core.Json.TypedArray;
import playn.core.PlayN;

public class KanjiInfo {

	private final String json ;
	private final Json.Object jsonObject ;
	private List<Integer> strokeCountList = null ;
	private List<String> kunYomiReading = null ;
	private List<String> onYomiReading = null  ;
	private List<String> meanings = null  ;
	private String kanji = null ;
	
	
	public KanjiInfo (String json)
	{
		super () ;
		if (json == null)
			throw new NullPointerException () ;
		this.json = json ;
		this.jsonObject = PlayN.json().parse(json) ;
	}
	
	
	public String getJson () {
		return json ;
	}
	
	
	public String getKanji () {
		if (kanji != null)
			return null ;
		kanji = jsonObject.getString("literal") ;
		return kanji ;
	}
	
	
	public List<Integer> getStrokeCountList () {
		if (strokeCountList != null)
			return strokeCountList ;
		strokeCountList = new ArrayList<Integer> () ;
		TypedArray<Integer> ja = jsonObject.getObject("misc").getArray("strokeCountList", Integer.class) ;
		if (ja == null)
			return strokeCountList ;
		Iterator<Integer> it = ja.iterator() ;
		while (it.hasNext())
			strokeCountList.add(it.next()) ;
		return strokeCountList ; 
	}
	
	
	public List<String> getKunYomiReadings ()
	{
		if (kunYomiReading != null)
			return kunYomiReading ;
		kunYomiReading = new ArrayList<String> () ;
		try
		{
			TypedArray<String> r = jsonObject.getObject("readingMeaning")
					.getArray("rmgrouplist")
					.getObject(0)
					.getObject("readingMap")
					.getArray("ja_kun", String.class);
			if (r == null || r.length() == 0)
				return kunYomiReading ;
			Iterator<String> it = r.iterator() ;
			while (it.hasNext())
				kunYomiReading.add(it.next()) ;
		}
		catch (Throwable t){
			PlayN.log().debug("Error occurred while getting the kun-yomi of " + getKanji () + ": " + t.toString()) ;
		}
		return kunYomiReading ;
	}
	
	
	public List<String> getOnYomiReadings ()
	{
		if (onYomiReading != null)
			return onYomiReading ;
		onYomiReading = new ArrayList<String> () ;
		try
		{
			TypedArray<String> r = jsonObject.getObject("readingMeaning")
					.getArray("rmgrouplist")
					.getObject(0)
					.getObject("readingMap")
					.getArray("ja_on", String.class);
			if (r == null || r.length() == 0)
				return onYomiReading ;
			Iterator<String> it = r.iterator() ;
			while (it.hasNext())
				onYomiReading.add(it.next()) ;
		}
		catch (Throwable t){
			PlayN.log().debug("Error occurred while getting the on-yomi of " + getKanji () + ": " + t.toString()) ;
		}
		return onYomiReading ;
	}
	
	
	public List<String> getMeaning ()
	{
		if (meanings != null)
			return meanings ;
		meanings = new ArrayList<String> () ;
		try
		{
			TypedArray<String> r = jsonObject.getObject("readingMeaning")
					.getArray("rmgrouplist")
					.getObject(0)
					.getObject("meaningMap")
					.getArray("eng", String.class);
			if (r == null || r.length() == 0)
				return meanings ;
			Iterator<String> it = r.iterator() ;
			while (it.hasNext())
				meanings.add(it.next()) ;
		}
		catch (Throwable t){
			PlayN.log().debug("Error occurred while getting the meaning of " + getKanji () + ": " + t.toString()) ;
		}
		return meanings ;
	}
}
