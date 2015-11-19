package com.src.java.framework.utils;

import framework.DataStore;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.testng.Assert;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileOperations {

    private static final String ERROR_MESSAGE = "Given file was not found or cannot be read";

    public Boolean isFileExist(String path) {
        File fileToCheck = new File(path);
        return fileToCheck.exists();
    }

    public Boolean isTextPresent(File fileToRead, String regExp) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead), 2048)) {
            Pattern textToFind = Pattern.compile(regExp);
            Matcher textToFindMatcher = textToFind.matcher("");
            LineNumberReader lineReader = new LineNumberReader(reader);
            for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                textToFindMatcher.reset(line);
                if (textToFindMatcher.find()) {
                    return true;
                }
            }
        } catch (IOException | NullPointerException e) {
            Assert.fail(ERROR_MESSAGE);
        }
        return false;
    }

    public long getFileSize(String path) {
        File fileToCheck = new File(path);
        return fileToCheck.length();
    }

    public String readFile(File fileToRead, boolean onlyFirstLine) {
        String fileContent = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead), 2048)) {
            LineNumberReader lineReader = new LineNumberReader(reader);
            for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                if (onlyFirstLine) {
                    return line;
                }
                fileContent += ("\t" + line + "\n");
            }
        } catch (IOException | NullPointerException e) {
            Assert.fail(ERROR_MESSAGE);
        }
        return fileContent;
    }

    public String readLastLine(File fileToRead) {
        String lineInFile = "";
        try (ReversedLinesFileReader linesFileReader = new ReversedLinesFileReader(fileToRead)) {
            lineInFile = linesFileReader.readLine();
        } catch (IOException | NullPointerException e) {
            Assert.fail(ERROR_MESSAGE);
        }
        return lineInFile;
    }

    public String readLastLines(File file, int linesToRead) {
        return readLastLines(file, linesToRead, false);
    }

    public String readLastLines(File file, int linesToRead, boolean ignoreExceptions) {
        List<String> lines = new LinkedList<>();
        try (FileInputStream in = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            for (String tmp; (tmp = br.readLine()) != null;)
                if (lines.add(tmp + "\n") && lines.size() > linesToRead)
                    lines.remove(0);
        } catch (IOException e) {
            if (!ignoreExceptions) {
                Assert.fail(ERROR_MESSAGE);
            }
            return "Information not available";
        }
        return lines.toString().replaceAll(",", "");
    }

    public String readSolidFile(File fileToRead) {
        String fileContent = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead))) {
            LineNumberReader lineReader = new LineNumberReader(reader);
            for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                fileContent += (line);
            }
        }
        catch (IOException | NullPointerException e) {
            Assert.fail(ERROR_MESSAGE);
        }
        return fileContent;
    }

    public void runFileViaProperty(String property) {
        try {
            Runtime.getRuntime().exec(DataStore.getInstance().getProperties().getProperty(property));
        } catch (IOException e) {
            Assert.fail(ERROR_MESSAGE);
        }
    }

    public void runFile(String path) {
        try {
            Runtime.getRuntime().exec(path);
        } catch (IOException e) {
            Assert.fail(ERROR_MESSAGE);
        }
    }

    public void runFileViaProcessBuilder(String file, String parameter) {
        try {
            new ProcessBuilder(file,  parameter).start();
        } catch (IOException e) {
            Assert.fail(ERROR_MESSAGE);
        }
    }

    public void writeFile(File path, String textToWrite, boolean multiline) {
        try (FileWriter writer = new FileWriter(path, true)) {
            if (multiline) {
                writer.write(textToWrite);
                writer.write(System.lineSeparator());
            } else {
                writer.write(textToWrite);
            }
        }
        catch (IOException e) {
            Assert.fail(ERROR_MESSAGE);
        }
    }

    public void cleanFile(File path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("");
            BasePage.sleepMS(500);
        }
        catch (IOException e) {
            Assert.fail(ERROR_MESSAGE);
        }
    }
}