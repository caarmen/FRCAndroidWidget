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

package ca.rmen.android.frccommon

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.Locale

class FRCDateUtilsTest {
    @Test
    fun testRomanNumerals() {
        val answers = File("src/test/resources/roman-numerals.txt")
        val errors = ArrayList<String>()
        var i = 0
        answers.forEachLine { line ->
            if (i > 0) {
                val actual = FRCDateUtils.getRomanNumeral(i)
                val expected = line
                if (expected != actual) {
                    errors.add(String.format(Locale.US, "%d: Got %s instead of %s", i, actual, expected));
                }
            }
            i++
        }

        if (!errors.isEmpty()) {
            val builder = StringBuilder(30 * errors.size)
            errors.forEach { error -> builder.append(error).append('\n') }
            Assert.fail(builder.toString())
        }
    }
}
