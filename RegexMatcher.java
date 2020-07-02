package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class RegexMatcher {

	public static boolean isMatchFound(String pattern, String input){
		
		 Pattern r = Pattern.compile(pattern);
		 Matcher m = r.matcher(input);
		 if (m.find( )) {
			 return true;
		 }
		return false;	
	}
	
	public static String getString(String pattern, String input){
		Pattern r = Pattern.compile(pattern);
		 Matcher m = r.matcher(input);
		 if (m.find( )) {
			return input.substring(m.start(),m.end());
		 }
		return "";
	}
	
	public static int getCountofOccurence(String pattern, String input){
		Pattern r = Pattern.compile(pattern);
		 Matcher m = r.matcher(input);
		 int count = 0;
		 while(m.find()) {
		         count++;
		      }
		return count;
	}
	
}
