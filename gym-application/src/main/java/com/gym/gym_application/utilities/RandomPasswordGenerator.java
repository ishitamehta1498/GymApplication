package com.gym.gym_application.utilities;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomPasswordGenerator {
    private RandomPasswordGenerator() {
    }

    public static String generateRandomPassword() {
//        char[] possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&+=")
//                .toCharArray();
//        return RandomStringUtils.random(10, 0, possibleCharacters.length - 1, false, false, possibleCharacters,
//                new SecureRandom());
        return generateStrongPassword(10);
    }

    public static String generateStrongPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%^&+=";
        String allChars = upper + lower + digits + special;

        SecureRandom random = new SecureRandom();

        // Ensure at least one from each
        StringBuilder password = new StringBuilder();
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill the rest
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle to remove predictability
        List<Character> pwdChars = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars, random);

        return pwdChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

}
