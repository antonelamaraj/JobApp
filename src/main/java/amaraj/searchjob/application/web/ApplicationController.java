package amaraj.searchjob.application.web;

import amaraj.searchjob.application.dto.ApplicationDTO;
import amaraj.searchjob.application.dto.PageDTO;
import amaraj.searchjob.application.dto.companydto.CompanyDto;
import amaraj.searchjob.application.dto.employeDTO.EmployeeDTO;
import amaraj.searchjob.application.entity.Application;
import amaraj.searchjob.application.entity.Employee;
import amaraj.searchjob.application.entity.Job;
import amaraj.searchjob.application.service.ApplicationService;
import amaraj.searchjob.application.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static amaraj.searchjob.application.mapper.EmployeeMapper.EMPLOYEE_MAPPER;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobService jobService;


    /**
     * ESHTE OK
     *
     * PER CDO JOB, KUSH EMPL KA APLIKUAR
     **/
    @GetMapping
    public ResponseEntity<PageDTO<ApplicationDTO>> getCompanies(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(applicationService.findAll(pageable));
    }

    //
//    @GetMapping("comp/{companyId}/jobs/{jobId}/applicants")
//    public ResponseEntity<List<EmployeeDTO>> employeesApplied(@PathVariable Long jobId) {
//       List<EmployeeDTO>  applicants = applicationService.findByJobId(jobId);
//        if (applicants.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(applicants);
//
//    }


//    //kthen te gjithe employee qe kane aplikuar ne nje pune te caktuar sipas id se job
//    @GetMapping("/job/{jobId}")
//    public ResponseEntity<List<Employee>> getApplicationsByJobId(@PathVariable Long jobId) {
//        List<Application> applications = applicationService.findByJobId(jobId);
//        List<Employee> employeesOfThisJob = new ArrayList<>();
//        for (Application application:applications){
//            employeesOfThisJob.add(application.getEmployee());
//        }
//        if (applications.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.ok(employeesOfThisJob);
//        }
//    }

}
