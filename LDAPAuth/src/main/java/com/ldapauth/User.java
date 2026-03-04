package com.ldapauth;

import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Entry(objectClasses = { "person", "inetOrgPerson", "top" })
public final class User  {
        
	@Id
	private Name dn; 
   
	 @Value("${ldap.partitionSuffix}")
	 private String ldapPartitionSuffix; 
	 
	private @Attribute(name = "cn") String username;
    private @Attribute(name = "userPassword") String password;
    private @Attribute(name = "ou") String ou;
    
	public User() {
    }

    public User(String username, String password,String ou, Name dn) {
        this.username = username;
        this.password = password;
        this.ou = ou;
        this.dn = constructUserDn(username); // Constructing the user's DN
    }

 

	public Name getDn() {
		return dn;
	}

	public void setDn(Name dn) {
		this.dn = dn;
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}
    @Override
    public String toString() {
        return username;
    }
    
    private Name constructUserDn(String username) {
   	 try {
            return new LdapName("cn=" + username + "," + ldapPartitionSuffix);
        } catch (Exception e) {
            // Handle exception
            return null;
        }		
	}
}