package framework.utils;

import features.BasePage;
import framework.*;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

public class WaitUtil {

    private static final int clickDelay = 1;
    private WebDriver driver = framework.WebDriver.getInstance().getDriver();

    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleepMS(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean waitForElementAggregate(String textToFind, String locator, int timeout) {
        BasePage.setLimitedTimeout(1);
        long expiredTimeout = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= expiredTimeout) {
            List<WebElement> webElements = driver.findElements(By.cssSelector(locator));
            try {
                for (WebElement currentText : webElements) {
                    if (currentText.getText().toLowerCase().contains(textToFind.toLowerCase())) {
                        BasePage.setDefaultTimeout();
                        sleep(clickDelay);
                        return true;
                    }
                }
            } catch (StaleElementReferenceException e) {
                webElements.clear();
                sleep(clickDelay);
                continue;
            }
            sleep(clickDelay);
            webElements.clear();
        }
        BasePage.setDefaultTimeout();
        return false;
    }

    public void waitingForElementDisplayed(final WebElement element, int maxSleep) {
		try {
			new WebDriverWait(driver, maxSleep).
					until(new ExpectedCondition<Boolean>() {
						@Override public Boolean apply(org.openqa.selenium.WebDriver webDriver) {
							return element.isDisplayed();
                    }
                });
        } catch (StaleElementReferenceException e) {
            waitingForElementDisplayed(locator, maxSleep);
        }
    }

    public void waitingForElementDisplayed(final String locator, int maxSleep) {
        try {
            new WebDriverWait(driver, maxSleep).
                    until(new ExpectedCondition<Boolean>() {
                        @Override public Boolean apply(org.openqa.selenium.WebDriver webDriver) {
                            return driver.findElement(By.cssSelector(locator)).isDisplayed();
                        }
                    });
        } catch (StaleElementReferenceException e) {
            waitingForElementDisplayed(locator, maxSleep);
        }
    }

    public void waitingForElementDisplayedCond(final boolean element, int maxSleep) {
        new WebDriverWait(driver, maxSleep).
                until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(org.openqa.selenium.WebDriver webDriver) {
                        return element;
                    }
                });
    }
	
	public void waitingForElementsDisplayed(final List<WebElement> elements, int maxSleep) {
        new WebDriverWait(driver, maxSleep).
                until(ExpectedConditions.visibilityOfAllElements(elements));
    }
	
	 public boolean waitingForElementNotDisplayed(String locator, int timeout) {
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
    }

    public void waitForFrameToBeAvailableAndSwitchToIt(WebElement frameElement, int timeout) {
        new WebDriverWait(driver, timeout).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
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