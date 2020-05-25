package ca.gc.tbs.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class BadWords {

	static Map<String, String[]> words = new HashMap<String, String[]>();

	public class LengthComparator implements java.util.Comparator<String> {

		public int compare(String s1, String s2) {
			if (s1.length() > s2.length()) {
				return -1;
			} else if (s1.length() < s2.length()) {
				return 1;
			} else {
				return s1.compareTo(s2);
			}
		}
	}

	static int largestWordLength = 0;

	public static void loadConfigs() {
		loadGoogleConfigs();
		loadFileConfigs("/static/badwords/facebook_badwords_en.txt");
		loadFileConfigs("/static/badwords/youtube_badwords_en.txt");
		loadFileConfigs("/static/badwords/badwords_fr.txt");
		loadFileConfigs("/static/badwords/threats_fr.txt");
		loadFileConfigs("/static/badwords/threats_en.txt");
		System.out.println("Loaded " + words.size() + " words to filter out");
	}

	public static void loadFileConfigs(String filePath) {
		String[] ignore_in_combination_with_words = new String[] {};
		try {
			Resource resource = new ClassPathResource(filePath);
			Path path = Paths.get(resource.getURI());
			String newWords[] = new String(Files.readAllBytes(path)).split(",");
			for (String word : newWords) {
				word = word.trim();
				if (word.length() > largestWordLength) {
					largestWordLength = word.length();
				}
				if (!words.containsKey(word)) {
					words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void loadGoogleConfigs() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
					"https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv")
							.openConnection().getInputStream()));
			String line = "";

			while ((line = reader.readLine()) != null) {
				String[] content = null;
				try {
					content = line.split(",");
					if (content.length == 0) {
						continue;
					}
					String word = content[0];
					String[] ignore_in_combination_with_words = new String[] {};
					if (content.length > 1) {
						ignore_in_combination_with_words = content[1].split("_");
					}

					if (word.length() > largestWordLength) {
						largestWordLength = word.length();
					}
					words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String removeLeetSpeak(String input) {
		input = input.replaceAll("1", "i");
		input = input.replaceAll("!", "i");
		input = input.replaceAll("3", "e");
		input = input.replaceAll("4", "a");
		input = input.replaceAll("@", "a");
		input = input.replaceAll("5", "s");
		input = input.replaceAll("7", "t");
		input = input.replaceAll("0", "o");
		input = input.replaceAll("9", "g");
		input = input.replaceAll("\\$", "s");
		return input;
	}

	/**
	 * Iterates over a String input and checks whether a cuss word was found in a
	 * list, then checks if the word should be ignored (e.g. bass contains the word
	 * *ss).
	 * 
	 * @param input
	 * @return
	 */

	public static ArrayList<String> badWordsFound(String input) {
		if (input == null) {
			return new ArrayList<>();
		}

		ArrayList<String> badWords = new ArrayList<>();

		// iterate over each letter in the word
		for (int start = 0; start < input.length(); start++) {
			// from each letter, keep going to find bad words until either the end of the
			// sentence is reached, or the max word length is reached.
			for (int offset = 1; offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
				String wordToCheckOrig = input.substring(start, start + offset);
				String wordToCheck = removeLeetSpeak(wordToCheckOrig);
				wordToCheck = wordToCheck.toLowerCase().replaceAll("[^a-zA-Z]", "");
				if (words.containsKey(wordToCheck) && wordToCheck.length() > 0) {
					// for example, if you want to say the word bass, that should be possible.
					String[] ignoreCheck = words.get(wordToCheck);
					boolean ignore = false;
					for (int s = 0; s < ignoreCheck.length; s++) {
						if (input.contains(ignoreCheck[s])) {
							ignore = true;
							break;
						}
					}
					if (!ignore) {
						badWords.add(wordToCheckOrig);
					}
				}
			}
		}

		for (String s : badWords) {
			System.out.println(s + " qualified as a bad word in a username");
		}
		return badWords;

	}

	static String censor(String text) {

		// Break down sentence by ' ' spaces
		// and store each individual word in
		// a different list
		String[] word_list = text.split("\\s+");

		// A new string to store the result
		String result = "";

		// Iterating through our list
		// of extracted words
		int index = 0;
		for (String i : word_list) {
			String wordToCheckOrig = i;
			String wordToCheck = removeLeetSpeak(wordToCheckOrig);
			wordToCheck = wordToCheck.toLowerCase().replaceAll("[^a-zA-Z]", "");
			if (words.containsKey(wordToCheck)) {
				// changing the censored word to
				// created asterisks censor
				word_list[index] = createMask(wordToCheckOrig);
			}
			index++;
		}

		// join the words
		for (String i : word_list)
			result += i + ' ';

		return result;
	}

	public static String createMask(String word) {
		String mask = "";
		for (int i = 0; i < word.length(); i++) {
			mask += "#";
		}
		return mask;
	}

	public static String filterText(String input) {
		List<String> badWords = badWordsFound(input);
		badWords.sort(Comparator.comparingInt(String::length).reversed());
		for (String badWord : badWords) {
			String mask = createMask(badWord);
			input = StringUtils.replaceIgnoreCase(input, badWord, mask);
		}
		return input;
	}
}