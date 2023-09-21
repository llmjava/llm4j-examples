package llmjava.palm;


import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;

import lombok.ToString;

@ToString
public class Question {
    String question;
    String A;
    String B;
    String C;
    String answer;


    public static Question parseAsObject(Gson gson, Type objectType, String jsonString) {
        Question question = null;
        try {
            question = gson.fromJson(jsonString, objectType);
        } catch(Exception e) {
            System.out.println("Failed to parse PaLM response:\n" + jsonString);
            e.printStackTrace();
        }
        return question;
    }


    public static Question parseAsList(Gson gson, Type listType, String jsonString) {
        Question question = null;
        try {
            List<Question> questions = gson.fromJson(jsonString, listType);
            question = questions.get(0);
            System.out.println(question);
        } catch(Exception e) {
            System.out.println("Failed to parse PaLM response:\n" + jsonString);
            e.printStackTrace();
        }
        return question;
    }

    public static Question parse(Gson gson, Type objectType, Type listType, String jsonString) {
        if(jsonString == null) return null;
        jsonString = jsonString.trim();
        Question question = null;
        if(jsonString.startsWith("[")) {
            question = parseAsList(gson, listType, jsonString);
        } else {
            question = parseAsObject(gson, objectType, jsonString);
        }
        System.out.println(jsonString + " => " + question);
        return question;
    }

    public static String toCSV(List<Question> questions) {
        String header = "question,A,B,C,answer";
        StringBuffer builder = new StringBuffer(header);
        for(Question qa: questions) {
            builder.append("\n")
            .append(qa.question).append(",")
            .append(qa.A).append(",")
            .append(qa.B).append(",")
            .append(qa.C).append(",")
            .append(qa.answer);
        }
        return builder.toString();
    }
}