package com.ldapauth;

import java.util.List;


import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends LdapRepository<User> {
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
    User findByUsernameAndPasswordAndOu(String username, String password,String ou);
    User findByPassword(String password);
    List<User> findByUsernameLikeIgnoreCase(String username);
}