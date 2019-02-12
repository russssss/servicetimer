package com.example.servicetimer;

public class DataConverter {
    public static String intToRoman(int number) {
        String out = "";
        while (number >= 100) {
            out += "C";
            number -= 100;
        }
        while (number >= 90) {
            out += "XC";
            number -= 90;
        }
        while (number >= 50) {
            out += "L";
            number -= 50;
        }
        while (number >= 40) {
            out += "XL";
            number -= 40;
        }
        while (number >= 10) {
            out += "X";
            number -= 10;
        }
        while (number >= 9) {
            out += "IX";
            number -= 9;
        }
        while (number >= 5) {
            out += "V";
            number -= 5;
        }
        while (number >= 4) {
            out += "IV";
            number -= 4;
        }
        while (number >= 1) {
            out += "I";
            number -= 1;
        }
        return out;
    }
}