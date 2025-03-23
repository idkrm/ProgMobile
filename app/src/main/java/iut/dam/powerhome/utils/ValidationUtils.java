package iut.dam.powerhome.utils;

public class ValidationUtils {

    // Expression régulière pour valider un e-mail
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // Expression régulière pour valider un mot de passe (au moins 8 caractères, une majuscule, un chiffre)
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

    // Valider l'e-mail
    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    // Valider le mot de passe
    public static boolean isValidPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }


    // Valider le numéro de téléphone en fonction du préfixe
    public static boolean isValidPhoneNumber(String prefix, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        // Vérifier la longueur du numéro en fonction du préfixe
        switch (prefix) {
            case "+33": // France
                return phoneNumber.length() == 9; // 9 chiffres après +33
            case "+44": // Royaume-Uni
                return phoneNumber.length() == 10; // 10 chiffres après +44
            case "+34": // Espagne
                return phoneNumber.length() == 9; // 9 chiffres après +34
            default:
                return false; // Préfixe non reconnu
        }
    }
}
