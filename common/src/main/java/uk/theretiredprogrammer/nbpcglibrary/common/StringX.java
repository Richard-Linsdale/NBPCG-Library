/*
 * Copyright 2015-2017 Richard Linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.nbpcglibrary.common;

/**
 * A Set of Static methods which extends String processing functionality.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class StringX {

    /**
     * Pad a string to the given length with a defined padding char inserted to
     * the left of the string.
     *
     * @param s the original string
     * @param requiredLength the target length
     * @param padchar the padding character
     * @return the padded string
     */
    public static String padLeft(String s, int requiredLength, char padchar) {
        int l = s.length();
        if (l >= requiredLength) {
            return s;
        }
        StringBuilder sb = new StringBuilder(requiredLength);
        for (int i = 0; i < (requiredLength - l); i++) {
            sb.append(padchar);
        }
        sb.append(s);
        return sb.toString();
    }

    /**
     * Pad a string to the given length with a defined padding char inserted to
     * the left of the string, if the original string is an integer, otherwise
     * return the original string.
     *
     * @param s the original string
     * @param requiredLength the target length
     * @param padchar the padding character
     * @return the padded string
     */
    public static String padLeftIfInt(String s, int requiredLength, char padchar) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return s;
        }
        return padLeft(s, requiredLength, padchar);
    }

    /**
     * Split a string on breaks defined by a regular expression and trim the
     * resulting strings.
     *
     * @param s the original string
     * @param regex the regular expression used to define the splits
     * @return the array of strings representing the extracted component strings
     */
    public static String[] splitAndTrim(String s, String regex) {
        String[] split = s.split(regex);
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }
        return split;
    }

    /**
     * Split a string on breaks defined by a regular expression and trim the
     * resulting strings. The method will extracts a defined number of strings,
     * if there are not enough string extracted from the regular expression
     * split, then the first string will be used to complete the required number
     * of strings.
     *
     * @param s the original string
     * @param regex the regular expression used to define the splits
     * @param l the number of string to be extracted
     * @return the array of strings representing the extracted component strings
     */
    public static String[] splitAndTrim(String s, String regex, int l) {
        String[] res = new String[l];
        String[] split = splitAndTrim(s, regex);
        String missing = split.length > 0 ? split[0] : "";
        for (int i = 0; i < res.length; i++) {
            res[i] = i < split.length ? split[i] : missing;
        }
        return res;
    }

    /**
     * Split a string on breaks defined by a regular expression and trim the
     * resulting strings. The method will extracts a defined number of strings,
     * if there are not enough string extracted from the regular expression
     * split, then the defined missing string will be used to complete the
     * required number of strings.
     *
     * @param s the original string
     * @param regex the regular expression used to define the splits
     * @param l the number of string to be extracted
     * @param missing the string to be substituted for any undefined strings
     * @return the array of strings representing the extracted component strings
     */
    public static String[] splitAndTrim(String s, String regex, int l, String missing) {
        String[] res = new String[l];
        if ("".equals(s.trim())) {
            for (int i = 0; i < l; i++) {
                res[i] = missing;
            }
        } else {
            String[] split = splitAndTrim(s, regex);
            for (int i = 0; i < l; i++) {
                res[i] = i < split.length ? split[i] : missing;
            }
        }
        return res;
    }

    /**
     * Split a string at "," delimiters and trim the resulting strings.
     *
     * @param s the original string
     * @return the array of strings representing the extracted component strings
     */
    public static String[] splitAndTrim(String s) {
        return splitAndTrim(s, ",");
    }

    /**
     * Split a string at "," delimiters and trim the resulting strings. The
     * method will extracts a defined number of strings, if there are not enough
     * string extracted from the regular expression split, then the first string
     * will be used to complete the required number of strings.
     *
     * @param s the original string
     * @param l the number of string to be extracted
     * @return the array of strings representing the extracted component strings
     */
    public static String[] splitAndTrim(String s, int l) {
        return splitAndTrim(s, ",", l);
    }

    /**
     * Split a string at "," delimiters and trim the resulting strings. The
     * method will extracts a defined number of strings, if there are not enough
     * string extracted from the regular expression split, then the defined
     * missing string will be used to complete the required number of strings.
     *
     * @param s the original string
     * @param l the number of string to be extracted
     * @param missing the string to be substituted for any undefined strings
     * @return the array of strings representing the extracted component strings
     */
    public static String[] splitAndTrim(String s, int l, String missing) {
        return splitAndTrim(s, ",", l, missing);
    }
}
