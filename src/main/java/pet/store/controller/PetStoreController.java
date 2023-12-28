package pet.store.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.service.PetStoreService;

@RestController
@RequestMapping("/pet_store")
@Slf4j
public class PetStoreController {
	
	@Autowired
	private PetStoreService  petStoreService;
	
	@PostMapping("/pet_store")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreData insertPetStore(
			@RequestBody PetStoreData petStoreData) {
		log.info("Creating pet store {}", petStoreData);
		return petStoreService.savePetStoreData(petStoreData);
	}
	
	@PutMapping("/pet_store/{petStoreId}")
	public PetStoreData updatePetstore(@PathVariable Long petStoreId,
			@RequestBody PetStoreData petStoreData) {
		petStoreData.setPetStoreId(petStoreId);
		log.info("Updating a pet store {}", petStoreData);
		return petStoreService.savePetStoreData(petStoreData);		
	}
	
	@GetMapping("/pet_store")
	public List<PetStoreData> retrieveAllContributors(){
		log.info("Retrive all contributors called.");
		return petStoreService.retriveAllPetStores();
	}
	
	@GetMapping("/pet_store/{petStoreId}")
	public PetStoreData retrievePetStoreById(@PathVariable Long petStoreId) {
		log.info("Retrieving pet store with ID={}", petStoreId);
		return petStoreService.retrievePetStoreById(petStoreId);
	}

	@DeleteMapping("/pet_store")
	public void deleteAllPetStores() {
		log.info("Attempting to delete all");
		throw new UnsupportedOperationException("Deleting all is not allowed");
	}
	
	@DeleteMapping("/pet_store/{petStoreId}")
	public Map<String, String> deletePetStoreById(
			@PathVariable Long petStoreId){
		log.info("Deleteing pet store with ID={}", petStoreId);
		
		petStoreService.deletePetStoreById(petStoreId);
		
		return Map.of("message", "Deleting of Pet Store with ID=" + petStoreId 
				+ " was successful");
	}
	
	@PostMapping("/{petStoreId}/employee")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreEmployee insertEmployee(@PathVariable Long petStoreId,
			@RequestBody PetStoreEmployee employeeData) {
		log.info("Creating pet store employee {}", employeeData);
		return petStoreService.saveEmployee(petStoreId, employeeData);
	}
	
	@PostMapping("/{petStoreId}/customer")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreCustomer insertCustomer(@PathVariable Long petStoreId, 
			@RequestBody PetStoreCustomer customerData) {
		log.info("Creating pet store customer {}", customerData);
		return petStoreService.saveCustomer(petStoreId, customerData);
	}
}
