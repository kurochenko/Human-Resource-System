package cz.muni.fi.pa165.hrs.server.service.profession;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.server.dao.profession.ProfessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of ProfessionService
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
@Service
public class ProfessionServiceImpl implements ProfessionService {

    /** Profession DAO */
    private ProfessionDAO professionDAO;


    @Autowired
    @Override
    public void setProfessionDAO(ProfessionDAO professionDAO) {
        if (professionDAO == null) {
            throw new IllegalArgumentException("Profession DAO is null");
        }
        this.professionDAO = professionDAO;
    }

    @Override
	public void create(Profession profession) {
        if(profession == null) {
            throw new IllegalArgumentException("Profession is null");
        }
        if (profession.getUuid() != null) {
            throw new IllegalArgumentException("Professions UUID is already set");
        }
        if (profession.getName() == null) {
            throw new IllegalArgumentException("Professions name is null");
        }
        if (professionDAO.findByName(profession.getName()) != null) {
            throw new IllegalArgumentException("Profession with name '" + profession.getName() + "' already exists");
        }

        professionDAO.create(profession);
	}

    @Override
	public void edit(Profession profession) {
        if(profession == null) {
            throw new IllegalArgumentException("Profession is null");
        }
        if (profession.getUuid() == null) {
            throw new IllegalArgumentException("Professions UUID is not set");
        }
        if (profession.getName() == null) {
            throw new IllegalArgumentException("Professions name is null");
        }
        if (professionDAO.findByUuid(profession.getUuid()) == null) {
            throw new  IllegalArgumentException("Profession with UUID '" + profession.getUuid() + "' does not exist");
        }

        professionDAO.edit(profession);
	}

    @Override
	public void remove(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Profession UUID is null");
        }
        if (professionDAO.findByUuid(uuid) == null) {
            throw new  IllegalArgumentException("Profession with UUID '" + uuid + "' does not exist");
        }

        professionDAO.remove(uuid);
	}

    @Override
    public Profession findByUuid(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID is null");
        }

        return professionDAO.findByUuid(uuid);
    }

    @Override
	public Profession findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }

        return professionDAO.findByName(name);
	}

    @Override
	public List<Profession> findAll() {
		return professionDAO.findAll();
	}
}