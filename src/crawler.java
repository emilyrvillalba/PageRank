import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class crawler {

	public static void main(String[] args) throws IOException {
		String url = "https://www.cpp.edu";
		Map<String, ArrayList<String>> list = new HashMap<String, ArrayList<String>>();
		crawl(1, url, new ArrayList<String>(), list);
	}
	
	private static void crawl(int level, String url, ArrayList<String> visited, 
			Map<String, ArrayList<String>> list) throws IOException {
		
		Document doc = request(url, visited, list);
		
		//if (doc != null && level < 2) {
		if (doc != null) {
			// find url
			for(Element link : doc.select("a[href]")) {
				String next_link = link.absUrl("href");
				//System.out.println("Next Link: " + next_link);
				if(visited.contains(next_link) == false && next_link.startsWith("https://www.cpp.edu")) {
					crawl(level++, next_link, visited, list);
				}
				else
					doc = request(url, visited, list);
			}
		}
	}
	
	private static Document request(String url, ArrayList<String> v, Map<String, ArrayList<String>> list) throws IOException {
		// from jsoup
		Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").get();
		//System.out.println("Link: " + url);
		
		String next_link = "";
		
		// extract url
		for(Element link : doc.select("a[href]")) {
			next_link = link.absUrl("href");
			System.out.println("url: " + url);
			System.out.println("nextlink: " + next_link);
		}
			
		if (list.get(next_link) == null) {
			list.put(next_link, new ArrayList<String>());
		}
		list.get(url).add(next_link);
		
		//System.out.println(doc.title());
		v.add(url);
		return doc;
	}
}
