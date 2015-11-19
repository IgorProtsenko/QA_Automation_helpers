package com.src.java.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebHelpers {

    protected WebDriver driver;
    protected Actions actions;
    private static int[] counter = new int[4];

    public WebHelpers(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(this.driver);
    }

    public void acceptAlert() {
        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException ignored) {}
        driver.switchTo().defaultContent();
    }

    public boolean areElementsDisplayed(String locator, int timeout) {
        setLimitedTimeout(timeout);
        try {
            if (this.driver.findElements(By.cssSelector(locator)).size() > 0) {
                setDefaultTimeout();
                return true;
            }
        }
        catch (NoSuchElementException ignored) {}
        setDefaultTimeout();
        return false;
    }

    public void doubleClickOnElement(WebElement element) {
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(element));
        actions.doubleClick(element).build().perform();
        sleep(2);
    }

    public void findElementByTextAndClick(String textInElement, String locator, boolean isDoubleClick) {
        findElementByTextAndClick(textInElement, locator, isDoubleClick, false);
    }

    public void findElementByTextAndClick(String textInElement, String locator, boolean isDoubleClick, boolean recheck) {
        findElementByTextAndClick(textInElement, locator, isDoubleClick, recheck, false, 7);
    }

    public void findElementByTextAndClick(String textInElement, String locator, boolean isDoubleClick, boolean recheck, int timeout) {
        findElementByTextAndClick(textInElement, locator, isDoubleClick, recheck, false, timeout);
    }

    public void findElementByTextAndClick(String textInElement, String locator, boolean isDoubleClick, boolean recheck, boolean checkIfClickable) {
        findElementByTextAndClick(textInElement, locator, isDoubleClick, recheck, checkIfClickable, 7);
    }

    public void findElementByTextAndClick(String textInElement, String locator, boolean isDoubleClick, boolean recheck, boolean checkIfClickable, int timeout) {
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        setLimitedTimeout(1);
        while (System.currentTimeMillis() <= expiredTimeout) {
            List<WebElement> buttons = driver.findElements(By.cssSelector(locator));
            try {
                for (WebElement currentElement : buttons) {
                    if (currentElement.getText().toLowerCase().contains(textInElement.toLowerCase())) {
                        if (checkIfClickable) {
                            waitForElementToBeClickable(currentElement, timeout);
                        }
                        if (isDoubleClick) {
                            doubleClickOnElement(currentElement);
                        } else {
                            currentElement.click();
                        }
                        setDefaultTimeout();
                        sleep(2);
                        return;
                    }
                }
            } catch (StaleElementReferenceException e) {
                if (counter[0] < 1) {
                    counter[0]++;
                    findElementByTextAndClick(textInElement, locator, isDoubleClick, recheck, checkIfClickable, timeout);
                }
            }
            buttons.clear();
            sleep(1);
        }
        if (recheck & waitForElementByText(textInElement, locator, 3)) {
            findElementByTextAndClick(textInElement, locator, isDoubleClick, false, checkIfClickable, timeout);
        }
        sleep(1);
        counter[0] = 0;
        setDefaultTimeout();
    }

    public void findElementByAttributeAndClick(String attribute, String attributeText, String locator, boolean isDoubleClick) {
        long expiredTimeout = System.currentTimeMillis() + 5000;
        setLimitedTimeout(1);
        while (System.currentTimeMillis() <= expiredTimeout) {
            List<WebElement> buttons = driver.findElements(By.cssSelector(locator));
            try {
                setLimitedTimeout(1);
                for (WebElement currentElement : buttons) {
                    if (currentElement.getAttribute(attribute).equals(attributeText)) {
                        if (isDoubleClick) {
                            doubleClickOnElement(currentElement);
                        } else {
                            currentElement.click();
                        }
                        setDefaultTimeout();
                        sleep(1);
                    }
                }
            } catch (StaleElementReferenceException e) {
                findElementByAttributeAndClick(attribute, attributeText, locator, isDoubleClick);
            } catch (NullPointerException e) {
                continue;
            }
            buttons.clear();
            sleep(1);
        }
        setDefaultTimeout();
    }

    public WebElement findElement(String locator) {
        setLimitedTimeout(1);
        WebElement element;
        try {
            element = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locator)));
        } catch (ElementNotVisibleException | ElementNotFoundException e) {
            setDefaultTimeout();
            return null;
        }
        setDefaultTimeout();
        return element;
    }

    public WebElement findElement(String locator, boolean guaranteed) {
        setLimitedTimeout(1);
        WebElement element;
        try {
            element = driver.findElement(By.cssSelector(locator));
        } catch (ElementNotVisibleException | ElementNotFoundException | NoSuchElementException e) {
            setDefaultTimeout();
            return null;
        }
        setDefaultTimeout();
        return element;
    }

    public List<WebElement> findElements(String locator) {
        setLimitedTimeout(1);
        List<WebElement> elements;
        try {
            elements = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(locator)));
        } catch (ElementNotVisibleException | ElementNotFoundException e) {
            setDefaultTimeout();
            return null;
        }
        setDefaultTimeout();
        return elements;
    }

    public void freshClickOnElement(String locator, boolean singular) {
        setLimitedTimeout(2);
        if (singular) {
            driver.findElement(By.cssSelector(locator)).click();
            sleep(1);
        } else {
            driver.findElements(By.cssSelector(locator)).get(0).click();
            sleep(1);
        }
        setDefaultTimeout();
    }

    public void freshClickOnXPATHElement(String locator, boolean singular) {
        setLimitedTimeout(2);
        if (singular) {
            driver.findElement(By.xpath(locator)).click();
            sleep(1);
        } else {
            driver.findElements(By.xpath(locator)).get(0).click();
            sleep(1);
        }
        setDefaultTimeout();
    }

    public boolean isElementDisplayed(String locator, int timeout) {
        setLimitedTimeout(1);
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            try {
                WebElement webElement = driver.findElement(By.cssSelector(locator));
                webElement.isDisplayed();
                setDefaultTimeout();
                return true;
            } catch (NoSuchElementException | ElementNotVisibleException | StaleElementReferenceException e) {
                sleep(1);
            }
        }
        setDefaultTimeout();
        return false;
    }

    public boolean isElementDisplayedXPATH(String locator, int timeout) {
        setLimitedTimeout(1);
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            try {
                WebElement webElement = driver.findElement(By.xpath(locator));
                webElement.isDisplayed();
                setDefaultTimeout();
                return true;
            } catch (NoSuchElementException | ElementNotVisibleException | StaleElementReferenceException e) {
                sleep(1);
            }
        }
        setDefaultTimeout();
        return false;
    }

    public int getNumberOfElements(String locator, int timeout) {
        int numberOfElements = 0;
        setLimitedTimeout(timeout);
        try {
            numberOfElements = this.driver.findElements(By.cssSelector(locator)).size();
        }
        catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        setDefaultTimeout();
        return numberOfElements;
    }

    public String getRandomText() {
        return RandomStringUtils.random(10, true, false);
    }

    public void jsExecute(String script, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(script, element);
    }

    public void jsExecute(String input) {
        ((JavascriptExecutor) driver).executeScript(input);
    }

    public void lazyClickMS(WebElement elementInMS) {
        setLimitedTimeout(1);
        try {
            elementInMS.click();
        } finally {
            setDefaultTimeout();
        }
        setDefaultTimeout();
        sleepMS(500);
    }

    public void lazyClick(WebElement elementInSec) {
        elementInSec.click();
        sleep(2);
    }

    public void lazyClick(WebElement elementInSec, int timeoutInSec) {
        elementInSec.click();
        sleep(timeoutInSec);
    }

    public void markElement(WebElement elem) {
        try {
            jsExecute("arguments[0].style.border='3px solid red'", elem);
        } catch (Exception e) {
            System.out.println("Failed to mark element");
            e.printStackTrace();
        }
    }

    public void maximazeWindow() {
        driver.manage().window().maximize();
    }

    public void minimizeWindow() {
        driver.manage().window().setPosition(new Point(-2000, 0));
    }

    public void pressChord(Keys firstKey, String secondKey, boolean longWait) {
        actions.sendKeys(Keys.chord(firstKey, secondKey)).build().perform();
        sleepMS(500);
        if (longWait) {
            sleep(2);
        } else {
            sleepMS(500);
        }
    }

    public void pressButton(String button, boolean longWait) {
        actions.sendKeys(button).build().perform();
        if (longWait) {
            sleep(2);
        } else {
            sleepMS(500);
        }
    }

    public void pressKey(Keys button, boolean longWait) {
        actions.sendKeys(button).build().perform();
        if (longWait) {
            sleep(2);
        } else {
            sleepMS(500);
        }
    }

    public void refreshPage() {
        driver.navigate().refresh();
        acceptAlert();
        sleepMS(300);
    }

    public void repeatedClick(String locator) {
        driver.findElement(By.cssSelector(locator)).click();
        safeClick(findElement(locator, false), 3);
        sleep(2);
        if (isElementDisplayed(locator, 3)) {
            safeClick(findElement(locator, false), 3);
            sleep(1);
            if (isElementDisplayed(locator, 3)) {
                actions.moveToElement(findElement(locator)).click().build().perform();
            }
        }
    }

    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sleepMS(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void safeClick(WebElement element, int timeout, int delay) {
        setLimitedTimeout(1);
        try {
            element.click();
            counter[1] = 0;
        } catch (WebDriverException e) {
            if (counter[1] < timeout) {
                counter[1]++;
                sleep(1);
                safeClick(element, timeout, delay);
            }
        } catch (NullPointerException ignored) {}
        sleep(delay);
        setDefaultTimeout();
        counter[1] = 0;
    }

    public void safeClick(WebElement element, int timeout) {
        safeClick(element, timeout, 0);
    }

    public void sendButtonsToElement(WebElement element, String buttons, boolean longWait) {
        actions.sendKeys(element).sendKeys(buttons).build().perform();
        if (longWait) {
            sleep(2);
        } else {
            sleepMS(500);
        }
    }

    public void sendKeysToElement(WebElement element, Keys key, boolean longWait) {
        actions.sendKeys(element).sendKeys(key).build().perform();
        if (longWait) {
            sleep(2);
        } else {
            sleepMS(500);
        }
    }

    public void setLimitedTimeout(int timeout) {
        this.driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }

    public void setDefaultTimeout() {
        this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    public void smartClick(WebElement element) {
        String url = driver.getCurrentUrl();
        String title = driver.getTitle();
        int windowNumber = driver.getWindowHandles().size();
        waitForElementToBeClickable(element, 10);
        try  {
            actions.clickAndHold(element).build().perform();
            sleepMS(500);
            actions.release(element).build().perform();
        } catch (StaleElementReferenceException e) {
            counter[2]++;
            if (counter[2] < 2) {
                sleep(1);
                smartClick(element);
            }
        }
        sleep(3);
        try {
            if (driver.getWindowHandles().size() > windowNumber) return;
            if (driver.getCurrentUrl().equals(url) & driver.getTitle().equals(title)) {
                element.click();
                sleep(2);
                if (driver.getWindowHandles().size() > windowNumber) return;
                if (driver.getCurrentUrl().equals(url) & driver.getTitle().equals(title)) {
                    jsExecute("arguments[0].click()", element);
                }
            }
        } catch (WebDriverException ignored) {}
        counter[2] = 0;
        sleep(2);
    }

    public void takeScreenshot() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("HH.mm");
        File scrFile = ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
        try {
            String path = "target/screenshots/onDemand/" +  "(Time)_" + formatterTime.format(calendar.getTime()) + "__(Date)_" +
                    formatterDate.format(calendar.getTime()) + "__(TestClass)_" +
                    getClass().getSimpleName() + ".png";
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void waitForElementDisplayed(final WebElement element, int maxSleep) {
        try {
            new WebDriverWait(this.driver, maxSleep).
                    until(new ExpectedCondition<Boolean>() {
                        @Override public Boolean apply(org.openqa.selenium.WebDriver webDriver) {
                            return element.isDisplayed();
                        }
                    });
        } catch (StaleElementReferenceException ignored) {}
    }

    public void waitForElementDisplayed(final String locator, int maxSleep) {
        try {
            new WebDriverWait(driver, maxSleep).
                    until(new ExpectedCondition<Boolean>() {
                        @Override public Boolean apply(org.openqa.selenium.WebDriver webDriver) {
                            return driver.findElement(By.cssSelector(locator)).isDisplayed();
                        }
                    });
        } catch (StaleElementReferenceException e) {
            waitForElementDisplayed(locator, maxSleep);
        }
    }

    public void waitForElementDisplayedXPATH(final String locator, int maxSleep) {
        try {
            new WebDriverWait(driver, maxSleep).
                    until(new ExpectedCondition<Boolean>() {
                        @Override public Boolean apply(org.openqa.selenium.WebDriver webDriver) {
                            return driver.findElement(By.xpath(locator)).isDisplayed();
                        }
                    });
        } catch (StaleElementReferenceException ignored) {}
    }

    public void waitForElementsDisplayed(final List<WebElement> elements, int maxSleep) {
        new WebDriverWait(this.driver, maxSleep).
                until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public boolean waitForElementNotDisplayed(String locator, int timeout) {
        setLimitedTimeout(1);
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            try {
                sleep(1);
                WebElement webElement = driver.findElement(By.cssSelector(locator));
                if (!webElement.isDisplayed()) {
                    return false;
                }
            } catch (NoSuchElementException | ElementNotVisibleException | StaleElementReferenceException e) {
                setDefaultTimeout();
                return false;
            }
        }
        setDefaultTimeout();
        return false;
    }

    public void waitForFrameToBeAvailableAndSwitchToIt(String frameId, int timeout) {
        new WebDriverWait(driver, timeout).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameId));
        sleep(2);
    }

    public void waitForFrameToBeAvailableAndSwitchToIt(WebElement frameElement, int timeout) {
        new WebDriverWait(driver, timeout).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
        sleep(2);
    }

    public void waitForElementToBeClickable(WebElement element, int timeout) {
        try {
            if (counter[3] < 2) {
                new WebDriverWait(driver, timeout).until(ExpectedConditions.elementToBeClickable(element));
            }
        } catch (StaleElementReferenceException e) {
            sleep(2);
            counter[3]++;
            waitForElementToBeClickable(element, timeout);
        }
        counter[3] = 0;
        sleep(2);
    }

    public void waitForTitle(String title, int maxSleep) {
        new WebDriverWait(driver, maxSleep).until(ExpectedConditions.titleIs(title));
    }

    public boolean waitForElementByText(String textToFind, String locator, int timeout) {
        setLimitedTimeout(1);
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            List<WebElement> webElements = driver.findElements(By.cssSelector(locator));
            try {
                for (WebElement currentText : webElements) {
                    if (currentText.getText().toLowerCase().contains(textToFind.toLowerCase())) {
                        setDefaultTimeout();
                        sleep(2);
                        return true;
                    }
                }
            } catch (StaleElementReferenceException e) {
                webElements.clear();
                sleep(1);
                continue;
            }
            sleep(1);
            webElements.clear();
        }
        setDefaultTimeout();
        return false;
    }

    public void waitForAjax(int timeoutInSec) {
        long expiredTimeout = System.currentTimeMillis() + timeoutInSec * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            boolean ajaxIsComplete = (boolean) ((JavascriptExecutor) driver)
                    .executeScript("return jQuery.active == 0");
            if (ajaxIsComplete) {
                break;
            }
            sleep(1);
        }
    }
}
