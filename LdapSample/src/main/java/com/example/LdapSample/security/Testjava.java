package com.example.LdapSample.security;

public class Testjava {
    
    public static void main(String[] args) {
        System.out.println("Application Started");

        TestJava test = new TestJava();
        test.calculateSum(10, 20);
        test.checkUser("Imran");
    }

    // Method to calculate sum
    public int calculateSum(int a, int b) {
        int result = a + b;
        System.out.println("Sum is: " + result);
        return result;
    }

    // Method to check user
    public void checkUser(String username) {
        if(username == null || username.isEmpty()) {
            System.out.println("Invalid User");
        } else {
            System.out.println("Welcome " + username);
        }
    }


}
