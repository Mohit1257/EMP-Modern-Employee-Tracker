package com.in.emptrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.in.emptrack.controller.api.EmployeeApiController;
import com.in.emptrack.dto.request.EmployeeFormRequest;
import com.in.emptrack.dto.response.EmployeeResponse;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import com.in.emptrack.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("POST /api/employees should return 201 Created on valid payload")
    void testCreateEmployeeEndpoint() throws Exception {
        EmployeeFormRequest req = EmployeeFormRequest.builder()
                .employeeCode("EMP-2001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .departmentId(1L)
                .designation("Senior Analyst")
                .employmentType(EmploymentType.FULL_TIME)
                .salary(75000.0)
                .joiningDate(LocalDate.of(2023, 1, 10))
                .status(EmploymentStatus.ACTIVE)
                .build();

        EmployeeResponse resp = EmployeeResponse.builder()
                .id(1L)
                .employeeCode("EMP-2001")
                .fullName("John Doe")
                .email("john.doe@example.com")
                .build();

        when(employeeService.createEmployee(any(EmployeeFormRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.employeeCode").value("EMP-2001"));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} should return 204 No Content")
    void testDeleteEmployeeEndpoint() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }
}
