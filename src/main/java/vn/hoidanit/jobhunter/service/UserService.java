package vn.hoidanit.jobhunter.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO.CompanyUser;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.repository.CompaniesRepo;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompaniesRepo companiesRepo;

    public UserService(UserRepository userRepository, CompaniesRepo companiesRepo) {
        this.userRepository = userRepository;
        this.companiesRepo = companiesRepo;
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> comOptional = companiesRepo.findById(user.getCompany().getId());
            user.setCompany(comOptional.isPresent() ? comOptional.get() : null);
        }
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    public Optional<User> getUser(long userId) {
        return userRepository.findById(userId);
    }

    public List<ResUserDTO> getListUser(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = userRepository.findAll(specification, pageable);
        List<ResUserDTO> list = pageUser.getContent()
                .stream().map(new Function<User, ResUserDTO>() {
                    @Override
                    public ResUserDTO apply(User t) {
                        return convertUserToResUserDTO(t);
                    }
                }).collect(Collectors.toList());
        return list;
    }

    public User getUserByEmail(String email) {
        Optional<User> optional = userRepository.findByEmail(email);
        return optional.get();
    }

    public boolean existsByEmail(String emString) {
        return userRepository.existsByEmail(emString);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            currentUser.setName(reqUser.getName());

            if (reqUser.getCompany() != null) {
                Optional<Company> comOptional = companiesRepo.findById(reqUser.getCompany().getId());
                currentUser.setCompany(comOptional.isPresent() ? comOptional.get() : null);
            }

            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleDeleteCompanyUser(User rUser) {
        User currentUser = this.fetchUserById(rUser.getId());
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

    public ResCreateUserDTO convertUserToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setName(user.getName());
        CompanyUser companyUser = new CompanyUser();
        if (user.getCompany() != null) {
            companyUser.setName(user.getCompany().getName());
            companyUser.setId(user.getCompany().getId());
            res.setCompany(companyUser);
        }
        return res;
    }

    public ResUserDTO convertUserToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setName(user.getName());
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            companyUser.setName(user.getCompany().getName());
            companyUser.setId(user.getCompany().getId());
            res.setCompany(companyUser);
        }
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        ResUpdateUserDTO.CompanyUser companyUser = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            companyUser.setName(user.getCompany().getName());
            companyUser.setId(user.getCompany().getId());
            res.setCompany(companyUser);
        }
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = getUserByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            handleCreateUser(currentUser);
        }
    }

    public User getUserByTokenAndEmail(String token, String email) {
        return userRepository.findUserByRefreshTokenAndEmail(token, email);
    }
}
