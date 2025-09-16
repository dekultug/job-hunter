package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
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

    public List<Company> getList(Pageable pageable) {
        return companiesRepo.findAll(pageable).getContent();
    }

    public Optional<Company> getCompanyById(long id) {
        return companiesRepo.findById(id);
    }

    public void deleteCompany(long id) {
        companiesRepo.deleteById(id);
    }

}