package amaraj.searchjob.application.web;

import amaraj.searchjob.application.dto.ApplicationDTO;
import amaraj.searchjob.application.dto.UserDto;
import amaraj.searchjob.application.dto.companydto.CompanyDto;
import amaraj.searchjob.application.dto.companydto.CompanyJobCountDTO;
import amaraj.searchjob.application.dto.companydto.CreateUpdateCompanyDTO;
import amaraj.searchjob.application.dto.PageDTO;
import amaraj.searchjob.application.dto.employeDTO.EmployeeDTO;
import amaraj.searchjob.application.dto.jobdto.JobDTO;
import amaraj.searchjob.application.entity.*;
import amaraj.searchjob.application.entity.exporter.CompanyPdfExporter;
import amaraj.searchjob.application.entity.exporter.EmployeePdfExporter;
import amaraj.searchjob.application.exception.DuplicateApplicationException;
import amaraj.searchjob.application.mail.EmailSenderService;
import amaraj.searchjob.application.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static amaraj.searchjob.application.mapper.CompanyMapper.COMPANY_MAPPER;
import static amaraj.searchjob.application.mapper.EmployeeMapper.EMPLOYEE_MAPPER;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final JobService jobService;
    private final EmployeService employeService;
    private final ApplicationService applicationService;
    private final EmailSenderService emailSenderService;

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    @GetMapping    //http://localhost:8080/api/jobs?page=0&size=30
    public ResponseEntity<PageDTO<CompanyDto>> getCompanies(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(companyService.findAll(pageable));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> findById(@PathVariable Long companyId) {
        var comp = companyService.findCompanyByCompanyId(companyId).map(COMPANY_MAPPER::toDTO).orElse(null);
        return comp != null ? ResponseEntity.ok(comp) : ResponseEntity.notFound().build();
    }

    //http://localhost:8080/api/companies/comp/1/jobs
    //JOBS FOR EACH COMPANY
    @GetMapping("/comp/{companyId}/jobs")
    public List<JobDTO> getCompanyJobs(@PathVariable Long companyId) {
        return jobService.findJobsByCompanyId(companyId);
    }

    //e njejte me ate me siper vecse e nxjerr vetem titujt
    @GetMapping("/{companyId}/jobs/map")
    public ResponseEntity<Map<String, List<String>>> getJobsByCompany(@PathVariable Long companyId) {
        Map<String, List<String>> companyJobsMap = companyService.getJobsByCompany(companyId);
        if (companyJobsMap.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(companyJobsMap);
        }
    }


    //Post a Job   FUNKSIONOOOOOOOOONNNNNNNNN
    @PostMapping("/{companyId}/post-job")
    public ResponseEntity<JobDTO> postJob(@PathVariable Long companyId, @RequestBody JobDTO jobDTO) {
        var company = companyService.findCompanyByCompanyId(companyId).get();
        Optional<JobDTO> existingJob = jobService.findByTitleAndCompany(jobDTO.getTitle(), company);
        if (existingJob.isPresent()) {
            throw new RuntimeException("This job already exists. Please update the job vacancies you have posted?");
        }
        jobDTO.setCompany(company);
        JobDTO savedJob = jobService.addJob(jobDTO);
        String endpointLink = "http://localhost/api/jobs/" + savedJob.getId(); // Replace this with your actual endpoint URL

        if (savedJob.getId() != null) {
            //ne momentin qe postohet nje job, shkon nje notification te te gjithe employee e regjistruar
            sendApplicationConfirmationEmail(companyId, savedJob.getId());
        } else {
            throw new RuntimeException("Failed to save job. ID is null.");
        }
        return ResponseEntity.ok(savedJob);
    }

    private void sendApplicationConfirmationEmail(Long companyId, Long jobId) {
        List<Employee> allEmployees = employeService.listOfEmployees();
        Job job = jobService.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        String companyName = job.getCompany().getName();
        String companyEmail = job.getCompany().getEmail();
        String subject = "Emporify Job Alerts!";
        String endpointLink = "http://localhost:8080/api/jobs/" + job.getId(); // Replace this with your actual endpoint URL

        String message = String.format("Look the new job posted in Emporify: " + job.getTitle() + " from " + companyName + "\nClick the link to see more details of the job -> " + endpointLink);
        for (Employee employee : allEmployees) {
            String applicantEmail = employee.getEmail();
            emailSenderService.sendEmail(companyId, companyEmail, applicantEmail, subject, message);
        }
    }

    /**
     * FUNKSIONON
     */
    @PostMapping
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CreateUpdateCompanyDTO req) throws DuplicateApplicationException {
        var company = companyService.findCompanyByNameAndNipt(req.getName(), req.getNipt());
        if (company.isPresent()) {
            throw new RuntimeException("This Company already exists. Please check the data you have put?");
        }
        return ResponseEntity.ok(companyService.addCompany(req));
    }

    //FUNKSIONON
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long companyId, @RequestBody CreateUpdateCompanyDTO req) {
        return ResponseEntity.ok(companyService.updateCompany(companyId, req));
    }

    //FUNKSIONON  GOOD
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        try {
            companyService.deleteCompany(companyId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*********************Aplikimi per nje job posted ************* FUNKSIONOOOOOOOOON*************/
    @PostMapping("comp/{companyId}/empl/{employeeId}/apply/{jobId}")
    String applyForJob(@PathVariable Long companyId, @PathVariable Long jobId, @PathVariable Long employeeId) throws DuplicateApplicationException {
        Job job = jobService.findById(jobId).get();
        Optional<Employee> employee = employeService.findById(employeeId);

        if (job != null && employee != null && !(job.getApplications().contains(employee))) {
            //Job.applicants++;
            applicationService.applyForJob(jobId, employeeId);
       //     job.setApplicants(Job.countApplicants++);
            sendApplicationConfirmationEmail(companyId, employeeId, jobId);
            return "Succes";
        } else {
            return "Error: Job or employee not found or you have applied at this job";
        }
    }

    private void sendApplicationConfirmationEmail(Long companyId, Long employeeId, Long jobId) {
        Employee employee = employeService.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        String aplicantEmail = employee.getEmail();

        Job job = jobService.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        String companyName = job.getCompany().getName();
        String companyEmail = job.getCompany().getEmail();

        String subject = "Application Confirmation";
        String message = String.format("Thank you for applying for " + job.getTitle() + " position. Your application has been received.\n" + companyName);
        emailSenderService.sendEmail(companyId, companyEmail, aplicantEmail, subject, message);

    }

    /*********************Anullimi per nje job posted ************* FUNKSIONOOOOOOOOON*************/
    @DeleteMapping("/empl/{employeeId}/delete/{applId}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long applId) {
        try {
            applicationService.deleteApplication(applId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        //duhet te kthej dicka-> nje mesazh qe aplikimi u fshi
    }

    /************************APLIKIMET  E BERA PER NJE JOB POSTED*********************************************************/
    //kthen te gjithe employee qe kane aplikuar ne nje pune te caktuar sipas id se job
    //http://localhost:8080/api/companies/job/1
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Employee>> getApplicationsByJobId(@PathVariable Long jobId) {
        List<Application> applications = applicationService.findByJobId(jobId);
        List<Employee> employeesOfThisJob = new ArrayList<>();
        for (Application application : applications) {
            employeesOfThisJob.add(application.getEmployee());
        }
        if (applications.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(employeesOfThisJob);
        }
    }


    //KOMPANIA SHIKON SE KUSH KA APLIKUAR PER NJE POZICION TE POSTUAR PREJ SAJ
    //http://localhost:8080/api/companies/export/employees/pdf/job/1
    @GetMapping("/export/employees/pdf/job/{jobId}")
    public void exportEmployeesToPDF(@PathVariable Long jobId, HttpServletResponse response) throws IOException {
        List<Application> applications = applicationService.findByJobId(jobId);
        List<Employee> employeesOfThisJob = new ArrayList<>();
        EmployeePdfExporter exporter = new EmployeePdfExporter();

        for (Application application : applications) {
            employeesOfThisJob.add(application.getEmployee());
        }
        exporter.export(employeesOfThisJob, response);
    }

    // ADMIN-I
    //sa usera jane regjistruar si kompani
    //http://localhost:8080/api/companies/export/pdf
    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        PageDTO<CompanyDto> compList = companyService.findAll(PageRequest.of(0, 10));
        CompanyPdfExporter exporter = new CompanyPdfExporter();
        exporter.export(compList, response);
    }

    //ADMIN
    //http://localhost:8080/api/companies/mostJobs/jobs
    @GetMapping("/mostJobs/jobs")
    public ResponseEntity<List<CompanyJobCountDTO>> getCompaniesWithMostJobs(@RequestParam(defaultValue = "10") Long topN) {
        List<CompanyJobCountDTO> companiesWithMostJobs = companyService.getCompaniesWithMostJobs(topN);
        return ResponseEntity.ok(companiesWithMostJobs);
    }


    //ADMIN
    //KTHE NJE MAP-> COMPANY: JOBS POSTED
    //http://localhost:8080/api/companies/jobs/allJobs/accordingCompanies
    @GetMapping("/jobs/allJobs/accordingCompanies")
    public ResponseEntity<Map<String, List<String>>> getAllCompaniesWithJobs() {
        Map<String, List<String>> companyJobsMap = companyService.getAllCompaniesWithJobs();
        if (companyJobsMap.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(companyJobsMap);
        }
    }


}
