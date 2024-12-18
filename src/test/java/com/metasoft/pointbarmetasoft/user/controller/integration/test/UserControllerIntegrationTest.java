package com.metasoft.pointbarmetasoft.user.controller.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.AdminSignUpRequestDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.EmployeeSignUpRequestDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.UserSignInRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateEmployee_thenStatus201() throws Exception {
        // Crear usuario administrador
        AdminSignUpRequestDto adminRequest = new AdminSignUpRequestDto();
        adminRequest.setFirstname("Pedro");
        adminRequest.setLastname("Garza");
        adminRequest.setEmail("pedro@gmail.com");
        adminRequest.setPassword("pedro123456789");
        adminRequest.setPhone("923456789");
        adminRequest.setBusinessName("TestBusiness");

        mockMvc.perform(post("/api/v1/auth/admin/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isCreated());

        // Iniciar sesión con el administrador creado
        UserSignInRequestDto signInRequest = new UserSignInRequestDto();
        signInRequest.setEmail("pedro@gmail.com");
        signInRequest.setPassword("pedro123456789");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.parse(responseBody).read("$.token");

        EmployeeSignUpRequestDto employeeRequest = new EmployeeSignUpRequestDto();
        employeeRequest.setFirstname("Luisina");
        employeeRequest.setLastname("Rosales");
        employeeRequest.setEmail("luis@gmail.com");
        employeeRequest.setPassword("31W'IT6U.Jyf");
        employeeRequest.setPhone("923450789");

        mockMvc.perform(post("/api/v1/auth/employee/sign-up")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee registered successfully"));
    }
}