package amaraj.searchjob.application.web;

import amaraj.searchjob.application.dto.employeDTO.CreateUpdateEmployeeDTO;
import amaraj.searchjob.application.dto.employeDTO.EmployeeDTO;
import amaraj.searchjob.application.dto.PageDTO;
import amaraj.searchjob.application.dto.jobdto.JobDTO;
import amaraj.searchjob.application.entity.Application;
import amaraj.searchjob.application.entity.Employee;
import amaraj.searchjob.application.entity.Job;
import amaraj.searchjob.application.entity.exporter.EmployeePdfExporter;
import amaraj.searchjob.application.entity.exporter.EmployeePdfExporterAsUser;
import amaraj.searchjob.application.service.ApplicationService;
import amaraj.searchjob.application.service.EmployeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static amaraj.searchjob.application.mapper.EmployeeMapper.EMPLOYEE_MAPPER;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    EmployeService employeService;

    @Autowired
    ApplicationService applicationService;

    @GetMapping("/{empId}")
    public ResponseEntity<EmployeeDTO> findById(@PathVariable Long empId){
        var emp = employeService.findById(empId).map(EMPLOYEE_MAPPER::toDTO).orElse(null);
        return emp!=null?ResponseEntity.ok(emp):ResponseEntity.notFound().build();
    }


    @GetMapping
    public ResponseEntity<PageDTO<EmployeeDTO>> findAll(@RequestParam(required = false,defaultValue = "0")Integer page,
                                                        @RequestParam(required = false,defaultValue = "10")Integer size){
        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(employeService.findAll(pageable));
    }

    @PostMapping   //OK
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody CreateUpdateEmployeeDTO req){
        var empl = employeService.findByNameAndEmail(req.getName(), req.getEmail());
        if (empl.isPresent()) {
            throw new RuntimeException("This Employee already exists. Please check the data you have put?");
        }
        return ResponseEntity.ok(employeService.addEmployee(req));
    }

    //update nje empl
    @PutMapping("/{emplID}")   //OK
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long emplID, @RequestBody CreateUpdateEmployeeDTO req){
        return ResponseEntity.ok(employeService.updateEmployee(emplID, req));
    }


    @DeleteMapping("/{employeedId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long employeedId){
        try {
            employeService.deleteEmployee(employeedId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //SHIKO TE GJITHA APLIKIMET E BERA NGA AI VETE
    //http://localhost:8080/api/employees/1/jobsApplied
    @GetMapping("{employeeId}/jobsApplied")
    public ResponseEntity<List<Job>> allJobsApplied(@PathVariable Long employeeId){
        List<Application> applications = applicationService.findApplicationsByEmployee_JobSeekerId(employeeId);
        List<Job> allJobs = new ArrayList<>();
        for (Application application:applications){
            allJobs.add(application.getJob());
        }
        if (applications.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(allJobs);
        }

    }

    //upload a CV

    @PostMapping("/uploadCV")
    public String handleFileUpload(@RequestParam("file")MultipartFile file){
        if (file.isEmpty()){
            return "Please select a file to upload";
        }
        try {
            String fileName = file.getOriginalFilename();
            byte[] fileContent = file.getBytes();
            return "File uploaded successfully: " + fileName;
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }

    //per ADMIN-in
    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        PageDTO<EmployeeDTO> employeeList = employeService.findAll(PageRequest.of(0, 10));
        EmployeePdfExporterAsUser exporter = new EmployeePdfExporterAsUser();
        exporter.export( employeeList, response);
    }





}
