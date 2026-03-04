package com.ldapauth;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unboundid.ldap.sdk.LDAPException;

@RestController
public class HomeController {

	@Autowired
	private LdapService ldapService;
	
  public HomeController(LdapService ldapServiceMock) {
		// TODO Auto-generated constructor stub
	}


@GetMapping("/loginvalidate")
  public String index(@RequestParam("username") String username,@RequestParam("password") String password) throws Exception {
	  System.out.println("=============="+username+password);
	  boolean service = ldapService.authenticate(username,password);
	  if(service) {
		    return "Welcome to the home page!";
	  }else {
		    return "Invalid!";

	  }
  }
  
  @GetMapping("/save")
  public String save(@RequestParam("username") String username,@RequestParam("password") String password,@RequestParam("ou") String ou)throws Exception{
	  System.out.println(username+"================ "+password);
	  ldapService.create(username, password,ou);
	  return "success";
  }
  @GetMapping("/modify")
  public String modify(@RequestParam("username") String username,@RequestParam("password") String password)throws Exception{
	  System.out.println(username+"================ "+password);
	  ldapService.modify(username, password);
	  return "success";
  }
  

  @GetMapping("/validatebyou")
  public String index(@RequestParam("username") String username,@RequestParam("password") String password,@RequestParam("ou") String ou) throws Exception {
	  System.out.println("=============="+username+password+ou);
	  boolean service = ldapService.authenticateByOu(username,password,ou);
	  System.out.println(service);
	  if(service) {
		    return "Welcome to the home page!";
	  }else {
		    return "Invalid!";

	  }
  }
//  @GetMapping("/validatebyOu")
//  public String index(@RequestParam("username") String username,
//                      @RequestParam("password") String password,
//                      @RequestParam("ou") String ou) throws Exception {
//      System.out.println("Received: " + username + ", " + password + ", " + ou);
//      boolean authenticated = ldapService.authenticateByOu(username, password, ou);
//      System.out.println("Authenticated: " + authenticated);
//      return authenticated ? "Welcome to the home page!" : "Invalid!";
//  }

  @GetMapping("/getOU")
  public List<String> getOU(@RequestParam String cn) throws LDAPException {
      return ldapService.getOUFromCommonName(cn);
  }
  @GetMapping("/searchOU")
  public List<String> searchOU(@RequestParam String username) throws LDAPException {
      return ldapService.searchBYOU(username);
  }

}
