package com.wi.main.recognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.wi.main.domain.Nature;
import com.wi.main.domain.NewWord;
import com.wi.main.util.Graph;


/**
 * 基于规则的新词发现
 * 
* @For WI Cloud
 * 
 */
public class RuleRecognition {

	private static Map<String, String> ruleMap = new HashMap<String, String>();

	static {
		ruleMap.put("《", "》");
	}

	public static List<NewWord> recognition(Graph graph) {
		return recognition(graph.chars);
	}

	public static List<NewWord> recognition(char[] chars) {
		String end = null;
		StringBuilder sb = null;
		String name;
		List<NewWord> result = new ArrayList<NewWord>();
		for (int i = 0; i < chars.length; i++) {
			name = String.valueOf(chars[i]);
			if (end == null) {
				if ((end = ruleMap.get(name)) != null) {
					sb = new StringBuilder();
				}
			} else {
				if (end.equals(name)) {
					result.add(new NewWord(sb.toString(), Nature.NW, -sb
							.length()));
					sb = null;
					end = null;
				} else {
					sb.append(name);
				}
			}
		}
		return result;
	}
}
