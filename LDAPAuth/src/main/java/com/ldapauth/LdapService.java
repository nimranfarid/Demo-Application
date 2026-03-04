package com.ldapauth;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class LdapService {

	 private final LdapTemplate ldapTemplate;
	 private static UserRepository userRepository;
	  
	    @Autowired
	    public LdapService(LdapTemplate ldapTemplate, UserRepository userRepository) {
	        this.ldapTemplate = ldapTemplate;
	        LdapService.userRepository = userRepository;
	    }
	    @Autowired
	    private static Environment env;
    

    public LdapService(UserRepository userRepository) {
        this.ldapTemplate = new LdapTemplate();
		this.userRepository = userRepository;
    }
    
    public List<String> getOUsByCN(String cn) {
        String baseDN = "ou=users,dc=example,dc=com"; // Specify the base DN to search within
        String filter = "(cn=" + cn + ")"; // Specify the filter to match the CN attribute

        return ldapTemplate.search(
                "",
                filter,
                new AttributesMapper<String>() {
                    @Override
                    public String mapFromAttributes(Attributes attrs) throws NamingException {
                        // Extract the child OU's name
                        return attrs.get("ou").get().toString();
                    }
                });
    }
    
    
    public List<String> getChildOUs(String cn)throws Exception {
        String baseDN = "dc=example,dc=com"; // Specify the base DN to search within
        String filter = "(&(objectClass=organizationalUnit)(cn=" + cn + "))"; // Filter to match CN attribute
        List<String> results = ldapTemplate.search(
        		baseDN,
                filter,
                new AttributesMapper<String>() {
                    @Override
                    public String mapFromAttributes(Attributes attributes) throws NamingException {
                        // Extract the OU attribute
                        return (String) attributes.get("ou").get();
                    }
                });

        if (!results.isEmpty()) {
            // Return the first OU found (assuming unique CNs)
            return results;
        } else {
            // OU not found
            return null;
        }
    }
    
    
   
    
    public String fetchDNByUsername(String username) {
        String baseDN = "dc=example,dc=com"; // Specify the base DN to search within
        String filter = "(cn=" + username + ")"; // Specify the filter to match the username attribute

        List<String> results = ldapTemplate.search(
                "",
                filter,
                (AttributesMapper<String>) attributes -> {
                    try {
                        // Retrieve and return the DN from the search results
                    	System.out.println(attributes.get("DN").get()+"=====attr=");
                        return (String) attributes.get("dn").get();
                    } catch (NamingException e) {
                        // Handle NamingException if attribute is not found
                        return null;
                    }
                });

        if (!results.isEmpty()) {
            // Return the first DN found (assuming unique usernames)
            return results.get(0);
        } else {
            // User not found
            return null;
        }
    }
    
    

    public List<String> search(final String username) throws Exception{
        // Define the hardcoded base DNs
        String[] baseDNs = {"ou=admin,dc=example,dc=com", "cn=admin,dc=example1,dc=com"};

        // Iterate over each base DN
        for (String baseDN : baseDNs) {
            // Search for the user entry within the current base DN
            ldapTemplate.search(
                baseDN,
                "(cn=" + username + ")",
                (AttributesMapper<Void>) attributes -> {
                    // If user found, print the base DN
                    System.out.println("User '" + username + "' found in DN: " + baseDN);
                    return null; // Return value not used
                });
        }
		return null;
    }

    
    public void create(final String username, final String password,String ou) {
        Name dn = LdapNameBuilder
          .newInstance()
          //.add("ou", "system")
          .add("ou", ou)
          .add("cn", username)
          .build();
        DirContextAdapter context = new DirContextAdapter(dn);

        context.setAttributeValues("objectclass", new String[] { "top", "person", "organizationalPerson", "inetOrgPerson" });
        context.setAttributeValue("cn", username);
        context.setAttributeValue("sn", username);
        context.setAttributeValue("userPassword", digestSHA(password));

        ldapTemplate.bind(context);
    }
    
    
    
   private static String digestSHA(final String password) {
        String base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(password.getBytes());
            base64 = Base64
              .getEncoder()
              .encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return "{SHA}" + base64;
    }
    
    public List<String> searchOUFromUser()throws Exception{
    	List<String> result = new ArrayList<>();

         return result; // Return null if OU not found
    }
    
   
    
    
    public static List<String> getOUFromCommonName(String cn) throws LDAPException {
    	  String ldapHost = env.getRequiredProperty("ldap.domain");
          int ldapPort = Integer.parseInt(env.getRequiredProperty("ldap.port"));
          String ldapBindDN = env.getRequiredProperty("ldap.principal");
          String ldapBindPassword = env.getRequiredProperty("ldap.password");
        LDAPConnection ldapConnection = LDAPConnectionFactory.getInstance(ldapHost, ldapPort, ldapBindDN, ldapBindPassword);
        System.out.println(ldapConnection+"ldapConnection=");
        List<String> result = new ArrayList<>();

        // Establish LDAP connection

        // Perform search operation to locate entry based on CN
        SearchRequest searchRequest = new SearchRequest("dc=example,dc=com",SearchScope.SUB, "(cn=" + cn + ")");
        searchRequest.setSizeLimit(22);
        SearchResultEntry searchResult = ldapConnection.searchForEntry(searchRequest);


        // Extract OU from DN of the located entry
        if (searchResult != null) {
            String dn = searchResult.getDN();
            String[] dnComponents = dn.split(",");
            for (String component : dnComponents) {
            	if (component.toLowerCase().startsWith("ou=")) {
                    result.add(component.trim().substring(3)); // Remove "ou=" prefix
                }
            }
        }

        // Close LDAP connection
        ldapConnection.close();

        return result; // Return null if OU not found
    }
    

    
    public List<String> searchOU(final String cn) {
        return ldapTemplate.search(
          "ou=",
          "cn=" + cn,
          (AttributesMapper<String>) attrs -> (String) attrs
          .get("ou")
          .get());
    }
  
  
    public static Boolean authenticate(String u ,String s) {	
    	System.out.println(digestSHA(s)+"==========");
    	//return ldapTemplate.authenticate("",u, s);
        return userRepository.findByUsernameAndPassword(u,digestSHA(s)) != null;
    }
    
    public void modify(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setPassword(password);
            user.setUsername(username);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User with username " + username + " not found.");
        }
    }
    
    public Boolean authenticateByOu(String username ,String password, String ou) {	
    	System.out.println(digestSHA(password)+"==========");
    	System.out.println(ou+"");
    	User user = userRepository.findByUsernameAndPasswordAndOu(username,digestSHA(password),ou);
    	System.out.println("use====r "+user);
        return userRepository.findByUsernameAndPasswordAndOu(username,digestSHA(password),ou) != null;
    }
    


    public List<String> searchBYOU(String username) throws LDAPException {
        // LDAP connection parameters
        String ldapHost = env.getRequiredProperty("ldap.domain");
        int ldapPort = 10389;
        String ldapBindDN = env.getRequiredProperty("ldap.principal");
        String ldapBindPassword = env.getRequiredProperty("ldap.password");
        LDAPConnection ldapConnection = LDAPConnectionFactory.getInstance(ldapHost, ldapPort, ldapBindDN, ldapBindPassword);
        List<String> result = new ArrayList<>();
        
      
            SearchRequest searchRequest = new SearchRequest("dc=example,dc=com", SearchScope.SUB, "(cn=" + username + ")");
//            searchRequest.setSizeLimit(1000); // Limit the number of entries returned to 1000 (adjust as needed)
            com.unboundid.ldap.sdk.SearchResult searchResult = ldapConnection.search(searchRequest);
            
            // Extract OU from DN of the located entries
            for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                String dn = entry.getDN();
                String[] dnComponents = dn.split(",");
                for (String component : dnComponents) {
                    if (component.toLowerCase().startsWith("ou=")) {
                        result.add(component.trim().substring(3)); // Remove "ou=" prefix
                    }
                }
            }
        
        
        return result;
    }


}
