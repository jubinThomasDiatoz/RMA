package com.pages;

import com.utility.EmailUtility;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.time.Duration;
import org.openqa.selenium.chrome.ChromeOptions;

public class LoginSmokeTest {

	WebDriver driver;

	String baseUrl = "https://solu-m.flowworktest.ai/return-management";

	@BeforeMethod
	public void setup() {

		System.out.println("\n====================================");

		System.out.println("STARTING PRODUCTION SMOKE TEST");

		System.out.println("====================================\n");

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--headless=new");
		options.addArguments("--disable-gpu");
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");

		driver = new ChromeDriver(options);

		driver.manage().window().maximize();

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

		System.out.println("[INFO] Browser Started Successfully\n");
	}

	@Test
	public void smokeLoginTest() throws Exception {

		int statusCode = 0;

		String responseMessage = "";

		String frontendReason = "";

		String backendReason = "";

		try {

			/*
			 * ==================================== BACKEND STATUS CHECK
			 * ====================================
			 */

			System.out.println("[INFO] Checking Backend Status...");

			URL url = new URL(baseUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			connection.setConnectTimeout(15000);

			connection.setReadTimeout(15000);

			connection.connect();

			statusCode = connection.getResponseCode();

			responseMessage = connection.getResponseMessage();

			System.out.println("[INFO] Backend Status Code : " + statusCode);

			System.out.println("[INFO] Backend Response Message : " + responseMessage + "\n");

			/*
			 * ==================================== BACKEND FAILURE
			 * ====================================
			 */

			if (statusCode != 200 && statusCode != 204) {

				backendReason = "BACKEND FAILURE";

				StringBuilder errorResponse = new StringBuilder();

				try {

					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

					String line;

					while ((line = reader.readLine()) != null) {

						errorResponse.append(line).append("\n");
					}

					reader.close();

				} catch (Exception ex) {

					errorResponse.append("Unable To Read Error Response");
				}

				String finalErrorMessage =

						"PRODUCTION IS DOWN\n\n"

								+ "ERROR TYPE : " + backendReason + "\n\n"

								+ "STATUS CODE : " + statusCode + "\n\n"

								+ "RESPONSE MESSAGE : " + responseMessage + "\n\n"

								+ "ERROR RESPONSE : \n" + errorResponse;

				System.out.println("====================================");

				System.out.println("[ERROR] BACKEND FAILURE DETECTED");

				System.out.println(finalErrorMessage);

				System.out.println("====================================");

				EmailUtility.sendFailureMail(finalErrorMessage);

				Assert.fail(finalErrorMessage);
			}

			/*
			 * ==================================== FRONTEND CHECK
			 * ====================================
			 */

			System.out.println("[INFO] Opening Frontend Application...");

			driver.get(baseUrl);

			Thread.sleep(5000);

			String currentUrl = driver.getCurrentUrl();

			String pageSource = driver.getPageSource();

			System.out.println("[INFO] Current URL : " + currentUrl);

			/*
			 * ==================================== FRONTEND ERROR HANDLING
			 * ====================================
			 */

			if (pageSource.contains("Error 1000")) {

				frontendReason = "Cloudflare Error 1000";

			} else if (pageSource.contains("DNS points to prohibited IP")) {

				frontendReason = "DNS Points To Prohibited IP";

			} else if (pageSource.contains("403 Forbidden")) {

				frontendReason = "403 Forbidden";

			} else if (pageSource.contains("404 Not Found")) {

				frontendReason = "404 Not Found";

			} else if (pageSource.contains("500 Internal Server Error")) {

				frontendReason = "500 Internal Server Error";

			} else if (pageSource.contains("502 Bad Gateway")) {

				frontendReason = "502 Bad Gateway";

			} else if (pageSource.contains("503 Service Temporarily Unavailable")) {

				frontendReason = "503 Service Temporarily Unavailable";

			} else if (pageSource.contains("504 Gateway Time-out")) {

				frontendReason = "504 Gateway Timeout";

			} else if (pageSource.contains("ERR_CONNECTION")) {

				frontendReason = "Connection Error";

			} else if (pageSource.contains("ERR_NAME_NOT_RESOLVED")) {

				frontendReason = "DNS Resolution Failure";

			} else if (pageSource.contains("ERR_TIMED_OUT")) {

				frontendReason = "Connection Timed Out";

			} else if (pageSource.contains("ERR_SSL")) {

				frontendReason = "SSL Certificate Error";

			} else if (pageSource.contains("This site can’t be reached")) {

				frontendReason = "Website Not Reachable";
			}

			/*
			 * ==================================== FRONTEND FAILURE
			 * ====================================
			 */

			if (!frontendReason.isEmpty()) {

				String finalErrorMessage =

						"PRODUCTION IS DOWN\n\n"

								+ "ERROR TYPE : FRONTEND FAILURE\n\n"

								+ "FRONTEND ERROR : " + frontendReason + "\n\n"

								+ "BACKEND STATUS CODE : " + statusCode + "\n\n"

								+ "BACKEND RESPONSE : " + responseMessage + "\n\n"

								+ "CURRENT URL : " + currentUrl;

				System.out.println("====================================");

				System.out.println("[ERROR] FRONTEND FAILURE DETECTED");

				System.out.println(finalErrorMessage);

				System.out.println("====================================");

				EmailUtility.sendFailureMail(finalErrorMessage);

				Assert.fail(finalErrorMessage);
			}

			/*
			 * ==================================== LOGIN FLOW
			 * ====================================
			 */

			System.out.println("[INFO] Entering Username...");

			WebElement email = driver.findElement(By.xpath("//input[@id='username']"));

			email.clear();

			email.sendKeys("mohanaqualityengineer@gmail.com");

			System.out.println("[INFO] Entering Password...");

			WebElement password = driver.findElement(By.xpath("//input[@id='password']"));

			password.clear();

			password.sendKeys("Diatoz@123");

			System.out.println("[INFO] Clicking Sign In Button...");

			WebElement signIn = driver.findElement(By.xpath("//input[@value='Sign In']"));

			signIn.click();

			Thread.sleep(8000);

			/*
			 * ==================================== LOGIN VALIDATION
			 * ====================================
			 */

			String afterLoginUrl = driver.getCurrentUrl();

			System.out.println("[INFO] URL After Login : " + afterLoginUrl);

			if (afterLoginUrl.contains("login")) {

				String finalErrorMessage =

						"PRODUCTION IS DOWN\n\n"

								+ "ERROR TYPE : LOGIN FAILURE\n\n"

								+ "STATUS CODE : " + statusCode + "\n\n"

								+ "RESPONSE MESSAGE : " + responseMessage + "\n\n"

								+ "LOGIN DID NOT COMPLETE";

				System.out.println("[ERROR] LOGIN FAILED");

				EmailUtility.sendFailureMail(finalErrorMessage);

				Assert.fail(finalErrorMessage);
			}

			/*
			 * ==================================== SUCCESS
			 * ====================================
			 */

			System.out.println("\n====================================");

			System.out.println("[SUCCESS] PRODUCTION IS UP");

			System.out.println("[SUCCESS] BACKEND STATUS : " + statusCode);

			System.out.println("[SUCCESS] RESPONSE MESSAGE : " + responseMessage);

			System.out.println("[SUCCESS] LOGIN SUCCESSFUL");

			System.out.println("[SUCCESS] SMOKE TEST PASSED");

			System.out.println("====================================\n");

			Assert.assertTrue(true);

		} catch (Exception e) {

			String finalErrorMessage =

					"PRODUCTION IS DOWN\n\n"

							+ "ERROR TYPE : UNKNOWN FAILURE\n\n"

							+ "STATUS CODE : " + statusCode + "\n\n"

							+ "REASON : " + e.getMessage();

			System.out.println("====================================");

			System.out.println("[ERROR] UNKNOWN FAILURE DETECTED");

			System.out.println(finalErrorMessage);

			System.out.println("====================================");

			EmailUtility.sendFailureMail(finalErrorMessage);

			Assert.fail(finalErrorMessage);
		}
	}

	@AfterMethod
	public void teardown() {

		try {

			if (driver != null) {

				driver.quit();

				System.out.println("\n[INFO] Browser Closed Successfully");
			}

		} catch (Exception e) {

			System.out.println("[ERROR] Error While Closing Browser");
		}

		System.out.println("\n====================================");

		System.out.println("SMOKE TEST COMPLETED");

		System.out.println("====================================\n");
	}
}