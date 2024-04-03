package amaraj.searchjob.application.dao;

import amaraj.searchjob.application.dto.employeDTO.EmployeeDTO;
import amaraj.searchjob.application.entity.Application;
import amaraj.searchjob.application.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

 //   List<Application> findByJobSeekerId(Long jobSeekerId);
 Optional<Employee> findByNameAndEmail(String name, String email);

}
