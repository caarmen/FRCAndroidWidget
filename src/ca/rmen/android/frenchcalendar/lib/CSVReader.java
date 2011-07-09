/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.android.frenchcalendar.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Simple CSV reader. Does not support commas inside cells, even if the cell is
 * surrounded by quotes. Does not unescape any characters: if quotes are
 * included in a cell, they will be included in the value read. Assumes the
 * first line in the CSV file is a header (with column names).
 *
 * @author Carmen Alvarez
 *
 */
public class CSVReader {

    private BufferedReader reader = null;
    private static final String FIELD_SEPARATOR = ",";

    private String[] header = null;
    private String[] currentLine = null;

    private int currentLineNumber = 0;

    /**
     * Open the stream for reading. Reads the header line.
     *
     * @param is
     * @throws IOException
     */
    public CSVReader(InputStream is) throws IOException {
        reader = new BufferedReader(new InputStreamReader(is));
        // read the header
        String line = reader.readLine();
        header = line.split(FIELD_SEPARATOR);
    }

    /**
     * Closes the stream.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Read the next line in the file.
     *
     * @return true if a line was read, false if EOF was reached.
     * @throws IOException
     */
    public boolean next() throws IOException {
        currentLineNumber++;
        String line = reader.readLine();
        if (line == null)
            return false;
        currentLine = line.split(FIELD_SEPARATOR);
        return true;
    }

    /**
     *
     * @return the list of cells in the current line.
     */
    public String[] curentLine() {
        return currentLine;
    }

    /**
     *
     * @return the list of cells in the first line. Normally this is the column
     *         headings.
     */
    public String[] getHeader() {
        return header;
    }

    /**
     *
     * @return the current line number we are reading. The header is line 0.
     */
    public int getLineNumber() {
        return currentLineNumber;
    }

    /**
     * For the current line, return the value of the cell corresponding to the
     * given column name.
     *
     * @param fieldName
     * @return the value of the cell in the current line corresponding to the
     *         given column name. If we haven't read any line yet, null is
     *         returned.
     */
    public String getValue(String fieldName) {
        // Return noting if this is the header line.
        if (currentLineNumber < 1)
            return null;
        int column = 0;
        for (; column < header.length; column++) {
            if (header[column].equals(fieldName))
                break;
        }
        if (column >= currentLine.length)
            return null;
        return currentLine[column];
    }
}
