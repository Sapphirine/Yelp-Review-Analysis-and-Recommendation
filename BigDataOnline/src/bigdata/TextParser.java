package bigdata;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;


public class TextParser
{
	private StanfordCoreNLP pipeline;

	public TextParser()
	{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	/**
	 * Filter text, only keep digits and letters
	 * @param text
	 * @return
	 */
	public String removeSpecialCharacter(String text)
	{
		StringBuffer buffer = new StringBuffer();
		
		text = text.toLowerCase();
		int length = text.length();
		
		for (int i = 0; i < length; ++i)
		{
			char c = text.charAt(i);
			if (Character.isLetterOrDigit(c))
			{
				buffer.append(c);
			}
		}
		
		if (buffer.length() == 0)
		{
			return null;
		}
		else
		{
			return buffer.toString();
		}
	}
	
	/**
	 * Remove special chars, unimportant parts and stop words
	 * @param text
	 * @return
	 */
	public Vector<String> parseText(String text)
	{
		Vector<String> wordList = new Vector<String>();
		
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for (CoreMap sentence: sentences)
		{
			for (CoreLabel token: sentence.get(TokensAnnotation.class))
			{
				String word = removeSpecialCharacter(token.lemma());
				if (word == null || StopWords.checkStop(word)) continue;
				String tag = token.tag();
				if (!POSChecker.checkTag(tag)) continue;
				
				wordList.add(word);
			}
		}
		
		return wordList;
	}
}
