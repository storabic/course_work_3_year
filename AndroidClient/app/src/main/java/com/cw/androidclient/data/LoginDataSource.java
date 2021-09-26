package com.cw.androidclient.data;

import android.util.Log;

import com.cw.androidclient.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        LoggedInUser user;
        switch (username) {
            case "daniilivanov82@gmail.com":
                user = new LoggedInUser(1L, "daniilivanov82@gmail.com");
                if (!password.equals("vafavafa")) {
                    return new Result.Error(new IOException("Error logging in"));
                }
                break;
            case "storabic@yandex.ru":
                user = new LoggedInUser(2L, "storabic@yandex.ru");
                if (!password.equals("vafavafa")) {
                    return new Result.Error(new IOException("Error logging in"));
                }
                break;
            case "randomuser":
                user = new LoggedInUser(3L, "randomuser");
                if (!password.equals("123456")) {
                    return new Result.Error(new IOException("Error logging in"));
                }
                break;
            default:
                return new Result.Error(new IOException("Error logging in"));
        }
        return new Result.Success<>(user);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}