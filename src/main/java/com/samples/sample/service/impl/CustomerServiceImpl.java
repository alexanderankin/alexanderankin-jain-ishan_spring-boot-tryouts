package com.samples.sample.service.impl;

import com.samples.sample.entity.Customer;
import com.samples.sample.repository.CustomerRepository;
import com.samples.sample.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    final CustomerRepository customerRepository;

    @Override
    public void createCustomer() {
        // Coming soon!!
    }

    @Override
    public Optional<Customer> findById(final Long id) {
        return customerRepository.findById(id);
    }
}
