package com.restaurant.service;

import com.restaurant.dao.UtilisateurDAO;
import com.restaurant.model.Utilisateur;

public class AuthService {

    private final UtilisateurDAO dao = new UtilisateurDAO();

    public Utilisateur authentifier(String login, String motDePasseClair) {
        // ⚠️ ON NE HASH PAS ICI
        return dao.login(login, motDePasseClair);
    }
}
