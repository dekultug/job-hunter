package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    List<Company> getListCompany() {
        return companiesService.getList();
    }

    @PutMapping("/companies/${id}")
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
        } else {
            return companiesService.createCompanies(company);
        }
    }

}
