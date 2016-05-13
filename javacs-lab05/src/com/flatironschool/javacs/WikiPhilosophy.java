package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		List<String> listOfURLs = new ArrayList<String>();
        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        String dest = "https://en.wikipedia.org/wiki/Philosophy";
        int count = 0;
        while (true) {
        	Elements paragraphs = wf.fetchWikipedia(url);
        	listOfURLs.add(url);
        	count++;
        	String oldURL = url;
        
        	forloop:
	        for (int i = 0; i < paragraphs.size(); i++) {
	        	Element para = paragraphs.get(i);

	        	Iterable<Node> iter = new WikiNodeIterable(para);
	        	for (Node node: iter) {
	        		if (node.hasAttr("href")) {
	        			if (!isValidLink(node)) continue;

	        			String firstURL = node.attr("abs:href");
	        			System.out.println(firstURL);
	        			if (listOfURLs.contains(firstURL)) {
	        				System.out.println("Loop detected");
	        				return;
	        			} else if (firstURL.equals(dest)) {
	        				System.out.println("Found Philosophy!");
	        				System.out.println("It took " + count + " links to reach Philosophy");
	        				return;
	        			} else {
	        				url = firstURL;
	        				break forloop;
	        			}
	        		}
	        		
	        	}
	        }
	        if (url.equals(oldURL)) {
	        	System.out.println("Reached dead end");
	        	return;
	        }
        }

        // the following throws an exception so the test fails
        // until you update the code
        // String msg = "Complete this lab by adding your code and removing this statement.";
        // throw new UnsupportedOperationException(msg);
	}

	public static boolean isValidLink(Node node) {
		List<Node> siblings = node.siblingNodes();
		Node parent = node.parent();
		int siblingIndex = node.siblingIndex();
		int indexLeft = -1, indexRight = -1;

		String attr = node.attr("href");
		if (attr.charAt(0) == '#') return false;

		if (parent instanceof Element) {
			String tagName = ((Element)parent).tagName();
			if (tagName != "p") return false;
		}

		for (Node n: siblings) {
			if (n instanceof TextNode && n.siblingIndex() > siblingIndex) {
				int currentIndexLeft = ((TextNode)n).text().indexOf("(");
				if (currentIndexLeft != -1) {
					indexLeft = n.siblingIndex();
					break;
				}
			}
		}

		for (Node n: siblings) {
			if (n instanceof TextNode && n.siblingIndex() > siblingIndex) {
				int currentIndexRight = ((TextNode)n).text().indexOf(")");
				if (currentIndexRight != -1) {
					indexRight = n.siblingIndex();
					if (indexLeft == indexRight) {
						indexLeft = ((TextNode)n).text().indexOf("(");
						indexRight = ((TextNode)n).text().indexOf(")");
					}
					break;
				}
			}
		}

		
		if (indexLeft != -1 && indexRight != -1 && indexRight < indexLeft) {
			return false;
		} else if (indexLeft == -1 && indexRight > 0) {
			return false;
		} else {
			return true;
		}
	}
}
