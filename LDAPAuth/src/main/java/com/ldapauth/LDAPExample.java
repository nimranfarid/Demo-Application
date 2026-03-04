package com.ldapauth;

import java.util.Hashtable;

import javax.naming.*;
import javax.naming.directory.*;

public class LDAPExample {
    public static void main(String[] args) {
        // LDAP connection parameters
        String ldapURL = "ldap://localhost:10389"; // Change to your LDAP server URL
        String ldapUser = "uid=admin,ou=system"; // Change to your LDAP admin username
        String ldapPassword = "secret"; // Change to your LDAP admin password
        String baseDN = "dc=example,dc=com"; // Change to your base DN
        
        // User search parameters
        String searchOU = "ou=Agora"; // Change to the OU you want to search within
        String searchFilter = "(objectClass=top)"; // Change to your search filter
        
        // Set up LDAP context
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUser);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        
        try {
            // Create LDAP context
            DirContext ctx = new InitialDirContext(env);
            
            // Set search controls
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            // Perform LDAP search
            NamingEnumeration<SearchResult> results = ctx.search(baseDN, searchFilter, searchControls);
            
            // Iterate over search results
            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                Attribute ouAttr = attrs.get("ou");
                
                // Check if OU matches search OU
                if (ouAttr != null && ouAttr.contains(searchOU)) {
                    // Print user information
                    Attribute cnAttr = attrs.get("cn");
                    String username = cnAttr != null ? (String) cnAttr.get() : "Unknown";
                    System.out.println("Found user in OU " + searchOU + ": " + username);
                }
            }
            
            // Close LDAP context
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
