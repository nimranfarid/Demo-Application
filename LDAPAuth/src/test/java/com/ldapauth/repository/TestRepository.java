package com.ldapauth.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ldapauth.UserRepository;

//@SpringBootTest
//public class TestRepository {
//
// 
////	@Test
////	public void testFetchUserNamePassword()throws Exception{
////		UserRepository userRepositoryMock = mock(UserRepository.class);
//// 
////		
////		UserDomain userDomain =  new UserDomain();
////		userDomain.setUserId(1l);
////		Optional<UserDomain> userData = Optional.of(userDomain);
////		when(userRepositoryMock.fetchByUserNameAndPassword("admin", "pwd")).thenReturn(userData);
////        Optional<UserDomain> result = userRepositoryMock.fetchByUserNameAndPassword("admin", "pwd");
////        assertEquals(userDomain, result.orElse(null));
////	}
