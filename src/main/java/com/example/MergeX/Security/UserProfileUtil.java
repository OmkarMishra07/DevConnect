package com.example.MergeX.Security;

import com.example.MergeX.model.User;

public class UserProfileUtil {

    public static boolean isProfileComplete(User user) {
        return user.getEducation() != null &&
                user.getCollege() != null &&
                user.getGithubUrl() != null &&
                user.getBio() != null;
    }
}
