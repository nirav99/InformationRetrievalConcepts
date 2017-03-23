package com.tfidfcalculator;

import java.util.*;

/**
 * Calculates the word frequencies and other stuff for the given topic
 * @author nirav99
 *
 */
public class MutualInformationCalculator
{
	// airplanes is class 1
	// locomotives is class 2
  private HashMap<String, Integer> wordDocumentCountClass1; // How many documents in class 1 contain the word
  private HashMap<String, Integer> wordDocumentCountClass2; // How many documents in class 2 contain the word
  
  private HashMap<String, Integer> wordCountClass1; // Number of times word occurred in all documents of class 1
  private HashMap<String, Integer> wordCountClass2; // Number of times word occurred in all documents of class 2
  
  private HashSet<String> wordSet;
  
  private int numDocumentsClass1;
  private int numDocumentsClass2;
  
  private ArrayList<MutualInformation> mutualInformationListClass1;
  private ArrayList<MutualInformation> mutualInformationListClass2;
  
  public static boolean DEBUG_MODE = false;
  
  public MutualInformationCalculator()
  {
  	wordDocumentCountClass1 = new HashMap<String, Integer>();
  	wordDocumentCountClass2 = new HashMap<String, Integer>();
  	
  	wordCountClass1 = new HashMap<String, Integer>();
  	wordCountClass2 = new HashMap<String, Integer>();
  	
  	wordSet = new HashSet<String>();
  	
  	mutualInformationListClass1 = new ArrayList<MutualInformation>();
  	mutualInformationListClass2 = new ArrayList<MutualInformation>();
  }
  
  public void processDocument(TextDocument textDocument)
  {
  	HashMap<String, Integer> wordDocumentCountClass = textDocument.className().equals("airplanes") ?
  			                     wordDocumentCountClass1 :
  			                     wordDocumentCountClass2;
  	
  	HashMap<String, Integer> wordCountClass = textDocument.className().equals("airplanes") ?
                             wordCountClass1 :
                             wordCountClass2;
  	
  	if(textDocument.className().equals("airplanes"))
  		numDocumentsClass1++;
  	else
  		numDocumentsClass2++;
  	
  	processDocumentHelper(textDocument, wordDocumentCountClass, wordCountClass);

  }
  
  /**
   * Increment documentCount by 1 for each word that occurs in the specified class
   * @param textDocument
   * @param wordDocumentCountClass
   */
  private void processDocumentHelper(TextDocument textDocument, HashMap<String, Integer> wordDocumentCountClass,
                                      HashMap<String, Integer> wordCountClass)
  {
  	Set<String> keySet = textDocument.wordCountMap().keySet();
  	
  	Integer documentCount;
  	Integer wordCount;
  	
  	for(String key : keySet)
  	{
  		documentCount = wordDocumentCountClass.get(key);
  		if(documentCount == null)
  			documentCount = 0;
  		documentCount = documentCount + 1;
  		
  		wordDocumentCountClass.put(key, documentCount);
  		
  		wordSet.add(key);
  		
  		wordCount = wordCountClass.get(key);
  		if(wordCount == null)
  			wordCount = 0;
  		wordCount = wordCount + textDocument.wordCountMap().get(key);
  		
  		wordCountClass.put(key, wordCount);
  	}
  }
  
  public void calculateMutualInformation()
  {
  	Integer numOccurrencesClass1;
  	Integer numOccurrencesClass2;
  	
  	for(String word : wordSet)
  	{
  		numOccurrencesClass1 = wordDocumentCountClass1.get(word);
  		numOccurrencesClass2 = wordDocumentCountClass2.get(word);
  		
  		if(numOccurrencesClass1 == null)
  			numOccurrencesClass1 = 0;
  		if(numOccurrencesClass2 == null)
  			numOccurrencesClass2 = 0;
  		
  		double miScore = mutualInformationHelper(word, numOccurrencesClass1, numOccurrencesClass2, this.numDocumentsClass1, this.numDocumentsClass2);
  		mutualInformationListClass1.add(new MutualInformation(word, miScore));
  		
  		miScore = mutualInformationHelper(word, numOccurrencesClass2, numOccurrencesClass1, this.numDocumentsClass2, this.numDocumentsClass1);
  		mutualInformationListClass2.add(new MutualInformation(word, miScore));
  	}
  	
  	System.out.println(numDocumentsClass1 + " , " + numDocumentsClass2);
  	Collections.sort(mutualInformationListClass1, new MutualInformationComparator());
  	Collections.sort(mutualInformationListClass2, new MutualInformationComparator());
  	
  	/*
  	System.out.println("Showing mutual information score Class 1 :");
  	for(int i = 0;  i < 20 && i < mutualInformationListClass1.size(); i++)
  		System.out.println(mutualInformationListClass1.get(i).toString());
  	
  	System.out.println("==========================");
  	System.out.println("Showing mutual information score Class 2 :");
  	for(int i = 0;  i < 20 && i < mutualInformationListClass2.size(); i++)
  		System.out.println(mutualInformationListClass2.get(i).toString());
  	*/
  	
  	showImportantWordsForEachClass(mutualInformationListClass1);
  }
  
