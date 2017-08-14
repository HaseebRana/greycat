/**
 * Copyright 2017 DataThings - All rights reserved.
 */
package greycat.ac;


import greycat.Callback;
import greycat.Node;

import java.util.Map;

/**
 * Created by Gregory NAIN on 18/07/2017.
 */
public interface AuthenticationManager {

    AuthenticationManager setUsersIndexName(String usersIndexName);

    AuthenticationManager setLoginAttribute(String loginAttribute);

    AuthenticationManager setPasswordAttribute(String passwordAttribute);

    AuthenticationManager setFirstAdminLogin(String adminLogin);

    AuthenticationManager activateTwoFactorsAuth(String issuer, boolean strict);

    void resetTwoFactorSecret(long uid, Callback<String> newSecret);

    void setSecret(long uid, String secret, Callback<Boolean> done);

    String getAuthenticatorUri(Node user, String secret);

    void revokeTwoFactorSecret(long uid, Callback<Boolean> done);

    AuthenticationManager setPasswordChangeKeyValidity(long duration);

    void verifyCredentials(Map<String, String> credentials, Callback<Long> callback);

    void createPasswordChangeAuthKey(long uid, Callback<String> callback);

    void resetPassword(String authKey, String newPass, Callback<Integer> callback);

    void load(Callback<Boolean> done);

    void save(Callback<Boolean> done);

    void loadInitialData(boolean createAdminAtBoot, Callback<Boolean> done);

    void printCurrentConfiguration(StringBuilder sb);
}