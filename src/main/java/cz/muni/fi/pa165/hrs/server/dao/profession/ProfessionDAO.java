package cz.muni.fi.pa165.hrs.server.dao.profession;

import cz.muni.fi.pa165.hrs.model.profession.Profession;

import javax.jcr.Session;
import java.util.List;

/**
 * Profession DAO for {@code Profession} entity which manages CRUD operations on it
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public interface ProfessionDAO {

    /**
     * JCR session setter
     * @param session JCR session
     */
    public void setSession(Session session);

	/**
	 * Creates new profession, does not check if there is profession with same name
	 * @param profession profession to create
	 * @throws IllegalArgumentException when profession is {@code null} or profession id is not {@code null}
	 */
	public void create(Profession profession);
	
	/**
	 * Updates existing profession. Replaces properties of profession which is identified by UUID
	 * @param profession profession to update
	 * @throws IllegalArgumentException when profession is {@code null} of profession id is {@code null}
	 */
	public void edit(Profession profession);
	
	/**
	 * Removes profession identified by UUID, does not check if there is not any profession with given UUID
	 * @param uuid profession unique identifier
	 * @throws IllegalArgumentException when professionId is {@code null}
	 */
	public void remove(String uuid);
	
	/**
	 * Gets profession by UUID
	 * @param uuid profession unique identifier
	 * @return profession or {@code null} when no profession with given uuid exists
	 * @throws IllegalArgumentException when name is {@code null}
	 */
	public Profession findByUuid(String uuid);

    /**
     * Gets profession by name
     * @param name profession unique name
     * @return profession or {@code null} when no profession with given name exists
     * @throws IllegalArgumentException when name is {@code null}
     */
    public Profession findByName(String name);
	
	/**
	 * Gets all professions and returns them as list of professions
	 * @return list of professions
	 */
	public List<Profession> findAll();
}