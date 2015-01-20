/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * 
	 * @param filename
	 *            : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException
	 *             In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// Initializing Variables
		BufferedReader bufferReader = null;
		Document doc = new Document();
		// Main function of the method
		try {
			if (filename == null)
				throw new FileNotFoundException();
			String fileSeperator = Pattern.quote(System
					.getProperty("file.separator"));
			String[] splitString = filename.split(fileSeperator);
			if (splitString.length < 2)
				throw new FileNotFoundException();
			doc.setField(FieldNames.FILEID, splitString[splitString.length - 1]);
			doc.setField(FieldNames.CATEGORY,
					splitString[splitString.length - 2]);
			bufferReader = new BufferedReader(new FileReader(filename));
			// Need to find an alternative approach for this
			if (bufferReader == null)
				throw new IOException();
			String line;
			int firstLineFlag = 0;
			int placeDateFlag = 0;
			StringBuilder contentBuilder = new StringBuilder();
			while ((line = bufferReader.readLine()) != null) {
				if (line.equals("") || line.equals("\n") || line.equals("\t"))
					continue;
				if (firstLineFlag == 0) {
					doc.setField(FieldNames.TITLE, line.trim());
					firstLineFlag = 1;
				} else if (line.toLowerCase().contains("<author>")
						&& line.toLowerCase().contains("</author>")) {

					String authorDetails = line.substring((line.toLowerCase()
							.indexOf("<author>") + ("<author>").length()), line
							.toLowerCase().indexOf("</author>"));

					String authorName = null;
					String authorOrg = null;
					if (authorDetails.contains(",")) {
						authorName = authorDetails
								.substring(
										(authorDetails.toLowerCase().indexOf(
												"by") + 3),
										authorDetails.indexOf(",")).trim();
						authorOrg = authorDetails.substring(
								authorDetails.indexOf(",") + 1).trim();
					} else
						authorName = authorDetails
								.substring(
										(authorDetails.toLowerCase().indexOf(
												"by") + 3)).trim();
					// Need to find an alternative approach for this
					if (authorName.contains(" and "))
						doc.setField(FieldNames.AUTHOR,
								authorName.split(" and "));
					else if (authorName.contains(" AND "))
						doc.setField(FieldNames.AUTHOR,
								authorName.split(" AND "));
					else
						doc.setField(FieldNames.AUTHOR, authorName);

					if (authorOrg != null)
						doc.setField(FieldNames.AUTHORORG, authorOrg);

				} else if (placeDateFlag == 0 && line.contains("-")
						&& line.split("-")[0].contains(",")) {

					String placeDate = line.split("-")[0].trim();

					// Trimming unwanted special characters, if any, at the
					// end of the string
					Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
					Matcher matcher = pattern
							.matcher(placeDate.substring(0, 1));
					boolean flag = matcher.find();
					if (flag == true)
						placeDate = placeDate.substring(1);
					matcher = pattern.matcher(placeDate.substring(
							placeDate.length() - 1, placeDate.length()));
					flag = matcher.find();
					if (flag == true)
						placeDate = placeDate.substring(0,
								placeDate.length() - 1);

					Pattern alphaPattern = Pattern.compile("[A-Za-z]");
					Pattern numbPattern = Pattern.compile("[0-9]");
					String[] placeDateTokens = placeDate.split(",");
					String placeDetails = "";
					int placeFlag = 0;
					for (int j = placeDateTokens.length - 1; j >= 0; j--) {
						String dateString = placeDateTokens[j];
						String[] months = { "jan", "feb", "mar", "apr", "may",
								"jun", "jul", "aug", "sep", "oct", "nov", "dec" };
						for (String month : months) {
							matcher = alphaPattern.matcher(dateString);
							boolean charFlagMatch = matcher.find();
							matcher = numbPattern.matcher(dateString);
							boolean numbFlagMatch = matcher.find();
							if (charFlagMatch && numbFlagMatch
									&& dateString.toLowerCase().contains(month)) {
								doc.setField(FieldNames.NEWSDATE,
										dateString.trim());
								placeFlag++;
								break;
							}
						}
						if (placeFlag == 1) {
							placeFlag++;
							continue;
						} else if (placeFlag == 2) {
							if (placeDetails.equals(""))
								placeDetails = dateString;
							else
								placeDetails = dateString + "," + placeDetails;
						}
					}
					if (placeFlag == 0)
						placeDetails = placeDate;
					doc.setField(FieldNames.PLACE, placeDetails.trim());
					contentBuilder
							.append(line.substring(((line.indexOf("-")) + 1)));
					contentBuilder.append(" ");
					placeDateFlag = 1;
				} else {
					placeDateFlag = 1;
					contentBuilder.append(line);
					contentBuilder.append("\n");
					// Rest is body content
				}

			}
			String content = contentBuilder.toString();
			doc.setField(FieldNames.CONTENT, content.trim());
			bufferReader.close();
		} catch (FileNotFoundException e) {
			throw new ParserException("Input file -" + filename
					+ "- not found!!");
		} catch (IOException e) {
			throw new ParserException("Unable to read data from the file: "
					+ filename);
		} catch (Exception e) {
			throw new ParserException(
					"Unexpected error encountered while parsing the file: "
							+ filename);
		}
		return doc;
	}

}
