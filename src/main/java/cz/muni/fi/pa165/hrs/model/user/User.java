package cz.muni.fi.pa165.hrs.model.user;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import java.io.Serializable;

/**
 * User entity. Holds all information about user. Serves for editing user details
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public class User implements Serializable {

    /** Unique identifier */
    private String uuid;

    /** Users password hash used for logging in */
    private String password;

    /** Users email address */
    private String email;

    /** Users real name */
    private String name;

    /** Has administrator privileges */
    private boolean privileged;

    /** Users profession */
    private Profession profession;
    

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (privileged != user.privileged) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (profession != null ? !profession.equals(user.profession) : user.profession != null) return false;
        if (uuid != null ? !uuid.equals(user.uuid) : user.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (privileged ? 1 : 0);
        result = 31 * result + (profession != null ? profession.hashCode() : 0);
        return result;
    }
}
