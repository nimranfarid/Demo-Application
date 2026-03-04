package com.ldapauth;
import java.util.ArrayList;
import java.util.List;

import com.unboundid.ldap.sdk.*;

public class OUExtractor {

    public static List<String> getOUFromCN(String cn) throws LDAPException {
        // LDAP connection parameters
        String ldapHost = "localhost";
        int ldapPort = 10389;
        String ldapBindDN = "uid=admin,ou=system";
        String ldapBindPassword = "secret";
        List<String> result = new ArrayList<>();

        // Establish LDAP connection
        LDAPConnection ldapConnection = new LDAPConnection(ldapHost, ldapPort, ldapBindDN, ldapBindPassword);

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
                	result.add(component.trim());
                }
            }
        }

        // Close LDAP connection
        ldapConnection.close();

        return result; // Return null if OU not found
    }

    public static void main(String[] args) {
        try {
            String cn = "muthu";
            List<String> ou = getOUFromCN(cn);
            for(String s:ou) {
            	System.out.println(s+"==========");
            }
            if (ou != null) {
                System.out.println("OU for " + cn + ": " + ou);
            } else {
                System.out.println("OU not found for " + cn);
            }
        } catch (LDAPException e) {
            e.printStackTrace();
        }
    }
}
