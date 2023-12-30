package com.sample.sample.controller;

import com.sample.sample.config.BaseIntegrationTest;
import com.samples.sample.entity.Customer;
import com.samples.sample.repository.CustomerRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CustomerConrollerIT extends BaseIntegrationTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @SneakyThrows
    public void getPaymentLinkSuccess() {

        Customer customer = new Customer();
        customer.setFirstName("getPaymentLinkSuccess.1");
        customer.setLastName(getClass().getSimpleName());
        customer = customerRepository.save(customer);
        RequestEntity<?> getCustomer = RequestEntity.get("/v1/customer/{id}", customer.getId())
                .headers(authHeaders())
                .build();
        ResponseEntity<String> response = testRestTemplate.exchange(getCustomer, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(not(nullValue())));
        assertThat(response.getBody(), containsStringIgnoringCase("Customer"));
    }
}
