package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.RestResponse;
import vn.hoidanit.jobhunter.service.CompaniesService;

@RestController
public class CompaniesController {

    private final CompaniesService companiesService;

    public CompaniesController(CompaniesService companiesService) {
        this.companiesService = companiesService;
    }

    @PostMapping("/companies")
    Company createCompany(@RequestBody Company company) {
        return companiesService.createCompanies(company);
    }

    @GetMapping("/companies")
    List<Company> getListCompany(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {

        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "1";
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "1";

        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);

        Pageable pageable = PageRequest.of(current - 1, pageSize);

        return companiesService.getList(pageable);
    }

    @PutMapping("/companies/{id}")
    Company updateCompany(@PathVariable("id") long id, @RequestBody Company company) {
        Optional<Company> item = companiesService.getCompanyById(id);
        if (item.isPresent()) {
            Company result = item.get();
            result.setName(company.getName());
            result.setAddress(company.getAddress());
            result.setCreatedAt(company.getCreatedAt());
            result.setCreatedBy(company.getCreatedBy());
            result.setDescription(company.getDescription());
            result.setLogo(company.getLogo());
            result.setUpdatedAt(company.getUpdatedAt());
            result.setUpdatedBy(company.getUpdatedBy());
            return companiesService.createCompanies(result);
        }
        return null;
    }

    @DeleteMapping("/delete/companies/{id}")
    public void deleteCompanies(@PathVariable("id") long id) {
        companiesService.deleteCompany(id);
    }
}
