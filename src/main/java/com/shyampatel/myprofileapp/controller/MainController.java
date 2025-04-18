package com.shyampatel.myprofileapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyampatel.myprofileapp.mail.EmailService;
import com.shyampatel.myprofileapp.message.Message;
import com.shyampatel.myprofileapp.message.MessageService;
import com.shyampatel.myprofileapp.model.MessageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@Controller
public class MainController {

    private final MessageService messageService;
    private final EmailService emailService;
    @Value("${application.recaptcha.secret.key}")
    private String secretRecaptchaKey;
    @Value("${application.recaptcha.site.key}")
    private String siteRecaptchaKey;
    private final RestClient restClient;

    @Autowired
    MainController(MessageService messageService, EmailService emailService) {
        this.restClient = RestClient.create("https://www.google.com/recaptcha/api/siteverify");
        this.messageService = messageService;
        this.emailService = emailService;
    }

    // remove leading and trailing whitespace
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model theModel) {

        theModel.addAttribute("theDate", java.time.LocalDateTime.now());

        return "helloworld";
    }

    @GetMapping("/processForm")
    public String processForm(@RequestParam("visitorName") String name) {
        return "helloworld";
    }

    @RequestMapping(path = "/requestMapping")
    public String processForm2() {
        return "helloworld";
    }


    @GetMapping("/showMessageForm")
    public String showForm(Model theModel) {

        // create a student object
        MessageRequest theMessageRequest = new MessageRequest();

        // add student object to the model
        theModel.addAttribute("message", theMessageRequest);
        theModel.addAttribute("siteRecaptchaKey", siteRecaptchaKey);

        return "message-form";
    }

    @PostMapping("/processMessageForm")
    public String processForm(HttpServletRequest request,
                              Model theModel,
                              @Valid @ModelAttribute("message") MessageRequest messageRequest,
                              BindingResult bindingResult) throws JsonProcessingException {

        theModel.addAttribute("siteRecaptchaKey", siteRecaptchaKey);

        var result = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("response", request.getParameter("g-recaptcha-response"))
                        .queryParam("secret", secretRecaptchaKey)
                        .build())
                .retrieve()
                .body(String.class);
//        System.out.println("token"+ request.getParameter("g-recaptcha-response"));
//        System.out.println(result);
        JsonNode recaptchaResult = (new ObjectMapper()).readTree(result);
        var isRecaptchaVerified = recaptchaResult.get("success").asBoolean();
        var score = recaptchaResult.get("score").asDouble( Double.MIN_VALUE);
//        System.out.println(" verified " + isRecaptchaVerified + " score: " + score);
        if (!isRecaptchaVerified || score < 0.5) {
            theModel.addAttribute("recaptchaFail", Boolean.TRUE);
            return "message-form";
        }
        if (bindingResult.hasErrors()) {
            return "message-form";
        } else {
//            System.out.println("theMessage: " + messageRequest.toString());
            var messageToSave = new Message();
            messageToSave.setMessage(messageRequest.getMessage());
            messageToSave.setSubject(messageRequest.getSubject());
            messageToSave.setVisitorName(messageRequest.getVisitorName());
            messageToSave.setVisitorEmail(messageRequest.getVisitorEmail());
            messageService.save(messageToSave);
            emailService.sendEmail(
                    "shyamkpatel@hotmail.com",
                    "MyProfileApp -> " + messageToSave.getVisitorName() + " -> " + messageToSave.getSubject(),
                    "From:" + messageToSave.getVisitorEmail() + "\n" +
                            messageToSave.getMessage()
            );

            return "message-confirmation";
        }
    }

}
