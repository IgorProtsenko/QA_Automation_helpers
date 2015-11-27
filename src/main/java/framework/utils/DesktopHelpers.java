package com.src.java.framework.utils;

import autoitx4java.AutoItX;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DesktopHelpers extends WebHelpers {

    protected AutoItX autoit;
    protected Screen sikuli;
    private static final String ERROR_MESSAGE = "Given element/file was not found";

    public DesktopHelpers(AutoItX autoit, Screen sikuli) {
        this.autoit = autoit;
        this.sikuli = sikuli;
    }

    public void clickButtonAutoit(String window, String button) {
        if (isWindowActiveAutoit(window, 2)) {
            autoit.winActivate(window);
        }
        autoit.controlClick(window, "", button);
    }

    public void clickSikuli(String element) throws FindFailed {
        clickSikuli(element, 1);
    }

    public void clickSikuli(String element, int timeout) throws FindFailed {
        try {
            waitForElementDisplayedSikuli(element, timeout);
            sikuli.click(element);
            sleep(1);
        } catch (FindFailed e) {
            throw new FindFailed(ERROR_MESSAGE);
        }
    }

    public void closeProcessAutoit(String processName) {
        autoit.processClose(processName);
        autoit.processWaitClose(processName, 5);
    }

    public void closeWindowAutoit(String window) {
        autoit.winClose(window);
    }

    public void copyFile(String source, String destination) throws IOException {
        try {
            FileUtils.copyFileToDirectory(new File(source), new File(destination));
        } catch (IOException e) {
            throw new IOException(ERROR_MESSAGE);
        }
    }

    public boolean deleteFileOrFolder(String path, boolean isFile, boolean ignoreError) throws IOException {
        try {
            if (isFile) {
                FileUtils.forceDelete(new File(path));
            } else {
                FileUtils.deleteDirectory(new File(path));
            }
        } catch (IOException e) {
            if (!ignoreError) {
                throw new IOException(ERROR_MESSAGE);
            } else {
                return false;
            }
        }
        return true;
    }

    public List<String> findWindowOrTrayText(String text, boolean isWindow) throws Exception {
        List <String> windows = new ArrayList<>();
        String arch = System.getProperty("os.arch");
        Process process;
        try {
            if (isWindow) {
                if (arch.equals("amd64")) {
                    process = Runtime.getRuntime().exec(Lab.automationRepo + "\\desktopData\\window_titles_x64.exe");
                } else {
                    process = Runtime.getRuntime().exec(Lab.automationRepo + "\\desktopData\\window_titles.exe");
                }
            } else {
                if (arch.equals("amd64")) {
                    process = Runtime.getRuntime().exec(Lab.automationRepo + "\\desktopData\\check_tray_x64.exe");
                } else {
                    process = Runtime.getRuntime().exec(Lab.automationRepo + "\\desktopData\\check_tray.exe");
                }
            }
        } catch (IOException e) {
            throw new IOException(ERROR_MESSAGE);
        }
        String line;
        if (process == null) {
            throw new NullPointerException(ERROR_MESSAGE);
        }
        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = stdout.readLine()) != null) {
                if (line.toLowerCase().contains(text.toLowerCase())) {
                    windows.add(line.toLowerCase());
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException ignored) {}
        return windows;
    }

    public String getAvailableElementSikuli(String... args) {
        return getAvailableElementSikuli(1, args);
    }

    public String getAvailableElementSikuli(int timeout, String... args) {
        for (String currentElement : args) {
            if (isElementDisplayedSikuli(currentElement, timeout))
                return currentElement;
        }
        return null;
    }

    public boolean isElementDisplayedSikuli(String element, int timeout) {
        try {
            sikuli.wait(element, (double) timeout);
        } catch (FindFailed e) {
            return false;
        }
        return true;
    }

    public boolean isFileExist(String path, int timeout) {
        File file = new File(path);
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            if (file.exists()) return true;
        }
        return false;
    }

    public boolean isWindowActiveAutoit(String window, int timeout) {
        return autoit.winWaitActive(window, "", timeout);
    }

    public void openDirectoryAndActivate(String pathToDirectory) {
        pathToDirectory = pathToDirectory.replace("/", "\\");
        autoit.run("explorer.exe " + pathToDirectory, "", AutoItX.SW_SHOW);
        String directoryName = pathToDirectory.split("\\\\")[pathToDirectory.split("\\\\").length - 1];
        autoit.winWaitActive(directoryName, "", 3);
        if (!autoit.winExists(directoryName)) {
            autoit.run("explorer.exe " + pathToDirectory, "", AutoItX.SW_SHOW);
            autoit.winWait(directoryName, "", 3);
            autoit.winActivate(directoryName);
        } else {
            autoit.winActivate(directoryName);
        }
    }

    public void pressKeyAutoit(String button) {
        autoit.send("{" + button + "}", false);
        sleep(1);
    }

    public void rightClickSikuli(String element) throws FindFailed {
        rightClickSikuli(element, 1);
    }

    public void rightClickSikuli(String element, int timeout) throws FindFailed {
        try {
            waitForElementDisplayedSikuli(element, timeout);
            sikuli.rightClick(element);
            sleep(1);
        } catch (FindFailed e) {
            throw new FindFailed(ERROR_MESSAGE);
        }
    }

    public void runFileAutoit(String path) throws IOException {
        if (autoit.run(path) == 0) {
            throw new IOException(ERROR_MESSAGE);
        }
    }

    public void typeSikuli(String element, String text, boolean clickBefore) throws FindFailed {
        try {
            if (clickBefore) {
                sikuli.click(element);
            }
            sikuli.type(text);
        } catch (FindFailed e) {
            throw new FindFailed(ERROR_MESSAGE);
        }
    }

    public void safeClickCikuli(String element) {
        try {
            sikuli.click(element);
            sleep(1);
        } catch (FindFailed ignored) {}
    }

    public void sleep(int seconds) {
        autoit.sleep(seconds * 1000);
    }

    public void takeScreenshot() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("HH.mm");
        BufferedImage image = null;
        try {
            image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(image, "png", new File(System.getProperty("user.dir") + "\\log\\(Time)_" + formatterTime.format(calendar.getTime()) + "__(Date)_" +
                    formatterDate.format(calendar.getTime()) + ".png"));
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    public void waitForElementDisplayedSikuli(String element, int timeout) throws FindFailed {
        try {
            sikuli.wait(element, (double) timeout);
        } catch (FindFailed e) {
            throw new FindFailed(ERROR_MESSAGE);
        }
    }
}