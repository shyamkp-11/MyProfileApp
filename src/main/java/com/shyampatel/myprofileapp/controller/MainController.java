package com.shyampatel.myprofileapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyampatel.myprofileapp.mail.EmailService;
import com.shyampatel.myprofileapp.message.Message;
import com.shyampatel.myprofileapp.message.MessageService;
import com.shyampatel.myprofileapp.model.MessageRequest;
import com.shyampatel.myprofileapp.visitor.Visit;
import com.shyampatel.myprofileapp.visitor.VisitService;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

@Controller
public class MainController {

    private final MessageService messageService;
    private final VisitService visitService;
    private final EmailService emailService;
    @Value("${application.recaptcha.secret.key}")
    private String secretRecaptchaKey;
    @Value("${application.recaptcha.site.key}")
    private String siteRecaptchaKey;
    private final RestClient restClient;

    @Autowired
    MainController(MessageService messageService, VisitService visitService, EmailService emailService) {
        this.visitService = visitService;
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

    @GetMapping("/githubplayroomapp")
    public String redirectToGithubPlayroomAndroidRepo() {
        return "redirect:https://github.com/shyamkp-11/AndroidPlayroom/tree/main/githubplayroom";
    }

    @GetMapping("/thebackendapp")
    public String redirectToGithubPlayroomBackendApp() {
        return "redirect:https://github.com/shyamkp-11/The-Backend-Server-Github-Webhooks";
    }

    @GetMapping("/geofenceapp")
    public String redirectToGeofenceApp() {
        return "redirect:https://github.com/shyamkp-11/AndroidPlayroom/tree/main/geofenceplayroom";
    }

    @GetMapping("withbluetoothapp")
    public String redirectToWithBluetoothApp() {
        return "redirect:https://github.com/shyamkp-11/AndroidPlayroom/tree/main/withbluetooth";
    }

    @GetMapping("/myprofileapp")
    public String redirectToMyProfileApp() {
        return "redirect:https://github.com/shyamkp-11/MyProfileApp";
    }

    @GetMapping("/")
    public String showForm(Model theModel, HttpServletRequest request) {

        // create a student object
        MessageRequest theMessageRequest = new MessageRequest();

        // add student object to the model
        theModel.addAttribute("message", theMessageRequest);
        theModel.addAttribute("siteRecaptchaKey", siteRecaptchaKey);

        String visitorAddress;
        if (request.getHeader("X-FORWARDED-FOR") == null) visitorAddress = request.getRemoteAddr();
        else visitorAddress = request.getHeader("X-FORWARDED-FOR");
        String userAgent = request.getHeader("User-Agent"); // Get User-Agent header

        var visited = visitService.getVisitWithTimeGreaterThan(visitorAddress, Instant.now().minus(5, ChronoUnit.MINUTES));
        if (visited.isEmpty()) {
            visitService.save(new Visit.Builder().setVisitTime(Instant.now()).setVisitorAddress(visitorAddress).setUserAgent(userAgent).build());
        }
        return "index";
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
//        JsonNode scoreValue = recaptchaResult.get("score");
//        double score = Double.MIN_VALUE;
//        if(scoreValue != null)
//        {
//            score = scoreValue.asDouble(Double.MIN_VALUE);
//        }
//        System.out.println(" verified " + isRecaptchaVerified + " score: " + score);
        if (!isRecaptchaVerified) {
            theModel.addAttribute("recaptchaFail", Boolean.TRUE);
            return "index";
        }
        if (bindingResult.hasErrors()) {
            return "index";
        } else {
//            System.out.println("theMessage: " + messageRequest.toString());
            var messageToSave = Message.builder()
                    .setMessage(messageRequest.getMessage())
                    .setSubject(messageRequest.getSubject())
                    .setVisitorName(messageRequest.getVisitorName())
                    .setVisitorEmail(messageRequest.getVisitorEmail())
                    .build();
            messageService.save(messageToSave);
            emailService.sendEmail(
                    "shyamkpatel@hotmail.com",
                    "MyProfileApp -> " + messageToSave.getVisitorName() + " -> " + messageToSave.getSubject(),
                    "From:" + messageToSave.getVisitorEmail() + "\n" +
                            messageToSave.getMessage()
            );
            theModel.addAttribute("messageSent", Boolean.TRUE);
            return "index";
        }
    }

}
