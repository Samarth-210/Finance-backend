package org.example.financeapp.Controllers;

import org.example.financeapp.Services.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gemini")
public class AIController {
    @Autowired
    private AIService aiService;

    @GetMapping("/get")
    public String quote() {

        return aiService.getDailyQuote();
    }
}

