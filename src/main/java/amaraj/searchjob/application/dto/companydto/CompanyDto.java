package amaraj.searchjob.application.dto.companydto;

//import amaraj.searchjob.application.entity.Address;
import amaraj.searchjob.application.entity.User;
import amaraj.searchjob.application.entity.enumeration.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
   // @NotNull(message = "{company.validations.id}")
    private Long id;
    @NotNull(message = "{company.validations.name}")
    @Size(min = 2, max = 30)
    private String name;
    @Lob
    @NotNull(message = "{company.validations.description}")
    private String description;
    @NotNull(message = "{company.validations.category}")
    private Category  category;
    @NotNull(message = "{company.validations.nipt}")
    @Column(unique = true)
    private String nipt;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",message = "{company.validations.openAt}")
    private LocalDate openAt;
  //  @NotNull(message = "{company.validations.website}")
    @NotNull
    private String website;
    @NotNull(message = "{company.validations.contactInformation}")
    private String contactInformation;
    @NotNull
    @Min(0)
    private Integer nrOfEmployees;
    @NotNull(message = "{company.validations.email}")
    @Email
    private String email;
    private User user;

   // private Company company;


    @Override
    public String toString() {
        return "CompanyDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", nipt='" + nipt + '\'' +
                ", openAt=" + openAt +
                ", website='" + website + '\'' +
                ", contactInformation='" + contactInformation + '\'' +
                ", nrOfEmployees=" + nrOfEmployees +
                ", email='" + email + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyDto that)) return false;
        return name.equals(that.name) && nipt.equals(that.nipt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nipt);
    }
}


