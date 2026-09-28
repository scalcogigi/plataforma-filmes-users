package org.example.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "users")
public class User {
    @Id
    private String id;

    // identificador fornecido pelo Auth0
    private String auth0UserId;

    private String username;
    private String email;
    private String displayName;
    private String profileImageUrl;
    private String bio;

    private List<String> filmesAssistidosIds = new ArrayList<>();
    private List<String> filmesFavoritosIds = new ArrayList<>();

    private List<String> seguidoresIds = new ArrayList<>();
    private List<String> seguindoIds = new ArrayList<>();


    private Instant createdAt;
    private Instant updateAt;

    public User() { }

    public User(String auth0UserId, String username, String email) {
        this.auth0UserId = auth0UserId;
        this.username = username;
        this.email = email;
        this.createdAt = Instant.now();
        this.updateAt = Instant.now();
    }

    public String getAuth0UserId() {
        return auth0UserId;
    }

    public void setAuth0UserId(String auth0UserId) {
        this.auth0UserId = auth0UserId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFilmesAssistidosIds() {
        return filmesAssistidosIds;
    }

    public void setFilmesAssistidosIds(List<String> filmesAssistidosIds) {
        this.filmesAssistidosIds = filmesAssistidosIds;
    }

    public List<String> getFilmesFavoritosIds() {
        return filmesFavoritosIds;
    }

    public void setFilmesFavoritosIds(List<String> filmesFavoritosIds) {
        this.filmesFavoritosIds = filmesFavoritosIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<String> getSeguidoresIds() {
        return seguidoresIds;
    }

    public void setSeguidoresIds(List<String> seguidoresIds) {
        this.seguidoresIds = seguidoresIds;
    }

    public List<String> getSeguindoIds() {
        return seguindoIds;
    }

    public void setSeguindoIds(List<String> seguindoIds) {
        this.seguindoIds = seguindoIds;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
