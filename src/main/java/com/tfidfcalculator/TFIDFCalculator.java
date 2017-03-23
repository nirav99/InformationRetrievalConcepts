package com.tfidfcalculator;
import java.io.*;
import java.util.*;

import com.sun.jersey.core.impl.provider.entity.Inflector;

/**
 * Calculates TF-IDF score for words from a set of files from a given directory
 * @author nirav99
 *
 */
public class TFIDFCalculator
{
  private ArrayList<TextDocument> textDocumentList;
  private Inflector inflector;
  
  public TFIDFCalculator(ArrayList<File> directoryList)
  {
  	textDocumentList = new ArrayList<TextDocument>();
  	inflector = Inflector.getInstance();
  	
  	for(File directory : directoryList)
  	  processDirectory(directory);
  }
  
  private void processDirectory(File directory)
  {
  	String[] listOfFiles = directory.list();
  	
  	for(String file : listOfFiles)
  	{
  		if(file.endsWith(".html"))
  		{
  			try
  			{
  				processFile(directory, file);
  			}
  			catch(IOException ie)
  			{
  				ie.printStackTrace();
  			}
  		}
  	}
  }
  
  private void processFile(File directory, String dataFile)  throws IOException
  {
  	String fileText = readFile(directory, dataFile);
  	
  	if(fileText != null && !fileText.isEmpty())
  	  processText(fileText, dataFile, directory);
  }
  
  private String readFile(File directory, String dataFile) throws IOException
  {
  	StringBuilder content = new StringBuilder();
  	File inputFile = new File(directory.getAbsolutePath() + File.separator + dataFile);
  	
  	String line;
  	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
  	
  	while((line = reader.readLine()) != null)
  	{
  		content.append(line).append("\n");
  	}
  	reader.close();
  	
  	return content.toString();
  }
  
  /**
   * Processes all the text from the given file
   * @param text
   * @param fileName
   */
  private void processText(String text, String fileName, File directory)
  {
    TextDocument textDocument = new TextDocument(fileName, text, directory, inflector);
    textDocumentList.add(textDocument);
  }
  
  public void calculateTFIDF()
  {
    for(TextDocument textDocument : textDocumentList)
    	textDocument.calculateTFIDFForDocument(textDocumentList);
    
    for(TextDocument textDocument : textDocumentList)
    	textDocument.showTopScores();
  }
  
  public void calculateMutualInformation()
  {
  	MutualInformationCalculator mic = new MutualInformationCalculator();
  	
  	for(int i = 0; i < textDocumentList.size(); i++)
  	{
  		mic.processDocument(textDocumentList.get(i));
    }
  	
  	mic.calculateMutualInformation();
  }
  
  public static void main(String[] args)
  {
  	try
  	{
  		ArrayList<File> inputDirList = new ArrayList<File>();
  		inputDirList.add(new File("/Users/nirav99/Documents/JavaPrograms/NLPToolsTest/Data/locomotives"));
  		inputDirList.add(new File("/Users/nirav99/Documents/JavaPrograms/NLPToolsTest/Data/airplanes"));
  		TFIDFCalculator calc = new TFIDFCalculator(inputDirList);
  		calc.calculateTFIDF();
  		calc.calculateMutualInformation();
  	}
  	catch(Exception e)
  	{
  		System.err.println(e.getMessage());
  		e.printStackTrace();
  	}
  }
}
