package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.CompaniesRepo;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompaniesService {

    private final CompaniesRepo companiesRepo;
    private final UserRepository userRepository;

    public CompaniesService(CompaniesRepo companiesRepo, UserRepository userRepository) {
        this.companiesRepo = companiesRepo;
        this.userRepository = userRepository;
    }

    public Company createCompanies(Company company) {
        return companiesRepo.save(company);
    }

    public List<Company> getList(Specification<Company> spec, Pageable pageable) {
        return companiesRepo.findAll(spec, pageable).getContent();
    }

    public Optional<Company> getCompanyById(long id) {
        return companiesRepo.findById(id);
    }

    public void deleteCompany(long id) {
        Optional<Company> comOptional = getCompanyById(id);
        if (comOptional.isPresent()) {
            List<User> listUserInCompany = comOptional.get().getUsers();
            for (User user : listUserInCompany) {
                handleDeleteCompanyUser(user);
            }
        }
        companiesRepo.deleteById(id);
    }

    public User handleDeleteCompanyUser(User rUser) {
        User currentUser = userRepository.findById(rUser.getId()).isPresent()
                ? userRepository.findById(rUser.getId()).get()
                : null;
        if (currentUser != null) {
            currentUser.setAddress(rUser.getAddress());
            currentUser.setGender(rUser.getGender());
            currentUser.setAge(rUser.getAge());
            currentUser.setName(rUser.getName());

            if (rUser.getCompany() != null) {
                currentUser.setCompany(null);
            }
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

}