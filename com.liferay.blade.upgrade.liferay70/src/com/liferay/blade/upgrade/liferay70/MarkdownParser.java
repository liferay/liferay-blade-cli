package com.liferay.blade.upgrade.liferay70;

import aQute.lib.io.IO;

import com.liferay.markdown.converter.factory.MarkdownConverterFactoryUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class MarkdownParser {

	private final static Map<String, Map<String, String>> _markdowns = new HashMap<>();

	public static Map<String, String> parse(String fileName) {
		Map<String, String> retval = _markdowns.get(fileName);

		if (retval == null) {
			try {
				final String markdown = new String(IO.read(MarkdownParser.class.getResourceAsStream(fileName)));
				final String html = MarkdownConverterFactoryUtil.create().convert(markdown);

				Map<String, String> sections = parseHtml(html);

				_markdowns.put(fileName, sections);

				retval = sections;
			} catch (IOException e) {
			}
		}

		return retval;
	}

	private static Map<String, String> parseHtml(String html) {
		Map<String, String> retval = new HashMap<>();

		Document document = Jsoup.parse(html);
		Elements elements = document.select("a[href] > h3");

		for (Element h3 : elements)  {
			Element a = h3.parent();
			int index = a.siblingIndex();
			List<Node> siblings = a.siblingNodes();

			StringBuilder sb = new StringBuilder();
			List<Node> interesting = new ArrayList<>();

			for (int i = index; i < siblings.size(); i++) {
				Node sibling = siblings.get(i);

				if (sibling.toString().startsWith("<hr")) {
					break;
				}
				else {
					interesting.add(sibling);
				}
			}

			for (Node node : interesting) {
				sb.append(node.toString());
			}

			String href = a.attr("href");

			retval.put(href, sb.toString());
		}

		return retval;
	}

	public static String getSection(String fileName, String sectionKey) {
		String retval = null;

		if (sectionKey.equals("#legacy")) {
			retval = "#legacy";
		}
		else {
			final Map<String, String> sections = parse(fileName);

			if (sections != null) {
				retval = sections.get(sectionKey);
			}
		}

		return retval;
	}
}
