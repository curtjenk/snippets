package org.example.graphql.controller;

public class Util {
    private static String encryptValueAdd = "Hello";
    private static String decryptValueAdd = "World";
    private static String hashValue = "iamhasedwithsha256";
    public static String encrypt(String val){
        return val + encryptValueAdd;
    }

    public static String decrypt(String val) {
        return val + decryptValueAdd;
    }

    public static String hash(String val) {
        return hashValue;
    }
}
