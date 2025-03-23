package iut.dam.powerhome.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    // Expression régulière pour valider un e-mail
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Expression régulière pour valider un mot de passe (au moins 8 caractères, une majuscule, un chiffre)
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

    // Expression régulière pour valider un numéro de téléphone (10 chiffres)
    private static final String PHONE_REGEX = "^[0-9]{10}$";

    // Valider l'e-mail
    public static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    // Valider le mot de passe
    public static boolean isValidPassword(String password) {
        return Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
    }

    // Valider le numéro de téléphone
    public static boolean isValidPhone(String phone) {
        return Pattern.compile(PHONE_REGEX).matcher(phone).matches();
    }
}
