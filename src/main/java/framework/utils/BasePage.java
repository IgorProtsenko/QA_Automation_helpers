package features;

import framework.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import java.text.SimpleDateFormat;
import java.util.*;
import org.openqa.selenium.NoSuchElementException;
import framework.utils.WaitUtil;

public class BasePage extends WaitUtil {

    protected static org.openqa.selenium.WebDriver driver;
    protected Actions actions;
    private static MappingOperations mappingOperations;
    private static final int clickDelay = 1;
    private static int counter;

    public BasePage() {
        driver = framework.WebDriver.getInstance().getDriver();
        PageFactory.initElements(driver, this);
        actions = new Actions(driver);
    }

    public org.openqa.selenium.WebDriver getDriver() {
        return driver;
    }

    public static void setLimitedTimeout(int timeout) {
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }

    public static void setDefaultTimeout() {
        driver.manage().timeouts().implicitlyWait(WebDriver.getImplicitlyWaitTime(), TimeUnit.SECONDS);
    }

    public void acceptAlert() {
        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException ignored) {}
        driver.switchTo().defaultContent();
    }

    public boolean areElementsDisplayed(String locator, int timeout) {
        setLimitedTimeout(1);
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            try {
                if (driver.findElements(By.cssSelector(locator)).size() > 0) {
                    setDefaultTimeout();
                    return true;
                }
            } catch (NoSuchElementException e) {
                sleepMS(500);
            }
        }
        LOG_STEP.info("Element(s) were not displayed");
        setDefaultTimeout();
        return false;
    }

    public void doubleClickOnElement(WebElement element) {
        actions.doubleClick(element).build().perform();
        sleep(clickDelay);
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
            sleepMS(100);
        }
        setDefaultTimeout();
    }

    public void findElementByTextAndClick(String textInElement, String locator, boolean isDoubleClick) {
        long expiredTimeout = System.currentTimeMillis() + 5000;
        setLimitedTimeout(1);
        while (System.currentTimeMillis() <= expiredTimeout) {
            List<WebElement> buttons = driver.findElements(By.cssSelector(locator));
            try {
                for (WebElement currentElement : buttons) {
                    if (currentElement.getText().toLowerCase().contains(textInElement.toLowerCase())) {
                        if (isDoubleClick) {
                            doubleClickOnElement(currentElement);
                        } else {
                            currentElement.click();
                        }
                        setDefaultTimeout();
                        sleep(1);
                        return;
                    }
                }
            } catch (StaleElementReferenceException e) {
                findElementByTextAndClick(textInElement, locator, isDoubleClick);
            }
            buttons.clear();
            sleepMS(100);
        }
        setDefaultTimeout();
    }

    public void findElementByTextAndClickMultiple(String textInElement, String otherTextInElement, String locator, boolean isDoubleClick) {
        long expiredTimeout = System.currentTimeMillis() + 5000;
        setLimitedTimeout(1);
        while (System.currentTimeMillis() <= expiredTimeout) {
            List<WebElement> buttons = driver.findElements(By.cssSelector(locator));
            try {
                for (WebElement currentElement : buttons) {
                    if (currentElement.getText().contains(textInElement) || currentElement.getText().contains(otherTextInElement)) {
                        if (isDoubleClick) {
                            doubleClickOnElement(currentElement);
                        } else {
                            currentElement.click();
                        }
                        setDefaultTimeout();
                        sleep(1);
                        return;
                    }
                }
            } catch (StaleElementReferenceException e) {
                findElementByTextAndClick(textInElement, locator, isDoubleClick);
            }
            buttons.clear();
            sleepMS(100);
        }
        setDefaultTimeout();
    }

    public WebElement findElement(String locator) {
        setLimitedTimeout(1);
        WebElement element;
        try {
            element = driver.findElement(By.cssSelector(locator));
        } catch (ElementNotVisibleException | ElementNotFoundException e) {
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
            elements = driver.findElements(By.cssSelector(locator));
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
            sleep(clickDelay);
        } else {
            driver.findElements(By.cssSelector(locator)).get(0).click();
            sleepMS(clickDelay);
        }
        setDefaultTimeout();
    }

    public int getNumberOfElements(String locator, int timeout) {
        int numberOfElements = 0;
        setLimitedTimeout(timeout);
        try {
            numberOfElements = driver.findElements(By.cssSelector(locator)).size();
        } catch (NoSuchElementException e) {
            LOG_STEP.info("Given element(s) was not displayed");
        }
        setDefaultTimeout();
        return numberOfElements;
    }

    public String getRandomText() {
        return RandomStringUtils.random(10, true, false);
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
                sleepMS(500);
            }
        }
        setDefaultTimeout();
        return false;
    }

    public boolean isProcessExists(String processName) {
        String line;
        String pidInfo ="";
        try {
            Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
            BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                pidInfo+=line;
            }
            if(pidInfo.contains(processName)) {
                return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    public void jsExecute(String input) {
        ((JavascriptExecutor) driver).executeScript(input);
    }

    public void jsExecute(String script, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(script, element);
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
        setLimitedTimeout(1);
        try {
            elementInSec.click();
        } finally {
            setDefaultTimeout();
        }
        setDefaultTimeout();
        sleep(1);
    }

    public void lazyClick(WebElement elementInSec, int timeoutInSec) {
        setLimitedTimeout(1);
        try {
            elementInSec.click();
        } finally {
            setDefaultTimeout();
        }
        setDefaultTimeout();
        sleep(timeoutInSec);
    }

    public void pressButton(String button, boolean longWait) {
        actions.sendKeys(button).build().perform();
        if (longWait) {
            sleep(clickDelay);
        } else {
            sleepMS(500);
        }
    }

    public void pressKey(Keys button, boolean longWait) {
        actions.sendKeys(button).build().perform();
        if (longWait) {
            sleep(clickDelay);
        } else {
            sleepMS(500);
        }
    }

    public void pressChord(Keys firstKey, String secondKey, boolean longWait) {
        actions.sendKeys(Keys.chord(firstKey, secondKey)).build().perform();
        sleepMS(500);
        if (longWait) {
            sleep(clickDelay);
        } else {
            sleepMS(500);
        }
    }

    public void repeatedClick(String locator) {
        safeClick(findElement(locator), 3);
        sleep(2);
        if (isElementDisplayed(locator, 3)) {
            safeClick(findElement(locator), 3);
            sleep(1);
            if (isElementDisplayed(locator, 3)) {
                actions.moveToElement(findElement(locator)).click().build().perform();
            }
        }
    }

    public void sendButtonsToElement(WebElement element, String buttons, boolean longWait) {
        actions.sendKeys(element).sendKeys(buttons).build().perform();
        if (longWait) {
            sleep(clickDelay);
        } else {
            sleepMS(500);
        }
    }

    public void sendKeysToElement(WebElement element, Keys key, boolean longWait) {
        actions.sendKeys(element).sendKeys(key).build().perform();
        if (longWait) {
            sleep(clickDelay);
        } else {
            sleepMS(500);
        }
    }

    public void refreshPage() {
        driver.navigate().refresh();
        acceptAlert();
        sleepMS(300);
    }

    public void safeClick(WebElement element, int timeout) {
        safeClick(element, timeout, 0);
    }

    public void safeClick(WebElement element, int timeout, int delay) {
        setLimitedTimeout(0);
        try {
            doubleClickOnElement(element);
            counter = 0;
        } catch (WebDriverException e) {
            if (counter < timeout) {
                counter++;
                sleep(1);
                safeClick(element, timeout, delay);
            }
        }
        sleep(delay);
        setDefaultTimeout();
    }

    public void takeScreenshot() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("HH.mm");
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            String path = "target/screenshots/onDemand/" + "(Time)_" + formatterTime.format(calendar.getTime()) + "__(Date)_" +
                    formatterDate.format(calendar.getTime()) + "__(TestClass)_" +
                    getClass().getSimpleName() + ".png";
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    public String getClassName() {
        return Thread.currentThread().getStackTrace()[3].getClassName().replaceAll("[A-Za-z]{1,}\\.", "");
    }
}
