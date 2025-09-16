package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.repository.CompaniesRepo;

@Service
public class CompaniesService {

    private final CompaniesRepo companiesRepo;

    public CompaniesService(CompaniesRepo companiesRepo) {
        this.companiesRepo = companiesRepo;
    }

    public Company createCompanies(Company company) {
        return companiesRepo.save(company);
    }

    public List<Company> getList(){
        return companiesRepo.findAll();
    }

    public Optional<Company> getCompanyById(long id){
        return companiesRepo.findById(id);
    }

}