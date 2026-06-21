package org.example.financeapp.Services;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.financeapp.Entity.Categories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AIService {
  private final Client client;
  private String dailyQuote="Wealth grows quietly through discipline, not loudly through luck. ";
  public AIService(@Value("${GEMINI_API_KEY}") String apiKey) {

    this.client = Client.builder()
            .apiKey(apiKey)
            .build();
  }
 @Scheduled(cron="0 0 1 * * *")
  public String getQuotes() {
    log.info("Getting Quotes");
    String prompt = "Generate a quote based on financial and budgeting activities by some eminent personalities in the field of economics,entrepreneurship and finance." +
            "Give a small 1 sentence quote and ensure that the topic of the quote keeps changing so that there is variety" +
            "Give only 1 quote along with the name of the person who quoted it";
    try {
      log.info("Quote obtained");
      GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);

      dailyQuote= response.text();
    } catch (Exception e) {
      System.err.println("Gemini API Call Failed! Reason: " + e.getMessage());
      log.error("Gemini API Call Failed! Reason: " + e.getMessage());

    }

   return dailyQuote;
  }
  public String getDailyQuote(){
    return this.dailyQuote;
  }

  public String classifyCategory(String description){
      String valid= Arrays.stream(Categories.values())
              .map(Enum::name)
              .collect(Collectors.joining(","));
      String category="";
      String prompt=String.format(
              "You are a strict financial transaction classifier.Your task is to classify the transaction description provided into EXACTLY ONE of the following categories:[%s].\n"+
                      "Rules:\n" +
                      "- Respond ONLY with the raw category name string from the allowed list.\n" +
                      "- Do NOT include punctuation, quotes, markdown formatting, or explanations.\n" +
                      "- If unsure or if it fits nothing else, default to 'MISCELLANEOUS'.\n\n" +
                      "Examples:\n" +
                      "- 'Restaurant' -> FOOD\n" +
                      "- 'Movie' -> ENTERTAINMENT\n" +
                      "- 'Netflix' -> ENTERTAINMENT\n" +
                      "- 'Uber' -> TRAVEL\n" +
                      "- 'Rent' -> UTILITIES\n\n" +
                      "Description: \"%s\"\n" +
                      "Category:",
              valid, description
      );
      try{
          log.info("Classifying Category");
          GenerateContentResponse response=client.models.generateContent("gemini-2.5-flash",prompt,null);
           category=response.text().trim().toUpperCase().replaceAll("[`\\s]","");
          return Categories.valueOf(category).name();
      } catch(IllegalArgumentException e){
          log.warn("Gemini returned invalid category");
          return Categories.MISCELLANEOUS.name();
      } catch(Exception e){
          log.error("Gemini API Call Failed! Reason: " + e.getMessage());
      }
      return category;
  }


}
