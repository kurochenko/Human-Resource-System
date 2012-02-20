package cz.muni.fi.pa165.hrs.server.service.profession;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.server.dao.profession.ProfessionDAO;

import java.util.List;

/**
 * Profession service for {@code Profession} entity which manages CRUD operations on it
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public interface ProfessionService {

    /**
     * {@code ProfessionDAO} setter
     * @param professionDAO
     * @throws IllegalArgumentException when {@code ProfessionDAO} is {@code null}
     */
    public void setProfessionDAO(ProfessionDAO professionDAO);

	/**
	 * Creates new profession if there is no profession with same name already created
	 * @param profession profession to create
	 * @throws IllegalArgumentException when profession is {@code null} or profession id is not {@code null}
	 */
	public void create(Profession profession);
	
	/**
	 * Updates existing profession. Replaces properties of profession which is identified by id
	 * @param profession profession to update
	 * @throws IllegalArgumentException when profession is {@code null} of profession id is {@code null} 
	 */
	public void edit(Profession profession);
	
	/**
	 * Removes profession identified by ID
	 * @param uuid profession unique identifier
	 * @throws IllegalArgumentException when professionId is {@code null} or there is no profession
	 * with specified ID 
	 */
	public void remove(String uuid);
	
	/**
	 * Gets profession by UUID
	 * @param uuid profession unique uuid
	 * @return {@code Profession} or {@code null} when no profession with given UUID was found
	 * @throws IllegalArgumentException
	 */
	public Profession findByUuid(String uuid);

    /**
     * Gets profession by name
     * @param name profession unique name
     * @return {@code Profession} or {@code null} when no profession with given UUID was found
     * @throws IllegalArgumentException
     */
    public Profession findByName(String name);
	
	/**
	 * Gets all professions and returns them as list of professions
	 * @return list of professions
	 */
	public List<Profession> findAll();
}