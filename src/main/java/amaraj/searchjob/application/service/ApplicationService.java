package amaraj.searchjob.application.service;

import amaraj.searchjob.application.dto.ApplicationDTO;
import amaraj.searchjob.application.dto.PageDTO;
import amaraj.searchjob.application.dto.employeDTO.EmployeeDTO;
import amaraj.searchjob.application.dto.jobdto.JobDTO;
import amaraj.searchjob.application.entity.Application;
import amaraj.searchjob.application.entity.Job;
import amaraj.searchjob.application.exception.DuplicateApplicationException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {

    //kjo eshte si te thuash add application
    void applyForJob(Long jobId, Long employeeId) throws DuplicateApplicationException;

    void deleteApplication(Long applId);
    PageDTO<ApplicationDTO> findAll(Pageable pageable);

    List<Application> findByJobId(Long jobId);

    List<Application> findApplicationsByEmployee_JobSeekerId(Long employeeId);
    Long countByJobId(Long jobId);


    //  boolean hasApplied(Long employeeId, Long jobId);



}
