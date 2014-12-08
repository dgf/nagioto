package de.g2d.nagioto.domain;

public class Settings {
    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    private Integer seconds;
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
                "username='" + username + '\'' +
                ", seconds='" + seconds + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
