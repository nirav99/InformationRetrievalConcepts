package com.tfidfcalculator;

import java.io.*;
import java.util.*;

import org.tartarus.snowball.ext.englishStemmer;

import com.sun.jersey.core.impl.provider.entity.Inflector;

/**
 * Encapsulates the content of one text document
 * @author nirav99
 *
 */
public class TextDocument
{
  private String name;  // Name of the file
  private HashMap<String, Integer> wordCountMap;
  private int totalWords;
  private String className; // Name of the corpus / classification to which this file belongs to
  
  private ArrayList<TFIDF> wordScoreList;
  
  // <br> is not a block tag but added to this regex as it must be rendered with a new line.
  public static final String HTML_BLOCK_MULTI_LETTER = "(address|article|aside|audio|blockquote|br|canvas|dd|div|dl|fieldset|figcaption|figure|footer|form|header|hgroup|hr|main|nav|noscript|ol|output|pre|section|table|tfoot|ul|video|tr|li)";
  public static final String HTML_BLOCK_SINGLE_LETTER = "(h1|h2|h3|h4|h5|h6|p)";
  
  public static final String HTML_BLOCK_MULTI_LETTER_START = "(?i)<" + HTML_BLOCK_MULTI_LETTER + "[^>]*?>";
  public static final String HTML_BLOCK_MULTI_LETTER_END = "(?i)</" + HTML_BLOCK_MULTI_LETTER + "[^>]*?>";
  public static final String HTML_BLOCK_SINGLE_LETTER_START = "(?i)<" + HTML_BLOCK_SINGLE_LETTER + "(/?>|\\s+[^>]*?>)";
  public static final String HTML_BLOCK_SINGLE_LETTER_END = "(?i)</" + HTML_BLOCK_SINGLE_LETTER + ">";
  
  private Inflector inflector;
  
  public TextDocument(String name, String pageHTML, File directory, Inflector inflector)
  {
  	this.name = name;
  	this.inflector = inflector;
  	
  	wordCountMap = new HashMap<String, Integer>();
  	buildWordMap(stripHTML(pageHTML));
  	getClassName(directory);
  }
  
  public boolean doesWordExist(String word)
  {
  	return wordCountMap.containsKey(word.toLowerCase());
  }
  
  public HashMap<String, Integer> wordCountMap()
  {
  	return this.wordCountMap;
  }
  
  public Integer getWordFrequency(String word)
  {
  	Integer frequency = wordCountMap.get(word.toLowerCase());
  	
  	return (frequency != null) ? frequency : 0;
  }
  
  public int totalWords()
  {
  	return totalWords;
  }
  
  public String name()
  {
  	return this.name;
  }
  
  public String className()
  {
  	return this.className;
  }
  
  /**
   * Calculates the TF-IDF score for each word in the given document
   * @param textDocumentList
   */
  public void calculateTFIDFForDocument(ArrayList<TextDocument> textDocumentList)
  {
  	wordScoreList = new ArrayList<TFIDF>();
  	
  	Set<String> wordSet = wordCountMap.keySet();
  	
  	for(String word : wordSet)
  	{
  		TFIDF tfidf = new TFIDF(word,
                              wordCountMap.get(word),
                              totalWords,
                              getNumberOfDocumentsWhereWordIsPresent(textDocumentList, word),
  				                    textDocumentList.size());
  		
  		wordScoreList.add(tfidf);
  	}
  }
  
  public void showTopScores()
  {
  	Collections.sort(wordScoreList, new TFIDFComparator());
  	
  	System.out.println("Document Name : " + this.name + " Corpus Name : " + this.className);
  	
  	for(int i = 0; i < 20 && i < wordScoreList.size(); i++)
  		System.out.println(wordScoreList.get(i).toString());
  	System.out.println("=======================");
  }
  /**
   * Returns the total number of documents where the given word is present
   * @param textDocumentList
   */
  private int getNumberOfDocumentsWhereWordIsPresent(ArrayList<TextDocument> textDocumentList, String word)
  {
  	int count = 0;
  	
  	for(int i = 0; i < textDocumentList.size(); i++)
  	{
  		if(textDocumentList.get(i).doesWordExist(word))
  			count++;
  	}
  	return count;
  }
  
  /**
   * Given the text calculates word counts for every word and builds the map
   * @param text
   */
  private void buildWordMap(String text)
  {
  	String[] words = text.toLowerCase().split("\\s+");
  	Integer wordCount;
  	
  	for(String word : words)
  	{
  		word = word.replaceFirst("^\\W*", "").replaceFirst("\\W*$", "");
/*  		
  		englishStemmer stemmer = new englishStemmer();
  		stemmer.setCurrent(word);
  		stemmer.stem();
  		word = stemmer.getCurrent();
*/  		
  		if(word.matches("[\\d\\-]+")) // Don't process numbers or dates
  			continue;
  		
  		word = inflector.singularize(word);
  		
  		totalWords++;
  		wordCount = wordCountMap.get(word);
  		
  		if(wordCount == null)
  			wordCount = 0;
  		
  		wordCount = wordCount + 1;
  		
  		wordCountMap.put(word, wordCount);
  	}
  }
  
  /**
   * The corpus name is the last directory's name
   * @param directory
   */
  private void getClassName(File directory)
  {
  	this.className = directory.getName();
  }
  
  private String stripHTML(String pageHTML)
  {
  	if(pageHTML != null)
  	{
      String textContent = pageHTML.replaceAll("(?i)<script[^>]*?>[\\s\\S]*?</script>", "").replaceAll("(?i)<style[^>]*?>[\\s\\S]*?</style>", "").replaceAll("(?i)<head[^>]*?>([\\s\\S]*?)</head>", "").replaceAll("<!--[\\s\\S]*?-->", " ");
      
      // This is required as some sites put page titles and meta tags in the HTML body !!! e.g. godiva.com
      textContent = textContent.replaceAll("(?i)<title[^>]*?>[\\s\\S]*?</title>", "").replaceAll("(?i)<meta\\s[^>]*?>", "");
      
      textContent = textContent.replaceAll("[\r\n]+", " ");
      textContent = textContent.replaceAll(HTML_BLOCK_MULTI_LETTER_START, "\n").replaceAll(HTML_BLOCK_MULTI_LETTER_END, "\n").replaceAll(HTML_BLOCK_SINGLE_LETTER_START,"\n").replaceAll(HTML_BLOCK_SINGLE_LETTER_END, "\n");
      textContent = textContent.replaceAll("<[^>]*?>", " ").replaceAll("[ \t]+", " ");
      textContent = textContent.replaceAll("\\&#\\d+;", " ");
      return textContent;
  	}
  	return null;
  }
}
