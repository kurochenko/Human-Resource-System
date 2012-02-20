package cz.muni.fi.pa165.hrs.server.service.user;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.dao.profession.ProfessionDAO;
import cz.muni.fi.pa165.hrs.server.dao.user.UserDAO;
import cz.muni.fi.pa165.hrs.server.jcr.AllowedFileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * Implementation of UserService using JackRabbit repository
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
@Service
public class UserServiceImpl implements UserService {

    /** User DAO */
    private UserDAO userDAO;

    /** Profession DAO */
    private ProfessionDAO professionDAO;


    @Autowired
    @Override
    public void setUserDAO(UserDAO userDAO) {
        if (userDAO == null) {
            throw new IllegalArgumentException("User DAO is null");
        }
        this.userDAO = userDAO;
    }

    @Autowired
    @Override
    public void setProfessionDAO(ProfessionDAO professionDAO) {
        if (professionDAO == null) {
            throw new IllegalArgumentException("Profession DAO is null");
        }
        this.professionDAO = professionDAO;
    }

    @Override
    public void create(User user) {
		if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (user.getUuid() != null) {
            throw new IllegalArgumentException("Users UUID is already set");
        }
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Users email is null");
        }
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Users password is null");
        }
        if (user.getProfession() == null) {
            throw new IllegalArgumentException("Users profession is null");
        }
        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("User with email '" + user.getEmail() + "' already exists");
        }

        userDAO.create(user);
	}

    @Override
	public void edit(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (user.getUuid() == null) {
            throw new IllegalArgumentException("Users UUID is null");
        }
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Users email is null");
        }
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Users password is null");
        }
        if (user.getProfession() == null) {
            throw new IllegalArgumentException("Users profession is null");
        }
        if (userDAO.findByUuid(user.getUuid()) == null) {
            throw new IllegalArgumentException("User with UUID '" + user.getUuid() + "' does not exist");
        }

        userDAO.edit(user);
	}

    @Override
    public void remove(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Users UUID is null");
        }
        if (userDAO.findByUuid(uuid) == null) {
            throw new IllegalArgumentException("User with UUID '" + uuid + "' does not exist");
        }

        userDAO.remove(uuid);
    }

    @Override
    public User findByUuid(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Users UUID is null");
        }

        return userDAO.findByUuid(uuid);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Users email is null");
        }

        return userDAO.findByEmail(email);
    }

    @Override
	public List<UserDTO> findAll() {
		return userDAO.findAll();
	}

    @Override
    public List<UserDTO> findByProfession(Profession profession) {
        if (profession == null) {
            throw new IllegalArgumentException("Profession is null");
        }
        if (profession.getUuid() == null) {
            throw new IllegalArgumentException("Profession UUID is null");
        }
        if (professionDAO.findByUuid(profession.getUuid()) == null) {
            throw new IllegalArgumentException("No such profession with UUID '" + profession.getUuid() + "'");
        }

        return userDAO.findByProfession(profession);
    }

    @Override
	public List<UserDTO> findFulltext(String query) {
        if (query == null) {
            throw new IllegalArgumentException("Query is null");
        }

        return userDAO.findFullText(query);
	}

    @Override
    public void addCv(String userId, InputStream cvStream, AllowedFileType fileType) {
        if (userId == null) {
            throw new IllegalArgumentException("User UUID is null");
        }
        if (cvStream == null) {
            throw new IllegalArgumentException("Input stream of CV file is null");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("CV file type is null");
        }
        if (userDAO.findByUuid(userId) == null) {
            throw new IllegalArgumentException("No user with UUID '" + userId + "' exists");
        }

        userDAO.addCv(userId, cvStream, fileType);
    }

    @Override
    public InputStream getCv(String userId, AllowedFileType fileType) {
        if (userId == null) {
            throw new IllegalArgumentException("User UUID is null");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("CV file type is null");
        }
        if (userDAO.findByUuid(userId) == null) {
            throw new IllegalArgumentException("No user with UUID '" + userId + "' exists");
        }

        return userDAO.getCv(userId, fileType);
    }
}