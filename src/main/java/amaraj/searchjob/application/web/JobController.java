package amaraj.searchjob.application.web;

import amaraj.searchjob.application.dto.companydto.CompanyDto;
import amaraj.searchjob.application.dto.jobdto.JobDTO;
import amaraj.searchjob.application.dto.PageDTO;
import amaraj.searchjob.application.dto.jobdto.CreateUpdateJobDTO;
import amaraj.searchjob.application.entity.Company;
import amaraj.searchjob.application.entity.Job;
import amaraj.searchjob.application.entity.enumeration.ExperienceLevel;
import amaraj.searchjob.application.entity.enumeration.JobType;
import amaraj.searchjob.application.entity.enumeration.Location;
import amaraj.searchjob.application.entity.exporter.JobPdfExporter;
import amaraj.searchjob.application.service.CompanyService;
import amaraj.searchjob.application.service.JobService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static amaraj.searchjob.application.mapper.JobMapper.JOB_MAPPER;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService service;
    private final CompanyService companyService;

    @GetMapping("/{jobID}")
    public ResponseEntity<JobDTO> findById(@PathVariable Long jobID){
        var emp = service.findById(jobID).map(JOB_MAPPER::toDTO).orElse(null);
        return emp!=null?ResponseEntity.ok(emp):ResponseEntity.notFound().build();
    }

    //http://localhost:8080/api/jobs/job/title?title=Java
    @GetMapping("/job/title")
    public List<JobDTO> findJobsByTitle(@RequestParam String title){
        return service.findJobsByTitle(title);
    }

    //http://localhost:8080/api/jobs/job/title/location?title=JAVA&location=ONSITE
    @GetMapping("/job/title/location")
    public List<JobDTO> findJobsByTitleAndLocation(@RequestParam String title, @RequestParam Location location){
        return service.findByTitleContainingIgnoreCaseAndLocation(title, location);
    }

    //http://localhost:8080/api/jobs/job/title/experienceLevel?title=JAVA&experienceLevel=NO_EXPERIENCE_REQUIRED
    @GetMapping("/job/title/experienceLevel")
    public List<JobDTO> findJobsByTitleAndLocation(@RequestParam String title, @RequestParam ExperienceLevel experienceLevel){
        return service.findByTitleContainingIgnoreCaseAndExperienceLevel(title, experienceLevel);
    }

    @GetMapping("/job/title/jobType")
    public List<JobDTO> findJobsByTitleAndJobType(@RequestParam String title, @RequestParam JobType jobType){
        return service.findByTitleContainingIgnoreCaseAndJobType(title, jobType);
    }

    //http://localhost:8080/api/jobs/job/title/jobType/location/experience?title=Spring&jobType=FULL_TIME&location=ONSITE&experience=SENIOR_LEVEL
    @GetMapping("/job/title/jobType/location/experience")
    public List<JobDTO> findByTitleContainingIgnoreCaseAndJobTypeAndLocationAndExperienceLevel(@RequestParam String title, @RequestParam JobType jobType, @RequestParam Location location, @RequestParam ExperienceLevel experience){
        return service.findByTitleContainingIgnoreCaseAndJobTypeAndLocationAndExperienceLevel(title, jobType, location, experience);
    }
    @GetMapping
    public ResponseEntity<PageDTO<JobDTO>> findAll(@RequestParam(required = false,defaultValue = "0")Integer page,
                                                        @RequestParam(required = false,defaultValue = "10")Integer size){
        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findAll(pageable));
    }

    //FUNKSIONON
    @PostMapping("/createJob/{companyId}")
    public ResponseEntity<JobDTO> createJob(@PathVariable Long companyId, @RequestBody JobDTO job){
        var company = companyService.findCompanyByCompanyId(companyId).get();
        job.setCompany(company);
        return ResponseEntity.ok(service.addJob(job));
    }

    @PutMapping("/{jobID}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long jobID, @RequestBody JobDTO req){
        return ResponseEntity.ok(service.updateJob(jobID, req));
    }

    @DeleteMapping("/{jobId}")    //NUK FSHIHET
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId){
        try {
            service.deleteJob(jobId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        PageDTO<JobDTO> jobList = service.findAll(PageRequest.of(0, 10));
        JobPdfExporter exporter = new JobPdfExporter();
        exporter.export(jobList, response);

    }

    //Te gjitha jobs te postuara pas dates se dhene
    //http://localhost:8080/api/jobs/after-date/date?date=2024-03-15

    //DUEHT TE DAL DHE KOMPANIA QE E KA POSTUAR
    @GetMapping("/after-date/date")
    public ResponseEntity<List<Job>> getJobsAfterDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
            List<Job> jobs = service.getJobsPostedFromDate(date);
            return ResponseEntity.ok(jobs);
    }


    //http://localhost:8080/api/jobs/title/date?title=java&date=2024-03-08
    @GetMapping("/title/date")
    public ResponseEntity<List<Job>> getJobsByTitleAndAfterDate(@RequestParam String title, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        List<Job> jobs = service.getJobsPostedFromDate(title, date);
        return ResponseEntity.ok(jobs);
    }

}
