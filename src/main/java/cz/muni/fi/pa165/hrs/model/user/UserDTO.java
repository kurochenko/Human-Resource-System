package cz.muni.fi.pa165.hrs.model.user;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import java.io.Serializable;

/**
 * User DTO entity. Holds necessary user information. Used for listing collection of users etc.
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public class UserDTO implements Serializable {

     /** Unique identifier */
    private String uuid;

    /** Users real name */
    private String name;

    /** Users profession */
    private Profession profession;

    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserDTO other = (UserDTO) obj;
        if ((this.uuid == null) ? (other.uuid != null) : !this.uuid.equals(other.uuid)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.profession != other.profession && (this.profession == null || !this.profession.equals(other.profession))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 43 * hash + (this.profession != null ? this.profession.hashCode() : 0);
        return hash;
    }
}
