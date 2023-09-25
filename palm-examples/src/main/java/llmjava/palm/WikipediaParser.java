package llmjava.palm;

// Import the Pattern and Matcher classes from the java.util.regex package
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.List;
import java.util.ArrayList;

/*
 * Parse a wikipedia page to extarct all sections within a page.
 */
public class WikipediaParser {

    public static class Section {
        int level;
        String title;
        String text;
        Section parent;

        @Override public String toString() {
            String text = "";
            if(this.text!=null && !this.text.isEmpty()) {
                text = this.text.substring(0, Math.min(this.text.length(), 20));
            }
            return "Section[level: "+level+", title: "+title+", text: "+text+"]";
        }
    }

    /*
     * Define a method that takes a Wikipedia text as a parameter and prints the sections, their titles and their texts
     */
    public static List<Section> extractSections(String wikiText) {
        List<Section> sections = new ArrayList<>();
        // Define a regular expression pattern that matches the section headers
        // The pattern consists of one or more equal signs, followed by any text, followed by the same number of equal signs
        // The pattern also captures the text between the equal signs as a group
        Pattern pattern = Pattern.compile("(=+)(.+?)\\1");

        // Create a Matcher object that matches the pattern against the wikiText
        Matcher matcher = pattern.matcher(wikiText);

        // Initialize a variable to store the previous match end index
        int prevEnd = 0;
        
        Section previous = null;

        // Loop through the matches and print the section level, title and text
        while (matcher.find()) {
            Section current = new Section();
            // Get the number of equal signs in the match
            current.level = matcher.group(1).length();

            // Get the text between the equal signs
            current.title = matcher.group(2).trim();

            // Get the start and end indices of the match in the wikiText
            int start = matcher.start();
            int end = matcher.end();

            if(previous != null) {
                // Get the text between the previous match end and the current match start
                previous.text = wikiText.substring(prevEnd, start).trim();
                // Print the section level, title and text
                sections.add(previous);
            }
            
            // Update the previous match end index
            prevEnd = end;
            previous = current;
        }

        // Get and print the text after the last match
        if(previous != null) {
            previous.text = wikiText.substring(prevEnd).trim();
            sections.add(previous);
        }
        return sections;
    }    
}
