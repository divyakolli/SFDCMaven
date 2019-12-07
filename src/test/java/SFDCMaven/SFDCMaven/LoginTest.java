package SFDCMaven.SFDCMaven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class LoginTest{
	public static WebDriver driver;
	static WebElement RememberMe;
	static WebElement usermenuDropdown;
	public static ExtentTest logger;
	static WebElement error;
	public static ExtentReports reports;
	public static WebElement username;
	public static WebElement password;
	public static WebElement Login;
	public static WebElement dropdown;

	@BeforeMethod
	public static void setup() {
		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		String url = "https://login.salesforce.com/";
		driver.get(url);
	}

	@BeforeTest
	public static void Extent_Reports() {
		reports = new ExtentReports("C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\ExtentReports\\SFDC_Login.html", true);		
	}

	@AfterTest
	public static void Extent_Reports_teardown() {
		reports.flush();
	}

	@AfterMethod
	public static void teardown() {
		driver.quit();
	}

	//Reading data from Excel
	public static String[][] readExcel(String ExcelPath, String sheetName) throws IOException{
		//Creating reference to open xls file
		File file = new File(ExcelPath);

		//FileInputStream class to read excel file
		FileInputStream fis = new FileInputStream(file);

		//workbook
		HSSFWorkbook wb = new HSSFWorkbook(fis);

		//Read sheet inside the workbook by its name
		HSSFSheet sheet = wb.getSheet(sheetName);

		//Number of rows and columns count in excel
		int rowCount = sheet.getLastRowNum()+1;
		//System.out.println("Total number of rows are : " + rowCount);
		int columnCount = sheet.getRow(0).getLastCellNum();
		//System.out.println("Total number of columns are : " + columnCount);

		String[][] Testdata = new String[rowCount][columnCount];

		for(int i=0; i<rowCount; i++) {
			for(int j=0; j<sheet.getRow(i).getLastCellNum(); j++) {
				Testdata[i][j] = sheet.getRow(i).getCell(j).getStringCellValue();
				//System.out.println(i + "is" + j + "is" + Testdata[i][j]);
			}
		}
		return Testdata;
	}

	public static void enter_data_textbox(WebElement textbox,String inputData, String textbox_name)
	{
		if (textbox.isDisplayed()== true)
		{
			if (textbox.isEnabled() == true)
			{
				textbox.sendKeys(inputData);

				if(textbox.getAttribute("value").equals(inputData))
				{
					logger.log(LogStatus.INFO,"'"+inputData+ "' was entered in '"+textbox_name+ "' textbox ");
					System.out.println("'"+inputData+ "' was entered in '"+textbox_name+ "' textbox ");
				}
				else
				{
					logger.log(LogStatus.FAIL,"'"+inputData+ "' was not entered in '"+textbox_name+ "' textbox ") ;
					System.out.println("'"+inputData+ "' was not entered in '"+textbox_name+ "' textbox ");
				}
			}
			else
			{
				logger.log(LogStatus.FAIL,textbox_name + " button was not enabled");
				System.out.println(textbox_name + " textbox was not enabled");
			}
		}
		else
		{
			logger.log(LogStatus.FAIL,textbox_name + " button was not displayed");
			System.out.println(textbox_name + " textbox was not displayed");
		}
	}

	public static void actual_expected_result(String actual, String expected, String data) {
		if(actual.equals(expected) || actual.contains(expected)) {
			logger.log(LogStatus.PASS, "Actual and expected " + data +"s are same");
		}
		else
			logger.log(LogStatus.FAIL, "Actual and expected " + data +"s are not same");
	}

	public static void click_element(WebElement element, String element_name) {
		if(element.isDisplayed()==true) {
			if(element.isEnabled()==true) {
				element.click();
				logger.log(LogStatus.INFO, "Clicked on " + element_name);
			}
			else
				logger.log(LogStatus.FAIL, element_name + " checkbox is not enabled");
		}
		else
			logger.log(LogStatus.FAIL, element_name + " checkbox is not displayed");
	}


	@Test(priority=1)
	public void NoPassword_Error_Message() throws InterruptedException, Exception {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase1.xls";

		//Getting data from Excel
		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("NoPassword_Error_Message");

		//Validating Username
		username = driver.findElement(By.xpath("//input[@id='username']"));
		enter_data_textbox(username, data[1][0], "username");

		//password
		password = driver.findElement(By.xpath("//input[@id='password']"));
		password.clear();

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		error = driver.findElement(By.id("error"));
		String actualError = error.getText();
		String expectedError = data[1][2];
		actual_expected_result(actualError, expectedError, "Error message");
	}

	@Test(priority=2)
	public void Login_Home_Page() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase2.xls";

		//Getting data from Excel
		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Login_Home_Page");

		//Validating Username
		username = driver.findElement(By.xpath("//input[@id='username']"));
		enter_data_textbox(username, data[1][0], "username");

		//password
		password = driver.findElement(By.xpath("//input[@id='password']"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Verifying if correct home page is opened or not
		WebElement actualText = driver.findElement(By.xpath("//a[contains(text(),'Venkata Phani Divya Abcd')]"));
		String actualHomePage_Text = actualText.getText();
		String expected_Text = data[1][2];
		actual_expected_result(actualHomePage_Text, expected_Text, "user name");
	}

	@Test(priority=3)
	public void Remember_Me_Checkbox() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase3.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Remember_Me_Checkbox");

		//Validating Username
		username = driver.findElement(By.xpath("//input[@id='username']"));
		enter_data_textbox(username, data[1][0], "username");

		//password
		password = driver.findElement(By.xpath("//input[@id='password']"));
		enter_data_textbox(password, data[1][1], "password");

		//Checking Rememberme checkbox
		RememberMe = driver.findElement(By.id("rememberUn"));
		click_element(RememberMe, "Remember me check box");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Verifying if correct home page is opened or not
		WebElement actualText = driver.findElement(By.xpath("//a[contains(text(),'Venkata Phani Divya Abcd')]"));
		String actualHomePage_Text = actualText.getText();
		String expected_Text = data[1][2];
		actual_expected_result(actualHomePage_Text, expected_Text, "user name");

		//usermenu dropdown
		usermenuDropdown = driver.findElement(By.xpath("//*[@id=\"userNavLabel\"]"));
		click_element(usermenuDropdown, "Usermenu dropdown");

		//Logout
		WebElement Logout = driver.findElement(By.xpath("//*[@id=\"userNav-menuItems\"]/a[5]"));
		click_element(Logout, "Logout");

		username = driver.findElement(By.xpath("//span[@id='idcard-identity']"));
		String actual_username = username.getText();
		String expected_username = data[1][3];
		actual_expected_result(actual_username, expected_username, "User email addresses");

		RememberMe = driver.findElement(By.id("rememberUn"));
		if(RememberMe.isSelected()) {
			logger.log(LogStatus.PASS, "RememberMe check box is checked..");
		}
		else
			logger.log(LogStatus.FAIL, "RememberMe check box is not checked..");
	}

	@Test(priority=4)
	public void Forgot_Password() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase4A.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Forgot_Password");

		//Forgot password
		WebElement ForgotPassword = driver.findElement(By.id("forgot_password_link"));
		click_element(ForgotPassword, "Forgot Password");

		//Validating url
		String actualUrl = driver.getCurrentUrl();
		String expectedUrl = "https://login.salesforce.com/secur/forgotpassword.jsp?locale=us";
		actual_expected_result(actualUrl, expectedUrl, "Url's");

		//Entering username in Forgot Your Password page
		username = driver.findElement(By.xpath("//input[@id='un']"));
		enter_data_textbox(username, data[1][0], "username");

		WebElement Continue = driver.findElement(By.name("continue"));
		click_element(Continue, "Continue button");

		WebElement resetText = driver.findElement(By.xpath("//*[@id=\"forgotPassForm\"]/div/p[1]"));
		String actualText = resetText.getText();
		String expectedText= data[1][2];
		actual_expected_result(actualText, expectedText, "Reset password text");
	}

	@Test(priority=4)
	public void Login_Error_Message() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase4B.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Login_Error_Message");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Validating Error
		error = driver.findElement(By.id("error"));
		String actualError = error.getText();
		System.out.println(actualError);
		String expectedError = data[1][2];
		actual_expected_result(actualError, expectedError, "Error message");
	}
}

