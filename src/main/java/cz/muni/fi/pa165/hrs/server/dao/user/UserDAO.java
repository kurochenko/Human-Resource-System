package cz.muni.fi.pa165.hrs.server.dao.user;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.jcr.AllowedFileType;

import javax.jcr.Session;
import java.io.InputStream;
import java.util.List;

/**
 * DAO interface for {@code User}, {@code UserDTO} entities
 * which manages CRUD operations on it
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public interface UserDAO {

    /**
     * JCR session setter
     * @param session
     * @throws IllegalAccessException when session is {@code null}
     */
    public void setSession(Session session);

    /**
     * Creates new user account. Does not check if there exists same account
     * @param user new user to create
     * @throws IllegalArgumentException when user is {@code null} or users id is not {@code null}
     */
    public void create(User user);
    
    /**
     * Updates existing users details
     * @param user user to update
     * @throws IllegalArgumentException when user is {@code null} or users id is {@code null}
     */
    public void edit(User user);
    
    /**
     * Removes user specified by ID.
     * @param uuid users unique identifier
     * @throws IllegalArgumentException when uuid is {@code null}
     */
    public void remove(String uuid);
    
    /**
     * Searches for user with given ID.
     * @param uuid users unique identifier
     * @return user or {@code null} when no user was found for given ID
     * @throws IllegalArgumentException when userId is {@code null}
     */
    public User findByUuid(String uuid);

    /**
     * Searches for user with given email address.
     * @param email users unique email address
     * @return user or {@code null} when no user was found for given email address
     * @throws IllegalArgumentException when email is {@code null}
     */
    public User findByEmail(String email);
    
    /**
     * Searches for all users and returns them in List
     * @return list of users
     */
    public List<UserDTO> findAll();
    
    /**
     * Searches for all users which belongs to certain profession
     * @param profession  profession
     * @return list of users which belong to specified profession
     * @throws IllegalArgumentException when profession {@code null}
     */
    public List<UserDTO> findByProfession(Profession profession);
    
    /**
     * Searches for users by their CV content using fulltext search
     * @param query query to search in users CV content
     * @return list of users 
     * @throws IllegalArgumentException when query is {@code null}
     */
    public List<UserDTO> findFullText(String query);

    /**
     * Adds users CV of type {@code fileType} to repository
     * @param userId UUID of user to which CV will be added
     * @param cvStream input stream of CV file
     * @param fileType type of file which contains CV
     * @throws IllegalArgumentException when any of parameter is {@code null}
     */
    public void addCv(String userId, InputStream cvStream, AllowedFileType fileType);

    /**
     * Gets users CV of type {@code fileType}
     * @param userId UUID of user whose CV will be retrieved
     * @param fileType type of file which contains CV
     * @return stream of CV file
     * @throws IllegalArgumentException when any of parameter is {@code null}
     */
    public InputStream getCv(String userId, AllowedFileType fileType);
}
