package iut.dam.powerhome.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Crypter un mot de passe
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Vérifier un mot de passe crypté
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Gérer l'erreur si le salt est invalide
            e.printStackTrace();
            return false;
        }
    }
}