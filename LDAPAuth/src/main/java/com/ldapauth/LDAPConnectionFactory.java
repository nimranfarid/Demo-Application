package com.ldapauth;
import org.springframework.stereotype.Component;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
@Component
public class LDAPConnectionFactory {

    private static LDAPConnection ldapConnection;

    private LDAPConnectionFactory() {
    }

    public static LDAPConnection getInstance(String ldapHost, int ldapPort, String ldapBindDN, String ldapBindPassword) throws LDAPException {
        if (ldapConnection == null) {
            synchronized (LDAPConnectionFactory.class) {
                if (ldapConnection == null) {
                    ldapConnection = new LDAPConnection(ldapHost, ldapPort, ldapBindDN, ldapBindPassword);
                }
            }
        }else {
        	 synchronized (LDAPConnectionFactory.class) {
                 if (!ldapConnection.isConnected()) {
                     ldapConnection = new LDAPConnection(ldapHost, ldapPort, ldapBindDN, ldapBindPassword);
                 }
             }
        }
        return ldapConnection;
    }
}
