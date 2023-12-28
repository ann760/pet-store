package pet.store.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;
	
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
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
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

	@Transactional(readOnly = true)
	public List<PetStoreData> retriveAllPetStores() {
		// @formatter:off
		return petStoreDao.findAll()
			.stream()
			.map(cont -> new PetStoreData(cont))
			.toList();
		// @formatter:on
	}

	@Transactional(readOnly = true)
	public PetStoreData retrievePetStoreById(Long petStoreId) {
			PetStore petStore = findPetStoreById(petStoreId);
				return new PetStoreData(petStore);
	}

	@Transactional(readOnly = false)
	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);
	}

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);
		
		setFieldsInEmployee(employee, petStoreEmployee);
		
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		
		return new PetStoreEmployee(employeeDao.save(employee));
	}
	
	private void setFieldsInEmployee(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
	}
	
	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		Employee employee;
		
		if(Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(employeeId);
		}
		return employee;
	}
			
	private Employee findEmployeeById(Long employeeId) {
		return employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException(
						"Employee with ID=" + employeeId + "does not match"));
	}
	
	
	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long customerId = petStoreCustomer.getCustomerId();
		Customer customer = findOrCreateCustomer(petStoreId, customerId);
		
		setFieldsInCustomer(customer, petStoreCustomer);
		
		customer.getPetStores().add(petStore);
		petStore.getCustomers().add(customer);
		
		return new PetStoreCustomer(customerDao.save(customer));
	}
	
	private void setFieldsInCustomer(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
	}
	
	private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
		Customer customer;
			if(Objects.isNull(customerId)) {
				customer = new Customer();
			}else{
				customer = findCustomerById(petStoreId, customerId);
			}
		return customer;
	}

    private Customer findCustomerById(Long petStoreId, Long customerId) {
        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer with Id=" + customerId + " was not found."));
				
	boolean found = false;
	
	for (PetStore petStore : customer.getPetStores()) {
		if (petStore.getPetStoreId() == petStoreId) {
			found = true;
			break;
		}else {
			if(!found) {
				throw new IllegalArgumentException("Customer with ID:" + customerId + " is not a member with the pet store ID= " + petStoreId);
			}
		}
	}
	return customer;	
	}	
}
