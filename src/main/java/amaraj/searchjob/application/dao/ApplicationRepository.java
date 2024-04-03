package amaraj.searchjob.application.dao;

import amaraj.searchjob.application.entity.Application;
import amaraj.searchjob.application.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
//    @Query("SELECT new amaraj.searchjob.application.dto.employeeDTO.EmployeeDTO(a.employee.jobSeekerId, a.employee.name, a.employee.surname, a.employee.email, a.employee.yearsOfExp) " +
//            "FROM Application a " +
//            "WHERE a.job.id = :jobId")
//    List<ApplicationDTO> findEmployeesByJobId(@Param("jobId")Long jobId);
//   // List<Long>findEmployeeIdsByJobId(Long jobId);
  //  List<Application> findEmployeesByCompanyIdAndJobId(Long companyId, Long jobId);

    List<Application> findByJobId(Long jobId);
    Long countByJobId(Long jobId);

    List<Application> findApplicationsByEmployee_JobSeekerId(Long jobSeekerId);
    //  boolean existsByEmployeeAndJob(Long employee, Long job);
}
