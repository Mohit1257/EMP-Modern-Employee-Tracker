package com.in.emptrack.repository;

import com.in.emptrack.entity.Employee;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);

    Optional<Employee> findByEmailIgnoreCase(String email);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmployeeCodeIgnoreCaseAndIdNot(String employeeCode, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByStatus(EmploymentStatus status);

    long countByDepartmentId(Long departmentId);

    @Query("SELECT COALESCE(SUM(e.salary), 0.0) FROM Employee e WHERE e.status = :status")
    Double sumSalaryByStatus(@Param("status") EmploymentStatus status);

    @Query("SELECT COALESCE(SUM(e.salary), 0.0) FROM Employee e")
    Double sumAllSalaries();

    List<Employee> findTop5ByOrderByCreatedAtDesc();

    List<Employee> findTop5ByOrderByIdDesc();

    @Query("SELECT FUNCTION('YEAR', e.joiningDate) as yr, FUNCTION('MONTH', e.joiningDate) as mo, COUNT(e) " +
           "FROM Employee e GROUP BY FUNCTION('YEAR', e.joiningDate), FUNCTION('MONTH', e.joiningDate) " +
           "ORDER BY yr DESC, mo DESC")
    List<Object[]> countHiresByMonth();

    @Query("SELECT e.status, COUNT(e) FROM Employee e GROUP BY e.status")
    List<Object[]> countByStatusGroup();

    @Query("SELECT e.employmentType, COUNT(e) FROM Employee e GROUP BY e.employmentType")
    List<Object[]> countByEmploymentTypeGroup();

    List<Employee> findAllByOrderByLastNameAsc();
}
