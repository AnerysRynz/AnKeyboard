package com.ankeyboard.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LearningDictionary {
    private SharedPreferences prefs;
    private static final String PREF_NAME = "AnKeyboard_Brain";

    public LearningDictionary(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void learnWord(String word) {
        if (word == null || word.trim().length() < 2) return; // Abaikan kata terlalu pendek
        
        String key = word.toLowerCase().trim();
        
        int currentFreq = prefs.getInt(key, 0);
        
        prefs.edit().putInt(key, currentFreq + 1).apply();
    }

    public List<String> getPredictions(String composingText) {
        String prefix = composingText.toLowerCase();
        List<Map.Entry<String, ?>> allWords = new ArrayList<>(prefs.getAll().entrySet());
        List<Map.Entry<String, ?>> matches = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allWords) {
            if (entry.getValue() instanceof Integer) {
                if (entry.getKey().startsWith(prefix)) {
                    matches.add(entry);
                }
            }
        }

        Collections.sort(matches, new Comparator<Map.Entry<String, ?>>() {
            @Override
            public int compare(Map.Entry<String, ?> o1, Map.Entry<String, ?> o2) {
                Integer freq1 = (Integer) o1.getValue();
                Integer freq2 = (Integer) o2.getValue();
                return freq2.compareTo(freq1);
            }
        });

        List<String> results = new ArrayList<>();
        if (!prefix.isEmpty()) {
            results.add(composingText); 
        }

        for (int i = 0; i < Math.min(10, matches.size()); i++) {
            String word = matches.get(i).getKey();
            if (!word.equalsIgnoreCase(composingText)) {
                results.add(word);
            }
        }
        
        return results;
    }
}
