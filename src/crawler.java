import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler {
	private static final int MAX_PAGES = 1000;
	private static Set<String> pagesVisited = new HashSet<String>();
	private static Queue<String> pagesToVisit = new LinkedList<String>();
	private static Map<String, ArrayList<String>> list = new HashMap<String, ArrayList<String>>();
	
	public static void main(String args[]) throws IOException {
		bfs("http://www.cpp.edu");
		showResults();
		writeResults();
	}
	
	private static void bfs(String root) throws IOException {
		pagesToVisit.add(root);
		BufferedReader br = null;
		boolean start = true;
		
		while(!pagesToVisit.isEmpty()) {
				
			String crawledUrl = pagesToVisit.poll();
//			while (!crawledUrl.contains("cpp") && !pagesToVisit.isEmpty() && !pagesVisited.contains(crawledUrl))
//			{
//				System.out.println("Rejected Site: " + crawledUrl);
//				crawledUrl = pagesToVisit.poll();
//			}
			System.out.println("\n**** Site crawled: " + crawledUrl + " *****");
			
			if (list.get(crawledUrl) == null && crawledUrl.contains("cpp")) {
				list.put(crawledUrl, new ArrayList<String>());
			}
			
			if (pagesVisited.size() > MAX_PAGES)
				return;
			
			boolean ok = false;
			URL url = null;
			while (!ok) {
				try {
					url = new URL(crawledUrl);
					br = new BufferedReader(new InputStreamReader(url.openStream()));
					ok = true;
				} catch (MalformedURLException e) {
					System.out.println("Malformed URL: " + crawledUrl);
					crawledUrl = pagesToVisit.poll();
					ok = false;
				} catch (IOException ioe) {
					System.out.println("IOException URL: " + crawledUrl);
					crawledUrl = pagesToVisit.poll();
					ok = false;
				} 
			}
			
			request(crawledUrl, pagesToVisit, pagesVisited);
			pagesVisited.add(crawledUrl);

			System.out.println("Number of pages to visit: " + pagesToVisit.size());
			System.out.println("Number of pages visited: " + pagesVisited.size());
		}
	}
	
	private static void showResults() {
		System.out.println("Results: ");
		System.out.println("Sites crawled: " + pagesVisited.size());
		
		for (String s : pagesVisited) {
			System.out.println(s);
		}
		
		for (String s : list.keySet()) {
			System.out.println("root url: " + s);
			for (String r : list.get(s)) {
				System.out.print(r + " ");
			}
			System.out.println();
		}
	}
	
	private static void writeResults() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("results.csv");
		StringBuilder sb = new StringBuilder();
		
		for (String s : list.keySet()) {
			sb.append("root: ");
			sb.append(',');
			sb.append(s);
			sb.append(',');
			sb.append("urls: ");
			sb.append(',');
			System.out.println("root url: " + s);
			for (String r : list.get(s)) {
				sb.append(r);
				sb.append(',');
			}
			sb.append("\n");
		}
		writer.write(sb.toString());
		writer.close();
	}
	
	private static void request(String url, Queue<String> pagesToVisit, Set<String> pagesVisited) {
		
		try {
			Document doc;
			doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").get();
			if (doc != null) {
				for (Element link : doc.select("a[href]")) {
					String nextlink = link.absUrl("href");
					if (nextlink.contains("www.cpp.edu") && !nextlink.contains(".pdf")
							&& !pagesVisited.contains(url)) {
						pagesToVisit.add(nextlink);
						// broken links
//						if (nextlink != "http://www.cpp.edu/~academic-programs/calendar/calendar.shtml") {
//							pagesToVisit.add(nextlink);
//						}
					}
					if (list.get(url) != null && nextlink.contains("www.cpp.edu")) {
						list.get(url).add(nextlink);
					}
				}
			}
		} catch (IOException e) {
		}
	}
}