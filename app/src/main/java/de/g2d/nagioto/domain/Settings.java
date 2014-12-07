package de.g2d.nagioto.domain;

public class Settings {
    private String password;
    private String username;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @Override
    public String toString() {
        return "Settings{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
