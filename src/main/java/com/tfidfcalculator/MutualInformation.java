package com.tfidfcalculator;

import java.util.Comparator;

public class MutualInformation
{
  private String word;
  private double mutualInformationScore;
  
  public MutualInformation(String word, double miScore)
  {
  	this.word = word;
  	this.mutualInformationScore = miScore;
  }
  
  public String word()
  {
  	return word;
  }
  
  public double mutualInformationScore()
  {
  	return this.mutualInformationScore;
  }
  
  @Override
  public String toString()
  {
  	return word + " --> " + String.format("%.4f", this.mutualInformationScore);
  }
}

class MutualInformationComparator implements Comparator<MutualInformation>
{
	@Override
	public int compare(MutualInformation mi1, MutualInformation mi2)
	{
		if(mi1.mutualInformationScore() > mi2.mutualInformationScore())
			return -1;
		else
		if(mi1.mutualInformationScore() < mi2.mutualInformationScore())
		  return 1;
		else
			return 0;
	}
}