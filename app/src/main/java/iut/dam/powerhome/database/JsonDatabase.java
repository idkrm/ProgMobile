package iut.dam.powerhome.database;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import entities.User;

public class JsonDatabase {
    private static final String FILE_NAME = "users.json";
    private Context context;
    private Gson gson = new Gson();

    public JsonDatabase(Context context) {
        this.context = context;
    }

    // Lire la liste des utilisateurs depuis le fichier JSON
    public List<User> readUsers() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            return null; // Retourne null si le fichier n'existe pas
        }

        try (FileReader reader = new FileReader(file)) {
            Type userListType = new TypeToken<List<User>>() {}.getType();
            return gson.fromJson(reader, userListType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Écrire la liste des utilisateurs dans le fichier JSON
    private void writeUsers(List<User> users) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ajouter un nouvel utilisateur
    public void addUser(User newUser) {
        List<User> users = readUsers();
        if (users == null) {
            users = new ArrayList<>();
        }

        // Générer un ID unique pour le nouvel utilisateur
        newUser.setId(users.size() + 1);

        // Ajouter le nouvel utilisateur à la liste
        users.add(newUser);

        // Écrire la liste mise à jour dans le fichier JSON
        writeUsers(users);
    }

    public boolean isEmailExists(String email) {
        List<User> users = readUsers();
        if (users == null) {
            return false;
        }

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
}
