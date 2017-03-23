package com.tfidfcalculator;
import java.util.Comparator;

/**
 * TF-IDF score for a word for a given document
 * @author nirav99
 *
 */
public class TFIDF
{
  private String word;
  
  private double tfidf;
  
  public TFIDF(String word, int freq, int totalWordsInDocument, int numDocsWhereWordPresent, int totalDocs)
  {
  	this.word = word;
    
  	double termFrequency = 1.0 * freq / totalWordsInDocument;
  	
  	double inverseDocFrequency = Math.log( 1.0 * totalDocs / (1 + numDocsWhereWordPresent));
  	
  	tfidf = termFrequency * inverseDocFrequency;
  }
  
  public double tfIdf()
  {
  	return this.tfidf;
  }
  
  @Override
  public String toString()
  {
  	return word + " : " + String.format("%.4f", tfidf);
  }
}

class TFIDFComparator implements Comparator<TFIDF>
{

	@Override
	public int compare(TFIDF o1, TFIDF o2)
	{
    if(o1.tfIdf() > o2.tfIdf())
    	return -1;
    else
    if(o1.tfIdf() < o2.tfIdf())
    	return 1;
    else
    	return 0;
	}
}