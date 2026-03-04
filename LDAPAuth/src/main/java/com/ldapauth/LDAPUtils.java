package com.ldapauth;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class LDAPUtils {

    public static String getOUFromCN(String cn) {
        try {
            // Parse the DN
            LdapName ldapName = new LdapName(cn);

            // Get the RDNs (Relative Distinguished Names)
            for (Rdn rdn : ldapName.getRdns()) {
                // Check if the RDN is an OU
                if (rdn.getType().equalsIgnoreCase("ou")) {
                    // Return the value of the OU
                    return rdn.getValue().toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null; // Return null if no OU found
    }

    public static void main(String[] args) {
        String cn = "cn=muthu1,dc=example,dc=com";
        String ou = getOUFromCN(cn);
        if (ou != null) {
            System.out.println("OU: " + ou);
        } else {
            System.out.println("OU not found.");
        }
    }
}
