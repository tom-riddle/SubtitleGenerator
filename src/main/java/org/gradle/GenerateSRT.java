package org.gradle;

import java.io.FileInputStream;
import java.io.PrintWriter;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Transcribe a continuous audio file that
 * has multiple utterances in it.
 */
public class GenerateSRT {

	private static final String ACOUSTIC_MODEL_PATH =
            "resource:/edu/cmu/sphinx/models/en-us/en-us";
    
	private static final String DICTIONARY_PATH =
            "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    
	private static final String LANGUAGE_MODEL_PATH =
			"resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin";
    
    public static void getSrt(String filePath) throws Exception {
        System.out.println("Loading models...");

        Configuration configuration = new Configuration();

        // Load model from the jar
        configuration
        .setAcousticModelPath(ACOUSTIC_MODEL_PATH);

        configuration
        	.setDictionaryPath(DICTIONARY_PATH);
        
        configuration
        	.setLanguageModelPath(LANGUAGE_MODEL_PATH);
     
        
        String filename_sans_extension = filePath.substring(0, filePath.length()-4);
        
        PrintWriter writer = new PrintWriter(filename_sans_extension+".srt", "UTF-8");
        
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        recognizer.startRecognition(new FileInputStream(filePath));       
        SpeechResult result; 
        int line_number=1;
        long startTime = System.nanoTime();
      
        while ((result = recognizer.getResult()) != null) {
        	int numOfWords = result.getWords().size();
        	if(numOfWords <=0)
        		continue;
        	writer.println(line_number);
        	writer.println(func2(result.getWords().get(0).getTimeFrame().getStart()) + " --> " +
        	func2(result.getWords().get(numOfWords-1).getTimeFrame().getEnd()) );
        	writer.println(result.getHypothesis());
        	writer.println();
        	line_number++;
        }
       writer.close();
       
       long endTime = System.nanoTime();
       System.out.println("Took "+(endTime - startTime) + " ns");
    }
    
    private static String func2(long n) {
        return func1(n / 3600000000l, 2) + ":" + func1(n / 60000 % 60, 2) + ":" 
                + func1(n / 1000 % 60, 2) + "," + func1(n % 1000, 3);
    }

    private static String func1(long n, int i) {
        String s = String.valueOf(n);
        while (s.length() < i) {
            s = "0" + s;
        }
        return s;
    }
}