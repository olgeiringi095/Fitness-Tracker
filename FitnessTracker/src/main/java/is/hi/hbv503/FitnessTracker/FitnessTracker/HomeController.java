package is.hi.hbv503.FitnessTracker.FitnessTracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.JSONPObject;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Entities.*;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Services.ExerciseLogService;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Services.ExerciseService;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Services.UserService;
import org.json.JSONArray;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.time.*;
import java.util.Date;



@Controller
public class HomeController {

    private ExerciseService exerciseService;
    private ExerciseLogService exerciseLogService;
    private UserService userService;


    @Autowired
    public HomeController(ExerciseService exerciseService, ExerciseLogService exerciseLogService, UserService userService) {
        this.exerciseService = exerciseService;
        this.userService = userService;
        this.exerciseLogService = exerciseLogService;
    }

    @RequestMapping("/")
    public String Home(Model model) {
        model.addAttribute("exercices", exerciseService.findAll());
        return "Velkominn";
    }

    @RequestMapping(value = "/addexercise", method = RequestMethod.POST)
    public String addExercise(@Valid Exercise exercise, @Valid User user, BindingResult result, Model model, HttpSession session) throws Exception {
        Date cDate = new Date();
        model.addAttribute("exercices", exerciseService.findAll());
        User sessionUser2 = (User) session.getAttribute("LoggedInUser");
        String tempUser = sessionUser2.toString();
        if (sessionUser2 != null) {
            model.addAttribute("loggedinuser", sessionUser2);

            if (result.hasErrors()) {
                return "add-exercise";
            }
            model.addAttribute("exercices", exerciseService.findAll());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String tempname = sessionUser2.getuName();
            exerciseService.save(exercise);
            User sessionUserCurrent = userService.findByUsername(sessionUser2.toString());
            try {
                exerciseLogService.save(new ExerciseLog(exercise, sessionUserCurrent, cDate, cDate));
                System.out.println("Add exercice");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(3);

            return "userprofile";
        }
        return "userprofile";
    }

    @RequestMapping(value = "/addexercise", method = RequestMethod.GET)
    public String addExerciseForm(Exercise exercise) {
        return "add-exercise";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteExercise(@PathVariable("id") long id, Model model) {
        Exercise exercise = exerciseService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid exercise ID"));
        exerciseService.delete(exercise);
        model.addAttribute("exercises", exerciseService.findAll());
        return "Velkominn";
    }

    @RequestMapping(value = "stats", method = RequestMethod.GET)
    public String statsPage(HttpSession session, Model model) throws IOException {
        User sessionUser1 = (User) session.getAttribute("LoggedInUser");
        String tempname = sessionUser1.toString();
        List<ExerciseLog> l = userService.findByUsername(tempname).getExercices();
        ArrayList<Tmp> tmpArr = new ArrayList<Tmp>();

        Tmp t = new Tmp();
        for (int i = 0; i < l.size(); i++) {
            t.setDate(l.get(i).getFromdate());
            t.setDuration(l.get(i).getExercise().getDuration());
            t.setTitle(l.get(i).getExercise().getTitle());
            Tmp cp = new Tmp(t);
            tmpArr.add(cp);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        objectMapper.setDateFormat(df);
        String arrayToJson = objectMapper.writeValueAsString(tmpArr);
        System.out.println(arrayToJson);
        model.addAttribute("arrayToJson", arrayToJson);

        return "Stats";
    }

    //@GetMapping("/signup")
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUpGET(User user) {
        return "signup.html";
    }

    //@GetMapping("/signup")
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUpPOST(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup.html";
        }
        User exists = userService.findByUsername(user.getuName());
        if (exists == null) {
            userService.save(user);
        }
        model.addAttribute("exercices", exerciseService.findAll());
        return "Velkominn";
    }


    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String usersGET(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGET(User user) {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPOST(@Valid User user, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }
        model.addAttribute("exercices", exerciseService.findAll());
        User exists = userService.login(user);
        if (exists != null) {
            session.setAttribute("LoggedInUser", user);
            return "userprofile";
        }
        return "redirect:/";
    }


    @RequestMapping(value = "/loggedin", method = RequestMethod.GET)
    public String loggedinGET(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("loggedinuser", sessionUser);
            return "loggedInUser";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/userprofile", method = RequestMethod.GET)
    public String userprofile(HttpSession session, Model model) {

        User sessionUser1 = (User) session.getAttribute("LoggedInUser");

        if (sessionUser1 != null) {
            String tempname = sessionUser1.getuName();
            if (userService.findByUsername(tempname).getExercices().size() > 0) {
                model.addAttribute("exerciceLog", userService.findByUsername(tempname).getExercices());
                System.out.println(userService.findByUsername(tempname).getExercices().get(0).getExercise().getDuration());
            }
            return "userprofile";
        }
        return "Velkominn";
    }


    @RequestMapping("/rentals")
    public String allRentals(Model model, HttpSession session) {
        model.addAttribute("exerciceLog", exerciseLogService.findAll());
        User sessionUser1 = (User) session.getAttribute("LoggedInUser");
        String tempname = sessionUser1.toString();
        for (int i = 0; i < userService.findByUsername(tempname).getExercices().size(); i++) {
            System.out.println(i + " Exercise = " + userService.findByUsername(tempname).getExercices().get(i).getExercise().getDuration() + " Minutes");
        }
        return "rentals";
    }


    @RequestMapping("/makedata")
    public String makeData(Model model) {
        User temp = new User("Sindri", "pass");
        userService.save(temp);
        userService.login(temp);
        System.out.println(temp);
        String[] titleTemp = {"Hlaupa", "hjola", "labba"};
        int j = 10;
        ExerciseLog eLog;
        for (int i = 0; i < 10; i++) {
            Exercise e = new Exercise();
            Random r = new Random();
            int n = r.nextInt((50) + 1);
            e.setDuration(n + 0.0);
            e.setId(0);
            n = r.nextInt(3);
            e.setTitle(titleTemp[n]);
            e.setDescription(titleTemp[n]);

            exerciseService.save(e);
            eLog = makeExerciseLog(e, j, temp);
            exerciseLogService.save(eLog);
            j--;
        }
        return "userprofile";
    }

    private ExerciseLog makeExerciseLog(Exercise exercise, int dagar, User user) {
        LocalDate tmpDate = LocalDate.now().minusDays(dagar);
        Date date = Date.from(tmpDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        ExerciseLog tmpLog = new ExerciseLog();
        tmpLog.setExercise(exercise);
        tmpLog.setFromdate(date);
        tmpLog.setToDate(date);
        tmpLog.setUser(user);
        System.out.println(date);
        return tmpLog;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(Model model, HttpSession session) {
        User temp = new User("Sindri", "pass");
        System.out.println("gsgsdf");
        return "test";
    }
}