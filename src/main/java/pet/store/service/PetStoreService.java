package pet.store.service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.dao.PetStoreDao;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;
	
	@Transactional(readOnly = false)
	public PetStoreData savePetStoreData(PetStoreData petStoreData) {
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreId, 
				petStoreData.getPetStoreAddress());
		
		setFieldsInPetStore(petStore, petStoreData);
		return new PetStoreData(petStoreDao.save(petStore));
	}

	private void setFieldsInPetStore(PetStore petStore, 
			PetStoreData petStoreData) {
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
	}
	
	private PetStore findOrCreatePetStore(Long petStoreId, String petStoreAddress) {
		PetStore petStore;
		
		if(Objects.isNull(petStoreId)) {
			Optional<PetStore> opContrib = 
					petStoreDao.findByPetStoreAddress(petStoreAddress);
			
			if(opContrib.isPresent()) {
				throw new DuplicateKeyException("Pet Store at this address " + petStoreAddress + 
						" already exist");
			}
			
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}
		return petStore;
	}

	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException(
						"Pet Srtore with ID=" + petStoreId + "was not found"));
	}
}
