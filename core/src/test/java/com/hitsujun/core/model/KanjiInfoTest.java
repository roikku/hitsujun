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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import playn.core.PlayN;
import playn.java.JavaPlatform;

public class KanjiInfoTest {

	static {
		// We need the Json service
		// http://stackoverflow.com/questions/10467667/with-playn-framework-how-can-i-include-unit-tests-in-my-project
		JavaPlatform.Config config = new JavaPlatform.Config();
		config.headless = true;
		JavaPlatform.register(config);
	}

	private static final String kanjiJson = "{\"literal\":\"聡\",\"misc\":{\"grade\":9,\"strokeCountList\":[14],\"freq\":1507,\"jlpt\":1,\"variantSetMap\":{\"jis208\":[\"70-66\"],\"jis212\":[\"53-70\"]},\"variantKanjiSet\":[\"聰\",\"聦\"]},\"readingMeaning\":{\"rmgrouplist\":[{\"readingMap\":{\"korean_r\":[\"chong\"],\"ja_on\":[\"ソウ\"],\"korean_h\":[\"총\"],\"ja_kun\":[\"みみざと.い\",\"さと.い\"],\"pinyin\":[\"cong1\"]},\"meaningMap\":{\"eng\":[\"fast learner\",\"wise\"],\"spa\":[\"inteligente\",\"que aprende rápido\"]}}],\"nanoriSet\":[\"みのる\",\"さとる\",\"さと\",\"あきら\",\"とし\",\"さた\",\"あき\",\"さとし\"]},\"radicalMap\":{\"classical\":128},\"radicalKanjiListMap\":{\"classical\":[\"耳\"]},\"codepointMap\":{\"ucs\":\"8061\",\"jis208\":\"33-79\"},\"dicNumberMap\":{\"moro\":\"29109\",\"halpern_kkld\":\"938\",\"sh_kk\":\"2203\",\"nelson_c\":\"3708\",\"oneill_names\":\"2311\",\"heisig\":\"2677\",\"oneill_kk\":\"1961\",\"nelson_n\":\"4730\",\"halpern_njecd\":\"1384\"},\"kanjiElementsList\":[\"耳\",\"心\",\"ハ\",\"厶\"]}" ;

	
	@Test
	public void shouldGetJson ()
	{
		KanjiInfo kanjiInfo = new KanjiInfo (kanjiJson, PlayN.json()) ;
		assertEquals(kanjiJson, kanjiInfo.getJson ()) ;
	}
	
	
	@Test
	public void shouldGetKanji ()
	{
		KanjiInfo kanjiInfo = new KanjiInfo (kanjiJson, PlayN.json()) ;
		assertEquals("聡", kanjiInfo.getKanji ()) ;
	}
	

	@Test
	public void ShouldGetStrokeCountList ()
	{
		KanjiInfo kanjiInfo = new KanjiInfo (kanjiJson, PlayN.json()) ;
		List<Integer> scList = kanjiInfo.getStrokeCountList () ;
		assertFalse(scList.isEmpty ()) ;
		assertEquals(Integer.valueOf(14), scList.get(0)) ;
	}
	
	
	@Test
	public void shouldGetKunYomiReadings ()
	{
		KanjiInfo kanjiInfo = new KanjiInfo (kanjiJson, PlayN.json()) ;
		List<String> readingList = kanjiInfo.getKunYomiReadings () ;
		assertEquals(2, readingList.size ()) ;
		assertEquals("みみざと.い", readingList.get(0)) ;
		assertEquals("さと.い", readingList.get(1)) ;
	}
	
	
	@Test
	public void shouldGetOnYomiReadings ()
	{
		KanjiInfo kanjiInfo = new KanjiInfo (kanjiJson, PlayN.json()) ;
		List<String> readingList = kanjiInfo.getOnYomiReadings () ;
		assertEquals(1, readingList.size ()) ;
		assertEquals("ソウ", readingList.get(0)) ;
	}
	
	
	@Test
	public void shouldGetMeaning ()
	{
		KanjiInfo kanjiInfo = new KanjiInfo (kanjiJson, PlayN.json()) ;
		List<String> meaningList = kanjiInfo.getMeaning () ;
		assertEquals(2, meaningList.size ()) ;
		assertEquals("fast learner", meaningList.get(0)) ;
		assertEquals("wise", meaningList.get(1)) ;
	}
}
