package is.hi.hbv503.FitnessTracker.FitnessTracker.Controllers;

import is.hi.hbv503.FitnessTracker.FitnessTracker.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercise")
public class UserController {

    private UserService userService;
    
    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") long id){
        // TODO
        
        return ResponseEntity.noContent().build();
    }
    
}