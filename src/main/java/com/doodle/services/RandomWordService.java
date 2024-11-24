package com.doodle.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RandomWordService {

    private static final List<String> WORD_LIST = List.of(
            "Apple", "Airplane", "Dog", "Cat", "Tree", "House", "Sun", "Moon", "Car", "Bicycle", "Pizza",
            "Rocket", "Chair", "Table", "Butterfly", "Guitar", "Ice cream", "Elephant", "Hat", "Ball", "Book",
            "Bird", "Cloud", "Flower", "Fish", "Shark", "Boat", "Camera", "Mountain", "Rainbow", "Skyscraper",
            "Balloon", "School", "T-shirt", "Treehouse", "Toothbrush", "Banana", "Cupcake", "Zebra", "Lion",
            "Wolf", "Spider", "Tiger", "Bear", "Robot", "Iceberg", "Sandcastle", "Helicopter", "Sunglasses",
            "Starfish", "Telephone", "Watermelon", "Whale", "Snail", "Burger", "Ladder", "Clock", "Ship",
            "Tractor", "Castle", "Spider", "Cup", "Ice cream cone", "Jellyfish", "Notebook", "Camera", "Mountain",
            "Tornado", "Spiderweb", "Octopus", "Planet", "Firetruck", "Snowman", "Tent", "Guitar", "Seahorse",
            "Dragon", "Skeleton", "Helicopter", "Hot Air Balloon", "Ice Skates", "Pastry", "Violin", "Crocodile",
            "Deer", "Trampoline", "Skateboard", "Scissors", "Wizard", "Ghost", "Horse", "King", "Queen", "Santa", "Monster"
    );

    public List<String> getRandomWords() {
        Random random = new Random();
        List<String> selectedWords = new ArrayList<>();

        while (selectedWords.size() < 3) {
            String word = WORD_LIST.get(random.nextInt(WORD_LIST.size()));
            if (!selectedWords.contains(word)) {
                selectedWords.add(word);
            }
        }

        return selectedWords;
    }
}
