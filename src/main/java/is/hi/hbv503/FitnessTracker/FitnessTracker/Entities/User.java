package is.hi.hbv503.FitnessTracker.FitnessTracker.Entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "USER")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    private List<Exercise> userExercices = new ArrayList<>();

    public List<Exercise> getUserExercice() {
        return userExercices;
    }

    public void setUserExercices(List<Exercise> exercices) {
        this.userExercices = exercices;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return username;
    }

    public String getuName() {
        return username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        this.userExercices = new ArrayList<Exercise>();
    }

    public void addExercice(Exercise exercise) {
        userExercices.add(exercise);
    }
}