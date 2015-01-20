package edu.buffalo.cse.irf14;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class DataReader {

	public static void main(String args[]) {
		IndexReader ir = new IndexReader("IndexDirectory", IndexType.PLACE);

		int totalKeyterms = ir.getTotalKeyTerms();
		System.out.println("Total Key Terms: " + totalKeyterms);

		int totalValueTerms = ir.getTotalValueTerms();
		System.out.println("Total Value Terms: " + totalValueTerms);

		String searchText = "WASHINGTON";
		Map<String, Integer> postingTermData = ir.getPostings(searchText);
		// TEMP PRINT
		if (postingTermData != null) {
			System.out.println("#############################");
			Set<Entry<String, Integer>> set2 = postingTermData.entrySet();
			Iterator<Entry<String, Integer>> iterator2 = set2.iterator();
			while (iterator2.hasNext()) {
				Map.Entry<String, Integer> mapEntry = (Map.Entry<String, Integer>) iterator2
						.next();
				System.out.println(mapEntry.getKey() + ": "
						+ mapEntry.getValue());
			}
		}
		int k = 10;
		List<String> topKStrings = ir.getTopK(k);
		System.out.println("The top " + k
				+ "terms with most postings (in the decreasing order) are: "
				+ topKStrings);

		String term1 = "chennai";
		String term2 = "benguluru";
		String term3 = "hyderabad";
		Map<String, Integer> queryResult = ir.query(term1, term2, term3);

		// ------------------------------------------------------------------------------------------
		// TEMP PRINT
		System.out.println("#############################");
		Set<Entry<String, Integer>> set1 = queryResult.entrySet();
		Iterator<Entry<String, Integer>> iterator1 = set1.iterator();
		while (iterator1.hasNext()) {
			Map.Entry<String, Integer> mapEntry = (Map.Entry<String, Integer>) iterator1
					.next();
			System.out.println(mapEntry.getKey() + ": " + mapEntry.getValue());
		}
		// TEMP PRINT ENDS HERE
		// ------------------------------------------------------------------------------------------

	}
}
