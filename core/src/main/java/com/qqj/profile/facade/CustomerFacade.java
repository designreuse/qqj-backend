package com.qqj.profile.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.service.AdminUserService;
import com.qqj.profile.controller.RegisterRequest;
import com.qqj.profile.controller.RegisterResponse;
import com.qqj.profile.domain.Customer;
import com.qqj.profile.service.CustomerService;
import com.qqj.profile.wrapper.CustomerWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 6:58 PM
 */
@Service
public class CustomerFacade {

    private static Logger logger = LoggerFactory.getLogger(CustomerFacade.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AdminUserService adminUserService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String RESTAURANT_LIST = "/template/restaurant-list.xls";

    @Transactional
    public Customer findCustomerByUsername(String username) {
        return customerService.findCustomerByUsername(username);
    }

    @Transactional
    public CustomerWrapper findCustomerWrapperByUsername(String username) {
        return new CustomerWrapper(customerService.findCustomerByUsername(username));
    }

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {

        Customer customer = new Customer();
        customer.setUsername(registerRequest.getTelephone());
        customer.setTelephone(registerRequest.getTelephone());
        customer.setPassword(registerRequest.getPassword());
        customer.setEnabled(true);

        if (StringUtils.isNotBlank(registerRequest.getRecommendNumber())) {
            try {
                AdminUser adminUser = adminUserService.getAdminUser(Long.valueOf(registerRequest.getRecommendNumber()));
                // TODO any better solution
                // just visit name to ensure adminUser exists
                adminUser.getUsername();
                customer.setAdminUser(adminUser);
            } catch (Exception e) {
                logger.warn(registerRequest.getRecommendNumber() + " is not a valid recommend number", e);
            }
        }
        customer = customerService.register(customer);


        RegisterResponse response = new RegisterResponse();
        response.setCustomerId(customer.getId());

        return response;
    }

    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        Customer customer = findCustomerByUsername(username);
        if (null != customer) {
            customerService.updateCustomerPassword(customer, newPassword);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updatePassword(Customer customer, String oldPassword, String newPassword) {

        // 兼容旧密码格式
        String compatibleOldPassword = customer.getUsername() + oldPassword + "mirror";
        String compatibleNewPassword = customer.getUsername() + newPassword + "mirror";

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(customer.getUsername(),
                compatibleOldPassword);

        try {
            Authentication auth = authenticationManager.authenticate(token);

            if (auth.isAuthenticated()) {
                customerService.updateCustomerPassword(customer, compatibleNewPassword);

                return true;
            }
        } catch (AuthenticationException e) {
        }
        return false;
    }



    public String generateRestaurantNumberById(Long restaurantId) {
        return String.valueOf(restaurantId);
    }


}