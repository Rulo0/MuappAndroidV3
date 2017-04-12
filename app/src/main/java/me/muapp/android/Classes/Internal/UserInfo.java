package me.muapp.android.Classes.Internal;

/**
 * Created by rulo on 12/04/17.
 */

public class UserInfo {
    Boolean hasInstagramToken;
    String instagramToken;

    public UserInfo() {
    }

    public Boolean getHasInstagramToken() {
        return hasInstagramToken;
    }

    public String getInstagramToken() {
        return instagramToken;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "hasInstagramToken=" + hasInstagramToken +
                ", instagramToken='" + instagramToken + '\'' +
                '}';
    }
}
