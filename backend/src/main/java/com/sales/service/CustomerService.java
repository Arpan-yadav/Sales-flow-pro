package com.sales.service;

import com.sales.model.Customer;
import com.sales.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAll(String search) {
        if (search != null && !search.isBlank())
            return customerRepository.searchCustomers(search.trim());
        return customerRepository.findAll();
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    public Customer create(Customer customer) {
        if (customer.getEmail() != null && !customer.getEmail().isBlank()
                && customerRepository.existsByEmail(customer.getEmail()))
            throw new RuntimeException("Email already registered: " + customer.getEmail());
        return customerRepository.save(customer);
    }

    public Customer update(Long id, Customer updated) {
        Customer c = getById(id);
        c.setName(updated.getName());
        c.setEmail(updated.getEmail());
        c.setPhone(updated.getPhone());
        c.setAddress(updated.getAddress());
        c.setCity(updated.getCity());
        c.setCountry(updated.getCountry());
        return customerRepository.save(c);
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    public long count() {
        return customerRepository.count();
    }
}
