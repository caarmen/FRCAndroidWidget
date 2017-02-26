/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2017 Carmen Alvarez
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package ca.rmen.android.frccommon;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FRCDateUtilsTest {

    @Test
    public void testRomanNumerals() throws IOException {
        File answers = new File("src/test/resources/roman-numerals.txt");
        BufferedReader reader = new BufferedReader(new FileReader(answers));
        int i = 0;
        List<String> errors = new ArrayList<>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (i > 0) {
                String actual = FRCDateUtils.getRomanNumeral(i);
                String expected = line;
                if (!actual.equals(expected)) {
                    errors.add(String.format(Locale.US, "%d: Got %s instead of %s", i, actual, expected));
                }
            }
            i++;
        }

        if (!errors.isEmpty()) {
            StringBuilder builder = new StringBuilder(30 * errors.size());
            for (String error : errors) {
                builder.append(error).append('\n');
            }
            Assert.fail(builder.toString());
        }

    }
}