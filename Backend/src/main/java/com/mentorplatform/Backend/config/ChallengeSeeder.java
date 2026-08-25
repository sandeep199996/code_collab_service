package com.mentorplatform.Backend.config;

import com.mentorplatform.Backend.entity.CodingChallenge;
import com.mentorplatform.Backend.repository.CodingChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ChallengeSeeder implements CommandLineRunner {

    @Autowired
    private CodingChallengeRepository challengeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only seed data if the table is currently empty
        if (challengeRepository.count() == 0) {

            // Challenge 1: Two Sum
            CodingChallenge twoSum = new CodingChallenge();
            twoSum.setTitle("Two Sum");
            twoSum.setDifficulty("EASY");
            twoSum.setDescription("Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.");
            twoSum.setStarterCode("function twoSum(nums, target) {\n    // Write your code here\n    \n}");
            twoSum.setTestCases("[{\"input\": \"[2,7,11,15], 9\", \"expected\": \"[0,1]\"}]");
            challengeRepository.save(twoSum);
            twoSum.setAuthorEmail("SYSTEM");
            twoSum.setPublic(true);


            // Challenge 2: FizzBuzz
            CodingChallenge fizzBuzz = new CodingChallenge();
            fizzBuzz.setTitle("FizzBuzz");
            fizzBuzz.setDifficulty("EASY");
            fizzBuzz.setDescription("Write a program that outputs the string representation of numbers from 1 to n. For multiples of three, output 'Fizz'. For multiples of five, output 'Buzz'.");
            fizzBuzz.setStarterCode("function fizzBuzz(n) {\n    // Write your code here\n    \n}");
            fizzBuzz.setTestCases("[{\"input\": \"15\", \"expected\": \"[..., 'Fizz', 'Buzz', '11', 'Fizz', '13', '14', 'FizzBuzz']\"}]");
            challengeRepository.save(fizzBuzz);
            fizzBuzz.setAuthorEmail("SYSTEM");
            fizzBuzz.setPublic(true);
            System.out.println("✅ Coding Challenges Seeded Successfully!");
        }
    }
}