  public static double mutualInformationHelper(String word, int numOccurrencesClass1, int numOccurrencesClass2, 
                                               int numDocumentsClass1, int numDocumentsClass2)
  {
  	int N = numDocumentsClass1 + numDocumentsClass2;
  	int n11 = numOccurrencesClass1;
  	int n10 = numOccurrencesClass2;
  	
  	int n01 = (numDocumentsClass1 - n11);
  	
  	
  	int n00 = numDocumentsClass2 - n10;
  	
  	if(DEBUG_MODE)
  	{
  	  System.out.println("n11 = " + n11);
  	  System.out.println("n10 = " + n10);
  	  System.out.println("n01 = " + n01);
  	  System.out.println("n00 = " + n00);
  	  System.out.println("N = " + N);
  	}
  	
  	int n1_ = n11 + n10;
  	int n_1 = n01 + n11;
  	
  	int n0_ = n00 + n01;
  	int n_0 = n10 + n00;
  	
  	if(DEBUG_MODE)
  	{
   	  System.out.println("n1_ = " + n1_);
  	  System.out.println("n_1 = " + n_1);
  	  System.out.println("n0_ = " + n0_);
  	  System.out.println("n_0 = " + n_0);
  	}
  	double mutualInformation = 0;
  	
  	double term1 = calculateTerm(n11, n1_, n_1, N); //(n1_ > 0 && n_1 > 0 && n11 > 0) ? 1.0 * n11/N * Math.log(1.0 * N * n11 / (n1_ * n_1)) : 0;
  	double term2 = calculateTerm(n01, n0_, n_1, N); //(n0_ > 0 && n_1 > 0 && n01 > 0) ? 1.0 * n01/N * Math.log(1.0 * N * n01 / (n0_ * n_1)) : 0;
  	double term3 = calculateTerm(n10, n1_, n_0, N); // ? 1.0 * n10/N * Math.log(1.0 * N * n10 / (n1_ * n_0)) : 0;
  	double term4 = calculateTerm(n00, n0_, n_0, N); //(n0_ > 0 && n_0 > 0 && n00 > 0) ? 1.0 * n00/N * Math.log(1.0 * N * n00 / (n0_ * n_0)) : 0;
  	
  	mutualInformation = term1 + term2 + term3 + term4; 
  	
  	return mutualInformation;
//  	mutualInformationList.add(new MutualInformation(word, mutualInformation));
  }
  
  private static double calculateTerm(int numerator, int denom1, int denom2, int N)
  {
  	double result = 0;
  	
  	if(numerator == 0 || denom1 == 0 || denom2 == 0)
  		return 0.0;
  	
 // 	System.out.println(numerator + " " + denom1 + " " + denom2 + " " + N);
  	double temp = 1.0 * N / denom1 * numerator / denom2;
 // 	System.out.println("temp = " + temp + " log temp = " + Math.log(temp));
  	
  	result = 1.0 * numerator / N * Math.log(temp);
 // 	System.out.println("result = " + result);
  	return result;
  }
  
  private static double calculateTerm2(int numerator, int denom1, int denom2, int N)
  {
  	double result = 0;
  	
  	if(numerator == 0 || denom1 == 0 || denom2 == 0)
  		return 0.0;
  	
  	System.out.println(numerator + " " + denom1 + " " + denom2 + " " + N);
  	double temp = 1.0 * N * numerator / (denom1 * denom2);
  	System.out.println("temp = " + temp + " log temp = " + Math.log(temp));
  	
  	result = 1.0 * numerator / N * Math.log(temp);
  	System.out.println("result = " + result);
  	return result;
  }
  
  private void showImportantWordsForEachClass(ArrayList<MutualInformation> mutualInformationListClass)
  {
  	Integer freqClass1 = null;
  	Integer freqClass2 = null;
  	String word;
  	
  	HashSet<MutualInformation> importantWordsClass1 = new HashSet<MutualInformation>();
  	HashSet<MutualInformation> importantWordsClass2 = new HashSet<MutualInformation>();
  	
  	for(MutualInformation mi : mutualInformationListClass)
  	{
  		word = mi.word();
  		freqClass1 = wordCountClass1.get(word);
  		freqClass2 = wordCountClass2.get(word);
  		
  		if(freqClass1 != null && freqClass2 != null && freqClass1.intValue() != freqClass2.intValue())
  		{
  			importantWordsClass1.add(mi);
  			importantWordsClass2.add(mi);
  		}
  		else
  		if(isNonZero(freqClass1) && !isNonZero(freqClass2))
  			importantWordsClass1.add(mi);
  		else
  		if(isNonZero(freqClass2) && !isNonZero(freqClass1))
  			importantWordsClass2.add(mi);
 // 		else
 // 		if(isNonZero(freqClass1) && isNonZero(freqClass2))
 // 			System.out.println("Word : " + word + " present in both classes. Discarding it");
  	}
  	
  	showImportantWords(importantWordsClass1, "Airplanes");
  	showImportantWords(importantWordsClass2, "Locomotives");
  }
  
  private void showImportantWords(Set<MutualInformation> classSpecificMIList, String className)
  {
  	ArrayList<MutualInformation> miList = new ArrayList<MutualInformation>(classSpecificMIList);
  	Collections.sort(miList, new MutualInformationComparator());
  	
  	System.out.println("Class : " + className);
  	for(int i = 0; i < miList.size() && i < 25; i++)
  		System.out.println("  " + miList.get(i).word()); // + " " + miList.get(i).mutualInformationScore());
  	System.out.println("===============================");
  }
  
  private boolean isNonZero(Integer intValue)
  {
    return intValue != null && intValue != 0;	
  }
  
  public static void main(String[] args)
  {
  	try
  	{
  		double miScore = MutualInformationCalculator.mutualInformationHelper("poultry", 49, 27652, 190, 801758);
  		System.out.println("MI score : " + miScore);
  	}
  	catch(Exception e)
  	{
  		System.err.println(e.getMessage());
  		e.printStackTrace();
  	}
  }
}
