package is.hi.hbv503.FitnessTracker.FitnessTracker;

import is.hi.hbv503.FitnessTracker.FitnessTracker.Entities.Exercise;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Entities.ExerciseLog;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Entities.Stats;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Entities.User;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Services.ExerciseLogService;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Services.ExerciseService;
import is.hi.hbv503.FitnessTracker.FitnessTracker.Services.UserService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ListIterator;


@Controller
public class HomeController {

    private ExerciseService exerciseService;
    private ExerciseLogService exerciseLogService;
    private UserService userService;


    @Autowired
    public HomeController(ExerciseService exerciseService, ExerciseLogService exerciseLogService, UserService userService){
        this.exerciseService = exerciseService;
        this.userService = userService;
        this.exerciseLogService = exerciseLogService;
    }

    @RequestMapping("/")
    public String Home(Model model){
        model.addAttribute("exercices", exerciseService.findAll());
        return "Velkominn";
    }


        @RequestMapping(value="/addexercise", method = RequestMethod.POST)
        public String addExercise(@Valid Exercise exercise, @Valid User user, BindingResult result, Model model, HttpSession session){
            model.addAttribute("exercices",exerciseService.findAll());
            User sessionUser2 = (User) session.getAttribute("LoggedInUser");

            if(sessionUser2  != null){
                model.addAttribute("loggedinuser", sessionUser2);

                if(result.hasErrors()){
                    return "add-exercise";
                }
                exerciseService.save(exercise);
                User sessionUserCurrent = userService.findByUsername(sessionUser2.toString());

                //model.addAttribute("exercices", exerciseService.findAll());
                System.out.println(exerciseService.findAll().get(0).getDescription());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String tempname = sessionUser2.getuName();
                //System.out.println(userService.findByUsername(tempname));
                //System.out.println(userService.findByUsername(tempname).getExercices());
                try{
                    //exerciseService.save(exercise);
                    exerciseLogService.save(new ExerciseLog(exercise,sessionUserCurrent,sdf.parse("21/12/2012"),sdf.parse("31/12/2013")));
                    //sessionUserCurrent.setExercices(new ExerciseLog(exercise,sessionUserCurrent,sdf.parse("21/12/2012"),sdf.parse("31/12/2013")));
                    System.out.println(userService.findByUsername(tempname));
                    //userService.findByUsername(tempname)(exerciseLogService.save(exercise,sessionUser2,sdf.parse("21/12/2012"),sdf.parse("31/12/2013")));
                    //userService.findByUsername(tempname).setExercices(new ExerciseLog(exerciseLogService.save(exercise,sessionUser2,sdf.parse("21/12/2012"),sdf.parse("31/12/2013"))));
                   // exerciseLogService.save(new ExerciseLog(exercise, sessionUser2, sdf.parse("21/12/2012"),sdf.parse("21/12/2012")));
                }catch (ParseException e) {
                    e.printStackTrace();
                }



                //rentalLogService.save(new RentalLog(tempMovie.get(0),tempUser,sdf.parse("21/12/2012"),sdf.parse("31/12/2013") ));
                //model.addAttribute("exercises", exerciseLogService.findAll());
                //System.out.println(sessionUser2.getExercices());
                //System.out.println();
                return "Velkominn";
            }

            return "Velkominn";
        }

        @RequestMapping(value="/addexercise", method = RequestMethod.GET)
        public String addExerciseForm(Exercise exercise){
            return "add-exercise";
        }

        @RequestMapping(value="/delete/{id}", method = RequestMethod.GET)
        public String deleteExercise(@PathVariable("id") long id, Model model){
            Exercise exercise = exerciseService.findById(id).orElseThrow(()-> new IllegalArgumentException("Invalid exercise ID"));
            exerciseService.delete(exercise);
            model.addAttribute("exercises", exerciseService.findAll());
            return "Velkominn";
        }

        //To be continued...
        @RequestMapping(value = "/stats", method = RequestMethod.GET)
        public String statsPage(Model model){

            //needs to be made in a seperate class...
            System.out.println("sdasd" + exerciseService.findAll().size());
            int size = exerciseService.findAll().size();
            System.out.println("size = " + size);
            Stats stats = new Stats();
            double time = stats.getTotalTime(size);
            System.out.println(time);
            model.addAttribute("time", time);
            /*
            int i = 0;
            int totalTime = 0;
            while(i < exerciseService.findAll().size()){
                totalTime += exerciseService.findAll().get(i).getDuration();
                i++;
            }
            System.out.println("Total time spent: " +  totalTime);
            model.addAttribute("time", totalTime);
            */
            return "Stats";
        }


    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUpGET(User user){
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUpPOST(@Valid User user, BindingResult result, Model model){
        if(result.hasErrors()){
            return "signup";
        }
        User exists = userService.findByUsername(user.username);
        if(exists == null){
            userService.save(user);
        }
        model.addAttribute("exercices", exerciseService.findAll());
        return "Velkominn";
    }


    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String usersGET(Model model){
        model.addAttribute("users", userService.findAll());
        return "users";
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGET(User user){
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPOST(@Valid User user, BindingResult result, Model model, HttpSession session){
        if(result.hasErrors()){
            return "login";
        }
        model.addAttribute("exercices",exerciseService.findAll());
        User exists = userService.login(user);
        if(exists != null){
            session.setAttribute("LoggedInUser", user);
            return "redirect:/";
        }
        return "redirect:/";
    }


    @RequestMapping(value = "/loggedin", method = RequestMethod.GET)
    public String loggedinGET(HttpSession session, Model model){
        model.addAttribute("exercices",exerciseService.findAll());
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        System.out.println(sessionUser);
        if(sessionUser  != null){
            model.addAttribute("loggedinuser", sessionUser);
            return "loggedInUser";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/userprofile", method = RequestMethod.GET)
    public String userprofile(HttpSession session, Model model){

        //model.addAttribute("exercices",exerciseService.findAll());
       // User sessionUser = (User) session.getAttribute("LoggedInUser");
        /*
        if(sessionUser  != null){
            model.addAttribute("loggedinuser", sessionUser);
        }

         */

        model.addAttribute("exercices",exerciseService.findAll());
        User sessionUser1 = (User) session.getAttribute("LoggedInUser");
        //System.out.println(sessionUser1);
        String tempname = sessionUser1.getuName();
        //System.out.println("LoggedInUser: " + sessionUser1 + " " +  tempname);
        if(sessionUser1  != null){
            //model.addAttribute("userprofile", sessionUser);
            model.addAttribute("exerciceLog", userService.findByUsername(tempname).getExercices());
            return "userprofile";
        }

        return "Velkominn";
    }


    @RequestMapping("/rentals")
    public String allRentals(Model model){
        model.addAttribute("exerciceLog", exerciseLogService.findAll());
        return "rentals";
    }
}
