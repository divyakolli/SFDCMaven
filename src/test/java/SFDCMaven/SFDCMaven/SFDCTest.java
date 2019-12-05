package SFDCMaven.SFDCMaven;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class SFDCTest{
	public static WebDriver driver;
	static WebElement RememberMe;
	static WebElement usermenuDropdown;
	public static ExtentTest logger;
	static WebElement error;
	static String downloadPath = "C:\\Users\\divya\\Downloads";
	public static ExtentReports reports;
	public static WebElement username;
	public static WebElement password;
	public static WebElement Login;
	public static WebElement dropdown;
	DateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
	Calendar cal = Calendar.getInstance();

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
		reports = new ExtentReports("C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\ExtentReports\\SFDCTestcaseReport.html", true);		
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
		int rowCount = sheet.getLastRowNum() + 1;
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
	public void TC01_NoPassword_Error_Message() throws InterruptedException, Exception {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase1.xls";
		
		//Getting data from Excel
		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC01_NoPassword_Error_Message");

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
	public void TC02_Login_Home_Page() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase2.xls";
		
		//Getting data from Excel
		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC02_Login_Home_Page");

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
	public void TC03_Remember_Me_Checkbox() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase3.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC03_Remember_Me_Checkbox");

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
	public void TC4A_Forgot_Password() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase4A.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC4A_Forgot_Password");

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
	public void TC4B_Login_Error_Message() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase4B.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC4B_Login_Error_Message");

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

	@Test(priority=5)
	public void TC5_Usermenu_dropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase5.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC5_Usermenu_dropdown");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
				
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
				
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		WebElement actualText = driver.findElement(By.xpath("//a[contains(text(),'Venkata Phani Divya Abcd')]"));
		String actualHomePage_Text = actualText.getText();
		String expected_Text = data[1][2];
		actual_expected_result(actualHomePage_Text, expected_Text, "Home Page username");

		UserMenudropdown();
	}

	public static void UserMenudropdown() throws InterruptedException {
		//Clicking on Usermenu dropdown
		dropdown = driver.findElement(By.xpath("//div[@id='userNavButton']"));
		click_element(dropdown, "Usermenu dropdown");
		
		//Validating dropdown options
		String[] dropdownExpected = {"My Profile", "My Settings", "Developer Console", "Switch to Lightning Experience", "Logout"};
		List<WebElement> dropdown_list= driver.findElements(By.xpath("//div[@id='userNavMenu']//a"));
		int count = 0;
		for(int i=0; i<dropdownExpected.length; i++) {	
			if(dropdownExpected[i].equals(dropdown_list.get(i).getText())) {
				count++;
			}
			else
				logger.log(LogStatus.FAIL, "Test Failed..");
		}
		if(count==5) {
			logger.log(LogStatus.PASS, "All the elements from the usermenu dropdown are getting displayed");
		}
		else
			logger.log(LogStatus.FAIL, "The elements from the usermenu dropdown are not getting displayed");
	}

/*	@Test(priority=6)
	public void TC6_Select_MyProfile_usermenuDropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase6.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC6_Select_MyProfile_usermenuDropdown");
		
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
				
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
				
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");
		
		Thread.sleep(5000);
		
		UserMenudropdown();

		Thread.sleep(5000);
		
		//Opening MyProfile Tab
		WebElement MyProfile = driver.findElement(By.xpath("//a[contains(text(),'My Profile')]"));
		click_element(MyProfile, "MyProfile from usermenu dropdown");
		
		WebElement rightDrowdown = driver.findElement(By.xpath("//a[@id='moderatorMutton']"));
		click_element(rightDrowdown, "right side arrow Drowdown");
		
		//Clicking on Edit Profile
		WebElement EditProfile = driver.findElement(By.xpath("//a[contains(text(),'Edit Profile')]"));
		click_element(EditProfile, "Edit Profile");

		//Switching to Iframe
		WebElement IFrame = driver.findElement(By.id("aboutMeContentId"));
		driver.switchTo().frame(IFrame);
		logger.log(LogStatus.INFO, "Switched to Iframe");

		//Lastname in the About Tab
		WebElement LastName = driver.findElement(By.xpath("//input[@id='lastName']"));
		LastName.clear();
		enter_data_textbox(LastName, data[1][3], "LastName Tab");

		WebElement SaveAll = driver.findElement(By.xpath("//input[@class='zen-btn zen-primaryBtn zen-pas']"));
		click_element(SaveAll, "Save All button");

		//Verifying if the updated lastname is getting displayed or not
		WebElement expected = driver.findElement(By.xpath("//span[@id='tailBreadcrumbNode']"));
		String actualText= expected.getText();
		String expectedText = data[1][3];
		actual_expected_result(actualText, expectedText, "Lastname");

		Thread.sleep(5000);
		
		//Post link
		WebElement Post_link = driver.findElement(By.xpath("//*[@id=\"publisherAttachTextPost\"]/span[1]"));
		click_element(Post_link, "Post link button");
		
		//Post Iframe
		WebElement Post_Iframe = driver.findElement(By.xpath("//*[@id=\"cke_39_contents\"]/iframe"));
		logger.log(LogStatus.INFO, "Switched to Post link Iframe");
		driver.switchTo().frame(Post_Iframe);
		WebElement Post_body = driver.findElement(By.xpath("/html/body"));
		click_element(Post_body, "Post body button");
		Post_body.sendKeys(data[1][4]);
		//enter_data_textbox(Post_body, data[1][4], "Post tab");
		
		//Switching back to the default window
		driver.switchTo().defaultContent();

		//Share button - Post link
		WebElement Share_Button = driver.findElement(By.xpath("//input[@id='publishersharebutton']"));
		click_element(Share_Button, "Share Button button");

		//File link
		WebElement File_link = driver.findElement(By.xpath("//span[contains(@class,'publisherattachtext')][contains(text(),'File')]"));
		click_element(File_link, "File link button");

		//Uploading File from Computer
		WebElement Upload_File = driver.findElement(By.xpath("//a[@id='chatterUploadFileAction']"));
		click_element(Upload_File, "Upload File button");

		WebElement Choose_File = driver.findElement(By.xpath("//input[@id='chatterFile']"));
		Choose_File.sendKeys(data[1][5]);
		//enter_data_textbox(Choose_File, data[1][5], "choose file");

		//Share button - File link
		Share_Button = driver.findElement(By.xpath("//*[@id=\"publishersharebutton\"]"));
		click_element(Share_Button, "Share Button button");
		
		//Uploading photo, Mouse Hover
		WebElement moderator = driver.findElement(By.xpath("//span[@class='profileImage chatter-avatarFull chatter-avatar']//img[@class='chatter-photo']"));
		System.out.println("Hi");
		Actions action = new Actions(driver);
		action.moveToElement(moderator).build().perform();

		//Upload Photo
		WebElement Upload_Photo = driver.findElement(By.xpath("//*[@id=\"uploadLink\"]"));
		click_element(Upload_Photo, "Upload Photo button");

		Thread.sleep(5000);
		
		//Upload photo link
		WebElement Photo_link = driver.findElement(By.xpath("//iframe[@id=\"uploadPhotoContentId\"]"));
		driver.switchTo().frame(Photo_link);

		Thread.sleep(5000);
		
		//Upload photo link
		WebElement Choose_File_Photo = driver.findElement(By.xpath("//*[@id=\"j_id0:uploadFileForm:uploadInputFile\"]"));
		Choose_File_Photo.sendKeys("C:\\PIC.jpg");
		//Choose_File_Photo.sendKeys(data[1][6]);
		
		Thread.sleep(5000);
		
		//Save button
		WebElement Save_button = driver.findElement(By.className("btn saveButton"));
		Save_button.click();
		logger.log(LogStatus.INFO, "Clicked on save button");
		
		Thread.sleep(10000);
	}
*/
/*	@Test(priority=7)
	public void TC7_MySettings_Usemenu_Dropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase7.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC7_MySettings_Usemenu_Dropdown");
		
		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
				
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
				
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");
		
		UserMenudropdown();
		
		//MySettings
		WebElement MySettings = driver.findElement(By.xpath("//a[contains(text(),'My Settings')]"));
		click_element(MySettings, "MySettings");
		
		//Personal and Login History
		WebElement Personal = driver.findElement(By.xpath("//*[@id=\"PersonalInfo_font\"]"));
		click_element(Personal, "Personal");
		WebElement LoginHistory = driver.findElement(By.xpath("//*[@id=\"LoginHistory_font\"]"));
		click_element(LoginHistory, "Login History");

		//downloading .csv file
		WebElement LoginHistory6Months= driver.findElement(By.xpath("//*[@id=\"RelatedUserLoginHistoryList_body\"]/div/a"));
		click_element(LoginHistory6Months, "Login History 6 Months");
		File getLatestFile = getLatestFilefromDir(downloadPath);
		String fileName = getLatestFile.getName();
		if(isFileDownloaded_Extension(downloadPath, ".csv") == true) {
			logger.log(LogStatus.PASS, "downloaded file is in .csv format");
		}
		else
			logger.log(LogStatus.FAIL, "downloaded file is not in .csv format");

		//Diaply and Layout
		WebElement DisplayandLayout = driver.findElement(By.xpath("//*[@id=\"DisplayAndLayout_font\"]"));
		click_element(DisplayandLayout, "Display and Layout");
		WebElement CustomizeMyTabs = driver.findElement(By.xpath("//*[@id=\"CustomizeTabs_font\"]"));
		click_element(CustomizeMyTabs, "Customize My Tabs");

		//Custom App dropdown to select salesforce Chatter
		dropdown = driver.findElement(By.id("p4"));
		Select dropdownOptions = new Select(dropdown); 
		dropdownOptions.selectByVisibleText(data[1][2]);

		//Available Tabs dropdown
		dropdown = driver.findElement(By.id("duel_select_0"));
		dropdownOptions = new Select(dropdown); 
		dropdownOptions.selectByVisibleText(data[1][3]);
		WebElement Add_Arrow = driver.findElement(By.xpath("//*[@id=\"duel_select_0_right\"]/img"));
		click_element(Add_Arrow, "Add Arrow");

		//checking if the selected element is in Selected Tabs dropdown
		dropdown = driver.findElement(By.id("duel_select_1"));
		dropdownOptions = new Select(dropdown); 
		List<WebElement> elements = dropdownOptions.getOptions();
		for(int i=0; i<elements.size(); i++) {
			if(elements.get(i).getText().equals(data[1][3])) {
				logger.log(LogStatus.PASS, "Test Passed..");	
			}
		}

		//Email Tab
		WebElement Email = driver.findElement(By.id("EmailSetup"));
		click_element(Email, "Email");
		WebElement MyEmailSettings = driver.findElement(By.id("EmailSettings_font"));
		click_element(MyEmailSettings, "My Email Settings");
		WebElement Email_Name = driver.findElement(By.id("sender_name"));
		Email_Name.clear();
		enter_data_textbox(Email_Name, data[1][4], "Email Name");
		WebElement Email_Address = driver.findElement(By.id("sender_name"));
		Email_Address.clear();
		enter_data_textbox(Email_Address, data[1][5], "Email Address");
		WebElement Bcc_radioButton = driver.findElement(By.id("auto_bcc1"));
		click_element(Bcc_radioButton, "Bcc radio button");
		WebElement Save_button = driver.findElement(By.name("save"));
		click_element(Save_button, "Save button");	

		//Calender & Remainders
		WebElement Calender_and_Reminders = driver.findElement(By.id("CalendarAndReminders"));
		click_element(Calender_and_Reminders, "Calender and Reminders");	
		WebElement Activity_Reminders = driver.findElement(By.xpath("//*[@id=\"Reminders_font\"]"));
		click_element(Activity_Reminders, "Activity Reminders");
		//Open a Test Remainder
		WebElement Test_Reminder = driver.findElement(By.className("btn"));
		click_element(Test_Reminder, "Test Reminder");

		String parentWindow = driver.getWindowHandle();
		for(String handle : driver.getWindowHandles()) {
			if(handle != parentWindow) {
				driver.switchTo().window(handle);
				String actual_url = driver.getCurrentUrl();
				//System.out.println(actual_url);
				String expected_url = data[1][6];
				actual_expected_result(actual_url, expected_url, "Open Remainder popup box url");
				driver.close();
			}
		}
	}

	//This method is used to get the latest downloaded file from directory
	public File getLatestFilefromDir(String dirPath) {
		File dir = new File(downloadPath);
		File[] files = dir.listFiles();
		if(files == null || files.length==0) {
			return null;
		}
		File lastModifiedFile = files[0];
	    for (int i = 1; i < files.length; i++) {
	       if (lastModifiedFile.lastModified() < files[i].lastModified()) {
	           lastModifiedFile = files[i];
	       }
	    }
	    return lastModifiedFile;
	}

	//This method checks the extension of the file downloaded
	public boolean isFileDownloaded_Extension(String dirPath, String extension) {
		boolean flag = false;
		File dir = new File(downloadPath);
		File[] files = dir.listFiles();
		if(files == null || files.length==0) {
			flag = false;
		}
		for(int i=1; i<files.length; i++) {
			if(files[i].getName().contains(extension)) {
				flag = true;
			}
		}
		return flag;
	}

	//This method is to verify if the file is downloaded or not
	/*	public boolean isFileDownloaded(String downloadPath, String fileName) {
			boolean flag = false;
			File dir = new File(downloadPath);
			File[] dir_contents = dir.listFiles();

				for(int i=0; i<dir_contents.length; i++) {
					if(dir_contents[i].getName().equalsIgnoreCase(fileName)) {
					return flag = true;
				}
			}
			return flag;
		}
	 */

/*	@Test(priority=8)
	public void TC8_Developer_Console() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase8.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC8_Developer_Console");
		
		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
				
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
				
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");
		
		UserMenudropdown();
		
		String primaryWindow = driver.getWindowHandle();
		WebElement DeveloperConsole = driver.findElement(By.xpath("//a[@class='debugLogLink menuButtonMenuLink']"));
		click_element(DeveloperConsole, "Developer Console from usermenu dropdown");
		
		for(String handle : driver.getWindowHandles()) {
			if(!handle.equals(primaryWindow)) {
				driver.switchTo().window(handle);
				Thread.sleep(5000);
				String actualUrl = driver.getCurrentUrl();
				String expectedUrl = data[1][2];
				actual_expected_result(actualUrl, expectedUrl, "Url");
				driver.close();
			}
		}
	}

	@Test(priority=9)
	public void TC9_Logout_UsermenuDropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase9.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC9_Logout_UsermenuDropdown");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");
		
		UserMenudropdown();
		
		//Logout
		WebElement Logout = driver.findElement(By.xpath("//a[contains(text(),'Logout')]"));
		click_element(Logout, "Logout");

		//Verify Login Page
		String actualUrl = driver.getCurrentUrl();
		String expectedUrl = data[1][2];
		actual_expected_result(actualUrl, expectedUrl, "Url");
	}

	@Test(priority=10)
	public void TC10_Create_Account() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase10.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC10_Create_Account");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Accounts page
		WebElement Accounts_Tab = driver.findElement(By.xpath("//li[@id='Account_Tab']"));
		click_element(Accounts_Tab, "Accounts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Creating New Account
		WebElement New = driver.findElement(By.xpath("//input[@name='new']"));
		click_element(New, "New button");
	
		//Entering account details
		WebElement AccountName = driver.findElement(By.id("acc2"));
		AccountName.clear();
		enter_data_textbox(AccountName, data[1][3], "account name");

		WebElement Save = driver.findElement(By.name("save"));
		click_element(Save, "Save button");

		//Validating correct new account page is opened or not
		WebElement New_Account_Page = driver.findElement(By.className("topName"));
		String actual_accountName = New_Account_Page.getText();
		String expected_accountName = data[1][3];
		actual_expected_result(actual_accountName, expected_accountName, "created account name");
	}

	@Test(priority=11)
	public void TC11_Create_New_View() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase11.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC11_Create_New_View");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");
		
		//Opening Accounts page
		WebElement Accounts_Tab = driver.findElement(By.xpath("//li[@id='Account_Tab']"));
		click_element(Accounts_Tab, "Accounts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Creating New View
		WebElement CreateNewView = driver.findElement(By.xpath("//*[@id=\"filter_element\"]/div/span/span[2]/a[2]"));
		click_element(CreateNewView, "Create New View button");

		//Entering view name
		WebElement ViewName = driver.findElement(By.xpath("//*[@id=\"fname\"]"));
		ViewName.clear();
		sdf = new SimpleDateFormat("yyyy_MM_ddhh_mm_ss");
		Calendar cal = Calendar.getInstance();
		enter_data_textbox(ViewName, data[1][2]+ sdf.format(cal.getTime()), "View Name");
		
		//Entering view unique name
		WebElement ViewUniqueName = driver.findElement(By.xpath("//*[@id=\"devname\"]"));
		ViewUniqueName.clear();
		ViewUniqueName.sendKeys(data[1][3]+sdf.format(cal.getTime()));
		logger.log(LogStatus.INFO, "Entered view unique name");
		
		//This step is to verify if the newly added view is getting diaplyed in the list or not
		String value = driver.findElement(By.xpath("//*[@id=\"fname\"]")).getAttribute("value");
		System.out.println(value);
				
		//Save button
		WebElement Save = driver.findElement(By.xpath("//div[@class='pbBottomButtons']//input[@name='save']"));
		click_element(Save, "Save button");

		//validating if the newly added account name is getting displayed in the account view dropdown or not
		dropdown = driver.findElement(By.className("title"));
		click_element(dropdown, "dropdown");
		Select dropdown_options = new Select(dropdown);
		List<WebElement> options = dropdown_options.getOptions();
		boolean presented = false;//Using boolean only to check if the account is displayed or not
		for(int i=0; i<options.size(); i++) {
			//presented = false;
			if(options.get(i).getText().equals(value)) {
				presented = true;
				break;
			}
		}
		
		if(presented==true) {
			logger.log(LogStatus.PASS, "Account name is displayed in the dropdown");
		}
		else
			logger.log(LogStatus.FAIL, "Account name is not displayed in the dropdown");
	}

	@Test(priority=12)
	public void TC12_Edit_View() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase12.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC12_Edit_View");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Accounts page
		WebElement Accounts_Tab = driver.findElement(By.xpath("//li[@id='Account_Tab']"));
		click_element(Accounts_Tab, "Accounts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//clicking account name from the view list dropdown
		dropdown = driver.findElement(By.xpath("//select[@id='fcf']"));
		click_element(dropdown, "dropdown");
		Select dropdown_list = new Select(dropdown);
		dropdown_list.selectByVisibleText(data[1][2]);
		logger.log(LogStatus.INFO, "Selected account from the View dropdown");

		//Edit account
		WebElement Edit_button = driver.findElement(By.xpath("//div[@class='filterLinks']//a[contains(text(),'Edit')]"));
		click_element(Edit_button, "Edit button");

		//Entering new view name
		WebElement viewName = driver.findElement(By.xpath("//input[@id='fname']"));
		viewName.clear();
		enter_data_textbox(viewName, data[1][3], "View Name");

		//Selecting from Field dropdown
		dropdown = driver.findElement(By.id("fcol1"));
		dropdown.click();
		dropdown_list = new Select(dropdown);
		dropdown_list.selectByVisibleText(data[1][4]);
		logger.log(LogStatus.INFO, "Selected Account Name from the Field dropdown");

		//Operator dropdown
		dropdown = driver.findElement(By.id("fop1"));
		dropdown.click();
		dropdown_list = new Select(dropdown);
		dropdown_list.selectByVisibleText(data[1][5]);
		logger.log(LogStatus.INFO, "Selected contains from the Operator dropdown");

		//Value Tab
		WebElement value = driver.findElement(By.id("fval1"));
		value.clear();
		enter_data_textbox(value, data[1][6], "value");

		/*These statements verifies if the "Last Activity" is availble in the Available Fields
	      If Last Activity is available under Available Fields, It add the same to Selected Fields
	      and save the modifications
	    */		
/*		WebElement Available_Fields_dropdown = driver.findElement(By.xpath("//*[@id='colselector_select_0']"));
		Select Available_Fields_dropdown_list = new Select(Available_Fields_dropdown);
		List<WebElement> Available_Fields_dropdown_options = Available_Fields_dropdown_list.getOptions();

		boolean Element_available = false; //using as a reference to validate if the last activity is in available fields dropdown or not

		for(int i=0; i<Available_Fields_dropdown_options.size(); i++) {
			if(Available_Fields_dropdown_options.get(i).getText().equals("Last Activity")) {
				Element_available = true;
				WebElement Add_arrow = driver.findElement(By.xpath("//img[@class='rightArrowIcon']"));
				click_element(Add_arrow, "Add arrow");
				break;
			}
		}

		/* These statements verifies if the "Last Activity" is availble in the Selected Fields
	       If Last Activity is available under Selected Fields, save the modifications
	    */
/*		if(Element_available = false) {
			WebElement Selected_Fields_dropdown = driver.findElement(By.xpath("//select[@id='colselector_select_1']"));
			Select Selected_Fields_dropdown_list = new Select(Available_Fields_dropdown);
			List<WebElement> Selected_Fields_dropdown_options = Available_Fields_dropdown_list.getOptions();

			for(int i=0; i<Available_Fields_dropdown_options.size(); i++) {
				if(Selected_Fields_dropdown_options.get(i).getText().equals("Last Activity")) {
					break;
				}
			}	
		}

		//Clicking on save button
		WebElement Save_button = driver.findElement(By.name("save"));
		click_element(Save_button, "Save button");

		List<WebElement> NumberOf_Rows = driver.findElements(By.xpath("//div[@class='x-grid3-hd-inner x-grid3-hd-Name']/table/tbody/tr"));
		List<WebElement> NumberOf_Columns = driver.findElements(By.xpath("//div[@class='x-grid3-hd-inner x-grid3-hd-Name']/table/tbody/tr[4]/td"));
		
		//for printing Account Name column
		List<WebElement> Account_Name_Column = driver.findElements(By.xpath("//div[@class='x-grid3-hd-inner x-grid3-hd-Name']/table/tbody/tr/td[3]"));
		for(int i=0; i<Account_Name_Column.size(); i++) {
			Account_Name_Column.get(i).getText();
		}

		int count = 0;
		//This function checks if all the account names contains 'a' or not
		for(int i=0; i<Account_Name_Column.size(); i++) {
			if(Account_Name_Column.get(i).getText().contains("a")) {
				count++;
			}
		}
		if(count == Account_Name_Column.size()) {
			logger.log(LogStatus.PASS, "All the accounts reflected in the table contains 'a' letter");
		}
		else
			logger.log(LogStatus.FAIL, "Accounts reflected in the table does not contains 'a' letter");
}

	 
	@Test(priority=13)
	public void TC13_Accounts_MergeAccounts() throws IOException, InterruptedException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase13.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC13_Accounts_MergeAccounts");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Accounts page
		WebElement Accounts_Tab = driver.findElement(By.xpath("//li[@id='Account_Tab']"));
		click_element(Accounts_Tab, "Accounts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Merge Accounts
		WebElement Merge_Accounts = driver.findElement(By.xpath("//a[contains(text(),'Merge Accounts')]"));
		click_element(Merge_Accounts, "Merge Accounts");
		
		//Find Accounts and search box
		WebElement Search_box = driver.findElement(By.xpath("//input[@id='srch']"));
		enter_data_textbox(Search_box, data[1][2], "Search box");
		
		//Find Accounts button
		WebElement Find_Accounts_button = driver.findElement(By.xpath("//div[@class='pbWizardBody']//input[@name='srchbutton']"));
		click_element(Find_Accounts_button, "Find Accounts button");
		
		//Table for merging
		List<WebElement> rows = driver.findElements(By.xpath("//table[contains(@class,'list')]//tbody//tr"));
		System.out.println(rows.size());
		
		if((rows.size()) > 1) {
			WebElement first_Row = driver.findElement(By.xpath("//input[@id='cid0']"));
			click_element(first_Row, "First Row checkbox");
			WebElement second_Row = driver.findElement(By.xpath("//input[@id='cid1']"));
			click_element(second_Row, "second Row checkbox");
			
			//Next button
			WebElement next_button = driver.findElement(By.xpath("//div[contains(@class,'pbBottomButtons')]//input[contains(@name,'goNext')]"));
			click_element(next_button, "Next button");
			
			//Merge button
			WebElement merge_button = driver.findElement(By.xpath("//div[@class='pbTopButtons']//input[@name='save']"));
			click_element(merge_button, "Merge button");
			
			//accepting popup
			driver.switchTo().alert().accept();
			
			//Accounts Page Table data after merging: Rows and column
			rows = driver.findElements(By.xpath("//table[contains(@class,'list')]//tbody//tr"));
			System.out.println(rows.size());
			List<WebElement> NumberOf_Columns = driver.findElements(By.xpath("//table[contains(@class,'list')]//tbody//tr//th"));
			System.out.println(NumberOf_Columns.size());

			System.out.println("here");
			
			//for printing particular column
			List<WebElement> AccountName_Column = driver.findElements(By.xpath("//table[contains(@class,'list')]//tbody//tr//th//a"));
			System.out.println("AccountName column size is : " + AccountName_Column.size());
			for(int i=0; i<AccountName_Column.size(); i++) {
				AccountName_Column.get(i).getText();
				//System.out.println(AccountName_Column.get(i).getText());
			}
		
			String Search_AccountName = data[1][3];

			//Verifying if the created merged account is within the Recent Accounts Table 
			for(int i=0; i<AccountName_Column.size(); i++) {
				if((AccountName_Column.get(i).getText()).equals(Search_AccountName)) {
					logger.log(LogStatus.PASS, "Recently merged account is getting displyed in the Recent Accounts Table");
					break;
				}
			}
		}
		
	}

	
	@Test(priority=14)
	public void TC14_Create_account_report() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase14.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC14_Create_account_report");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Accounts page
		WebElement Accounts_Tab = driver.findElement(By.xpath("//li[@id='Account_Tab']"));
		click_element(Accounts_Tab, "Accounts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Clicking on accounts with last activity > 30 days
		WebElement Accounts_with_lastactivity_30days = driver.findElement(By.xpath("//a[contains(text(),'Accounts with last activity > 30 days')]"));
		click_element(Accounts_with_lastactivity_30days, "Accounts with lastactivity>30 days");

		//Verifying if the Unsaved Report page is opened or not
		WebElement expectedPage = driver.findElement(By.xpath("//h2[@class='pageDescription']"));
		String actualText= expectedPage.getText();
		String expectedText = data[1][2];
		actual_expected_result(actualText, expectedText, "Unsaved Report name");

		//Date Field dropdown
		WebElement Date_Field = driver.findElement(By.xpath("//input[@id='ext-gen20']"));
		click_element(Date_Field, "Date Field dropdown");
		
		Thread.sleep(5000);
		
		WebElement Created_Date = driver.findElement(By.xpath("//div[@class='x-combo-list-item x-combo-selected']"));
		click_element(Created_Date, "Created Date option from dropdown");

		//Selection From and To dates as Today's date
		WebElement From_date = driver.findElement(By.xpath("//img[@id='ext-gen152']"));
		click_element(From_date, "From date");
		WebElement From_Today_Date = driver.findElement(By.xpath("//table[@id='ext-comp-1112']//em"));
		click_element(From_Today_Date, "Today's Date from From Calender");

		WebElement To_date = driver.findElement(By.xpath("//img[@id='ext-gen154']"));
		click_element(To_date, "To date");
		WebElement To_Today_Date = driver.findElement(By.xpath("//table[@id='ext-comp-1114']//em"));
		click_element(To_Today_Date, "Today's Date from To Calender");

		//Saving the information
		WebElement Save_button = driver.findElement(By.xpath("//button[@id='ext-gen49']"));
		click_element(Save_button, "Save button");

		String primaryWindow = driver.getWindowHandle(); //Window handle of the primary window

		for(String handle : driver.getWindowHandles()) {
			if(handle != primaryWindow) {
				//switching to the child window
				driver.switchTo().window(handle);

				//Entering Report name
				WebElement Report_Name = driver.findElement(By.xpath("//input[@id='saveReportDlg_reportNameField']"));
				Report_Name.clear();
				enter_data_textbox(Report_Name, data[1][3], "Report Name");

				//Entering Report Uniquename
				WebElement Report_Unique_Name = driver.findElement(By.xpath("//input[@id='saveReportDlg_DeveloperName']"));
				Report_Unique_Name.clear();
				sdf = new SimpleDateFormat("yyyy_MM_ddhh_mm_ss");
				Calendar cal = Calendar.getInstance();
				Report_Unique_Name.sendKeys(data[1][4] + sdf.format(cal.getTime()));

				Thread.sleep(5000);
				
				WebElement Save_and_Run_Report = driver.findElement(By.xpath("//table[@id='dlgSaveAndRun']//em"));
				click_element(Save_and_Run_Report, "Save and Run report button");
			}

			Thread.sleep(5000);
			
			driver.switchTo().window(primaryWindow);
			
			//Verifying if the Newly created report page is opened or not
			WebElement expected = driver.findElement(By.xpath("//table[@class='reportTable tabularReportTable']/tbody/tr[2]"));
			actualText= expected.getText();
			System.out.println(actualText);
			expectedText = data[1][5];
			actual_expected_result(actualText, expectedText, "New created report namein the page");
		}
	}

	public static void OpportunitiesPage_dropdown() throws InterruptedException {

		String[] Opportunities = {"All Opportunities", "Closing Next Month", "Closing This Month", "My Opportunities", "New Last Week", "New This Week", "Opportunity Pipeline", "Private", "Recently Viewed Opportunities", "Won"};

		WebElement All_Opportunities_dropdown = driver.findElement(By.xpath("//select[@id='fcf']"));
		click_element(All_Opportunities_dropdown, "All Opportunities dropdown");
		Thread.sleep(5000);
		Select Opportunities_dropdown = new Select(All_Opportunities_dropdown);
		List<WebElement> Opportunities_dropdown_Elements = Opportunities_dropdown.getOptions();

		int Total_Element_Count=0;

		for(int i=0; i<Opportunities.length; i++) {
			if(Opportunities[i].equalsIgnoreCase(Opportunities_dropdown_Elements.get(i).getText())) {
				Total_Element_Count++;
			}
		}
		if(Total_Element_Count==10) {
			logger.log(LogStatus.PASS, "All the elements are available in the dropdown");
		}
		else
			logger.log(LogStatus.FAIL, "All the elements are not available in the dropdown");
	}
	
	@Test(priority=15)
	public void TC15_SelectUsermenu_Username_dropdown() throws IOException, InterruptedException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase15.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC15_SelectUsermenu_Username_dropdown");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Opportunities page
		WebElement Opportunities_Tab = driver.findElement(By.id("Opportunity_Tab"));
		click_element(Opportunities_Tab, "Opportunities");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
				
		WebElement Opportunities_Page = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actualOpportunities_Header = Opportunities_Page.getText();
		String expectedOpportunities_Header = data[1][2];
		actual_expected_result(actualOpportunities_Header, expectedOpportunities_Header, "Opportunity page name");
		
		Thread.sleep(5000);
		
		//Verifing all the elements are available in the dropdown or not
		OpportunitiesPage_dropdown();
	}

	@Test(priority=16)
	public void TC16_Create_newOpportunity() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase16.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC16_Create_newOpportunity");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Opportunities page
		WebElement Opportunities_Tab = driver.findElement(By.xpath("//li[@id='Opportunity_Tab']"));
		click_element(Opportunities_Tab, "Opportunities");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct opportunity page is opened or not
		WebElement Opportunities_Page = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_opportunityPageName = Opportunities_Page.getText();
		String expected_opportunityPageName = data[1][2];
		actual_expected_result(actual_opportunityPageName, expected_opportunityPageName, "Opportunity page name");

		//New Opportunities Page
		WebElement New_button = driver.findElement(By.name("new"));
		click_element(New_button, "New button");

		//Validating correct opportunity page is opened or not
		WebElement New_opportunitiesPage = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_New_opportunitiesPageName = New_opportunitiesPage.getText();
		String expected_New_opportunitiesPageName = data[1][3];
		actual_expected_result(actual_New_opportunitiesPageName, expected_New_opportunitiesPageName, "Opportunity page name");
				
		//Opportunity details
		WebElement Opportunity_Name = driver.findElement(By.id("opp3"));
		Opportunity_Name.clear();
		enter_data_textbox(Opportunity_Name, data[1][4], "Opportunity Name");

		WebElement Opportunity_Account_Name = driver.findElement(By.id("opp4"));
		Opportunity_Account_Name.clear();
		enter_data_textbox(Opportunity_Account_Name, data[1][5], "Opportunity Account Name");

		//Choosing Close Date and Today's date
		WebElement Close_Date = driver.findElement(By.id("opp9"));
		click_element(Close_Date, "Close Date");
		WebElement Today_Date = driver.findElement(By.className("calToday"));
		click_element(Today_Date, "Today Date");
		
		Opportunity_Name.click();//This step is to just close the calender window

		//Stage dropdown
		dropdown = driver.findElement(By.id("opp11"));
		click_element(dropdown, "Stage dropdown");
		Select stage_dropdown = new Select(dropdown);
		stage_dropdown.selectByVisibleText(data[1][6]);
		logger.log(LogStatus.INFO, "Selected Qualification in Stage dropdown");

		//Probability
		WebElement Probability = driver.findElement(By.id("opp12"));
		Probability.clear();
		enter_data_textbox(Probability, data[1][7], "Probability");

		//Lead Source dropdown
		dropdown = driver.findElement(By.id("opp6"));
		click_element(dropdown, "Lead Source dropdown");
		Select LeadSource_dropdown = new Select(dropdown);
		LeadSource_dropdown.selectByVisibleText(data[1][8]);
		logger.log(LogStatus.INFO, "Selected Partner Referral in Lead Source dropdown");

		//Save button
		WebElement save_button = driver.findElement(By.name("save"));
		click_element(save_button, "Save button");

		Thread.sleep(5000);
		
		//Verifying if the headline of created account text is same as the opportunity name
		WebElement Opportunity_Page = driver.findElement(By.className("pageDescription"));
		String actual_createdOpportunityName = Opportunity_Page.getText();
		String expected_createdOpportunityName = data[1][4];
		actual_expected_result(actual_createdOpportunityName, expected_createdOpportunityName, "Newly created Opportunity page name");
	}

	@Test(priority=17)
	public void TC17_TestOpportunity_Pipeline_Report() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase17.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC17_TestOpportunity_Pipeline_Report");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Opportunities page
		WebElement Opportunities_Tab = driver.findElement(By.xpath("//li[@id='Opportunity_Tab']"));
		click_element(Opportunities_Tab, "Opportunities");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct opportunity page is opened or not
		WebElement Opportunities_Page = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_opportunityPageName = Opportunities_Page.getText();
		String expected_opportunityPageName = data[1][2];
		actual_expected_result(actual_opportunityPageName, expected_opportunityPageName, "Opportunity page name");
		
		//Opportunity Pipeline
		WebElement Opportunity_Pipeline = driver.findElement(By.xpath("//a[contains(text(),'Opportunity Pipeline')]"));
		click_element(Opportunity_Pipeline, "Opportunity Pipeline");

		//It prints all the values in a table
/*	    List<WebElement> Table = driver.findElements(By.xpath("//*[@id=\"fchArea\"]/table"));
		for(int i=0; i<Table.size(); i++) {
			System.out.println("Values are : " + Table.get(i).getText());
		}
*/	
		//Number of rows and number of columns in a table
/*		List<WebElement> NumberOf_Rows = driver.findElements(By.xpath("//table[@class='reportTable tabularReportTable']/tbody/tr"));
		System.out.println(NumberOf_Rows.size());
		List<WebElement> NumberOf_Columns = driver.findElements(By.xpath("//table[contains(@class,'reportTable tabularReportTable')]/tbody/tr/th"));
		System.out.println(NumberOf_Columns.size());

		//for printing particular column
		List<WebElement> Opportunity_Column = driver.findElements(By.xpath("//table[contains(@class,'reportTable tabularReportTable')]/tbody/tr/td[5]"));
		System.out.println("Opportunity Name column size is : " + Opportunity_Column.size());
		for(int i=0; i<Opportunity_Column.size(); i++) {
			Opportunity_Column.get(i).getText();
		}

		String Search_Opportunity_name = data[1][3];

		//Verifying if the created opportunity is within the opportunities pipeline or not 
		for(int i=0; i<Opportunity_Column.size(); i++) {
			if(Opportunity_Column.get(i).getText().equals(Search_Opportunity_name)) {
				logger.log(LogStatus.PASS, "Recently created Opportunity is available in the opportunities pipeline");
				break;
			}
		}
	}

	@Test(priority=18)
	public void TC18_TestStuck_Opportunity_Report() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase18.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC18_TestStuck_Opportunity_Report");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Opportunities page
		WebElement Opportunities_Tab = driver.findElement(By.xpath("//li[@id='Opportunity_Tab']"));
		click_element(Opportunities_Tab, "Opportunities");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct opportunity page is opened or not
		WebElement Opportunities_Page = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_opportunityPageName = Opportunities_Page.getText();
		String expected_opportunityPageName = data[1][2];
		actual_expected_result(actual_opportunityPageName, expected_opportunityPageName, "Opportunity page name");
		
		//Stuck Opportunities
		WebElement Stuck_Opportunities = driver.findElement(By.xpath("//a[contains(text(),'Stuck Opportunities')]"));
		click_element(Stuck_Opportunities, "Stuck Opportunities");

	/*	//It prints all the values from the table
		List<WebElement> Table = driver.findElements(By.xpath("//*[@id=\"fchArea\"]/table"));
		for(int i=0; i<Table.size(); i++) {
			System.out.println("Values are : " + Table.get(i).getText());
		}
	 */
		
		//Number of rows and number of columns in a table
/*		List<WebElement> NumberOf_Rows = driver.findElements(By.xpath("//table[@class='reportTable tabularReportTable']/tbody/tr"));
		System.out.println(NumberOf_Rows.size());
		List<WebElement> NumberOf_Columns = driver.findElements(By.xpath("//table[@class='reportTable tabularReportTable']/tbody/tr/th"));
		System.out.println(NumberOf_Columns.size());

		//for printing particular column
		List<WebElement> Opportunity_Column = driver.findElements(By.xpath("//table[@class='reportTable tabularReportTable']/tbody/tr/td[4]"));
		System.out.println("Account Name column size is : " + Opportunity_Column.size());
		for(int i=0; i<Opportunity_Column.size(); i++) {
			Opportunity_Column.get(i).getText();
		}

		String Search_Opportunity_name = data[1][3];
		//Verifying if the created account is within the stuck opportunities pipeline or not 
		for(int i=0; i<Opportunity_Column.size(); i++) {
			if(Opportunity_Column.get(i).getText().equals(Search_Opportunity_name)) {
				logger.log(LogStatus.PASS, "Recently created Opportunity is available in the Stuck opportunities pipeline");
				break;
			}
		}
	}

	@Test(priority=19)
	public void TC19_TestQuarterly_Summary_Report() throws IOException, ParseException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase19.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC19_TestQuarterly_Summary_Report");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Opportunities page
		WebElement Opportunities_Tab = driver.findElement(By.xpath("//li[@id='Opportunity_Tab']"));
		click_element(Opportunities_Tab, "Opportunities");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct opportunity page is opened or not
		WebElement Opportunities_Page = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_opportunityPageName = Opportunities_Page.getText();
		String expected_opportunityPageName = data[1][2];
		actual_expected_result(actual_opportunityPageName, expected_opportunityPageName, "Opportunity page name");
		
		//Selecting option from Inteval dropdown
		WebElement dropdown = driver.findElement(By.xpath("//select[@name='quarter_q']"));
		dropdown.click();
		Select Interval_dropdown = new Select(dropdown);
		Interval_dropdown.selectByVisibleText(data[1][3]);
		logger.log(LogStatus.INFO, "Selected Current and Next FQ from Interval dropdown");

		//Selection option from Include dropdown
		dropdown = driver.findElement(By.xpath("//select[@name='open']"));
		dropdown.click();
		Select Include_dropdown = new Select(dropdown);
		Include_dropdown.selectByVisibleText(data[1][4]);
		logger.log(LogStatus.INFO, "Selected All Opportunities from Include dropdown");

		WebElement Run_Report = driver.findElement(By.xpath("//*[@id=\"lead_summary\"]/table/tbody/tr[3]/td/input"));
		click_element(Run_Report, "Run Report Button");

		//Number of rows and number of columns in a table
		List<WebElement> NumberOf_Rows = driver.findElements(By.xpath("//*[@id=\"fchArea\"]/table/tbody/tr"));
		System.out.println(NumberOf_Rows.size());
		List<WebElement> NumberOf_Columns = driver.findElements(By.xpath("//*[@id=\"fchArea\"]/table/tbody/tr[4]/td"));
		System.out.println(NumberOf_Columns.size());

		//for printing particular column
		List<WebElement> Close_Date_Column = driver.findElements(By.xpath("//*[@id=\"fchArea\"]/table/tbody/tr/td[11]"));
		System.out.println("Close Date column size is : " + Close_Date_Column.size());

		//Printing all the data available in the Close Date Column
		for(int i=0; i<Close_Date_Column.size(); i++) {
			Close_Date_Column.get(i).getText();
		}	

		int count = 0; //To verify all the dates are within the range

		//Verifying if the dates are within the current and next FQ dates
		for (int i = 0; i < Close_Date_Column.size(); i++) {
	        String date = Close_Date_Column.get(i).getText();
	        boolean result = comp_Dates(date);
	        if (result) {
	        	count++;
	        } else {
	            System.out.println(result + " Dates are not within the range. Test Case Failed");
	        }
	    }
		//System.out.println(count);
		if(count == Close_Date_Column.size()) {
			logger.log(LogStatus.PASS, "All the dates reported in the table are within the range (Current and Next FQ)");
		}
		else
			logger.log(LogStatus.FAIL, "Dates reported in the table are not within the range (Current and Next FQ)");
	}

	//Function to verify if the date is within the rage or not
	public static boolean comp_Dates(String date) throws ParseException {
		//try {
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

			String FromDate = "10/01/2019";
			String ToDate = "03/31/2020";

			java.util.Date Fdate = fmt.parse(FromDate);
			java.util.Date Tdate = fmt.parse(ToDate);
			java.util.Date ActualDate = fmt.parse(date);

			if (ActualDate.compareTo(Fdate) >= 0 && ActualDate.compareTo(Tdate) <= 0) {
				return true;
			}
		//} 
		//catch (Exception ex) {
			//System.out.println(ex);
		//}
		return false;
	}

	@Test(priority=20)
	public void TC20_Leads_Tab() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase20.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC20_Leads_Tab");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Leads page
		WebElement Leads_Tab = driver.findElement(By.xpath("//li[@id='Lead_Tab']//a[contains(text(),'Leads')]"));
		click_element(Leads_Tab, "Leads");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct Leads page is opened or not
		WebElement Leads = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_LeadsPageName = Leads.getText();
		String expected_LeadsPageName = data[1][2];
		actual_expected_result(actual_LeadsPageName, expected_LeadsPageName, "Leads page name");
	}

	@Test(priority=21)
	public void TC21_Leads_Select_View() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase21.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC21_Leads_Select_View");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Leads page
		WebElement Leads_Tab = driver.findElement(By.xpath("//li[@id='Lead_Tab']//a[contains(text(),'Leads')]"));
		click_element(Leads_Tab, "Leads");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct Leads page is opened or not
		WebElement Leads = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_LeadsPageName = Leads.getText();
		String expected_LeadsPageName = data[1][2];
		actual_expected_result(actual_LeadsPageName, expected_LeadsPageName, "Leads page name");

		//Expected dropdown elements
		String[] Expected_View_dropdown = {"All Open Leads", "My Unread Leads", "Recently Viewed Leads", "Today's Leads"};

		//Verifying if those elements are present in the dropdown or not
		WebElement dropdown = driver.findElement(By.xpath("//select[@id='fcf']"));
		click_element(dropdown, "view dropdown");
		Select View_dropdown = new Select(dropdown);
		List<WebElement> actual_View_dropdown = View_dropdown.getOptions();
		logger.log(LogStatus.INFO, "Stored all the options of dropdown in actual_View_dropdown");
		
		int count = 0; //Considering this element to verify all the elements are present
		for(int i=0; i<Expected_View_dropdown.length; i++) {
			if(actual_View_dropdown.get(i).getText().equals(Expected_View_dropdown[i])){
				count++;
			}
		}
		if(count == 4) {
			logger.log(LogStatus.PASS, "All the Elements are presented in the dropdown");
		}
		else
			logger.log(LogStatus.FAIL, "All the Elements are not presented in the dropdown");
	}

	@Test(priority=22)
	public void TC22_Leads_Default_View() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase22.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC22_Leads_Default_View");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Leads page
		WebElement Leads_Tab = driver.findElement(By.xpath("//li[@id='Lead_Tab']//a[contains(text(),'Leads')]"));
		click_element(Leads_Tab, "Leads");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct Leads page is opened or not
		WebElement Leads = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_LeadsPageName = Leads.getText();
		String expected_LeadsPageName = data[1][2];
		actual_expected_result(actual_LeadsPageName, expected_LeadsPageName, "Leads page name");

		//Clicking Today's Leads from dropdown
		WebElement dropdown = driver.findElement(By.xpath("//select[@id='fcf']"));
		click_element(dropdown, "view dropdown");
		Select View_dropdown = new Select(dropdown);
		View_dropdown.selectByVisibleText(data[1][3]);
		logger.log(LogStatus.INFO, "Selected Today's Leads from the view dropdown");

		//Logout from usermenu dropdown
		WebElement Usermenu_dropdown = driver.findElement(By.xpath("//*[@id=\"userNav\"]"));
		click_element(Usermenu_dropdown, "Usermenu dropdown");
		WebElement Logout = driver.findElement(By.xpath("//a[contains(text(), 'Logout')]"));
		click_element(Logout, "Logout");

		//Verifying if the salesforce login page is appeared or not
		WebElement Username_Text = driver.findElement(By.xpath("//label[@class='label usernamelabel']"));
		String actualLogin_Page = Username_Text.getText();
		String expectedLogin_Page = data[1][4];
		actual_expected_result(actualLogin_Page, expectedLogin_Page, "Userame text in Login page");

		//Logging in into the salesforce application
		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Leads page
		Leads_Tab = driver.findElement(By.xpath("//li[@id='Lead_Tab']//a[contains(text(),'Leads')]"));
		click_element(Leads_Tab, "Leads");
		
		//Go button
		WebElement Go_button = driver.findElement(By.xpath("//input[@name='go']"));
		click_element(Go_button, "Go button");

		//Verifying the default dropdown
		WebElement default_dropdown = driver.findElement(By.xpath("//select[@id='00B6g0000020dEL_listSelect']//option[contains(text(),\"Today's Leads\")]"));
		if(default_dropdown.getText().equals(data[1][3])) {
			logger.log(LogStatus.PASS, "dropdown is defaultly set for Today's Leads");
		}
	}

	@Test(priority=23)
	public void TC23_TodaysLead_View() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase23.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC23_TodaysLead_View");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Leads page
		WebElement Leads_Tab = driver.findElement(By.xpath("//li[@id='Lead_Tab']//a[contains(text(),'Leads')]"));
		click_element(Leads_Tab, "Leads");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct Leads page is opened or not
		WebElement Leads = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_LeadsPageName = Leads.getText();
		String expected_LeadsPageName = data[1][2];
		actual_expected_result(actual_LeadsPageName, expected_LeadsPageName, "Leads page name");

		//Clicking Today's Leads from dropdown
		WebElement dropdown = driver.findElement(By.xpath("//select[@id='fcf']"));
		click_element(dropdown, "view dropdown");
		Select View_dropdown = new Select(dropdown);
		View_dropdown.selectByVisibleText(data[1][3]);
		logger.log(LogStatus.INFO, "Selected Today's Leads from the view dropdown");

		//Go button, adding this step because it was displaying Today's leads page
		WebElement Go_button = driver.findElement(By.xpath("//input[@name='go']"));
		click_element(Go_button, "Go button");
				
		//Verifying if Today's Leads page is opened or not
		String actual_TodaysLead_PageUrl = driver.getCurrentUrl();
		String expected_TodaysLead_PageUrl = data[1][4];
		System.out.println(actual_TodaysLead_PageUrl);
		actual_expected_result(actual_TodaysLead_PageUrl, expected_TodaysLead_PageUrl, "Today's Lead page url");
	}

	@Test(priority=24)
	public void TC24_New_Lead() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase24.xls";

		String[][] data = readExcel(path,"Sheet1");
		
		logger = reports.startTest("TC24_New_Lead");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");
		
		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");
		
		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Opening Leads page
		WebElement Leads_Tab = driver.findElement(By.xpath("//li[@id='Lead_Tab']//a[contains(text(),'Leads')]"));
		click_element(Leads_Tab, "Leads");
		
		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");
		
		//Validating correct Leads page is opened or not
		WebElement Leads = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_LeadsPageName = Leads.getText();
		String expected_LeadsPageName = data[1][2];
		actual_expected_result(actual_LeadsPageName, expected_LeadsPageName, "Leads page name");

		//Creating a New Lead
		WebElement New_button = driver.findElement(By.xpath("//input[@name='new']"));
		click_element(New_button, "New button");

		//Validating correct Leads page is opened or not
		WebElement New_Lead_Page = driver.findElement(By.xpath("//h2[@class='pageDescription']"));
		String actual_New_Lead_PageName = New_Lead_Page.getText();
		String expected_New_Lead_PageName = data[1][3];
		actual_expected_result(actual_New_Lead_PageName, expected_New_Lead_PageName, "New Lead page name");
		
		//LastName, Company Name and Save
		WebElement Last_Name = driver.findElement(By.xpath("//input[@id='name_lastlea2']"));
		Last_Name.clear();
		enter_data_textbox(Last_Name, data[1][4], "Last Name");
		WebElement Company_Name = driver.findElement(By.xpath("//input[@id='lea3']"));
		Company_Name.clear();
		enter_data_textbox(Company_Name, data[1][5], "Company Name");
		WebElement Save_button = driver.findElement(By.xpath("//input[@name='save']"));
		click_element(Save_button, "Save button");

		//Verifying if the Created New Lead page is opened or not
		WebElement expected = driver.findElement(By.xpath("//h2[@class='topName']"));
		String actual_LeadName= expected.getText();
		String expected_LeadName = data[1][4];
		actual_expected_result(actual_LeadName, expected_LeadName, "Created Lead page name");
	}

/*	@Test
	public void TC25_Contacts_New_Account(){
		logger = reports.startTest("TC25_Contacts_New_Account");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		Reusable.popup();

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		Contacts_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Contacts tab");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String expectedText = Text.getText();
		String actualText = "Contacts";
		if(expectedText.equals(actualText)) {
			logger.log(LogStatus.PASS, "Correct Contacts page is displayed");
		}
		else
			logger.log(LogStatus.FAIL, "Incorrect Contacts page is displayed");

		//New Contact
		WebElement New_Contact = driver.findElement(By.xpath("//input[@name='new']"));
		New_Contact.click();
		logger.log(LogStatus.INFO, "Clicked on New Contact");

		//LastName and AccountName
		WebElement LastName = driver.findElement(By.xpath("//input[@id='name_lastcon2']"));
		LastName.sendKeys("New");
		WebElement AccountName = driver.findElement(By.xpath("//input[@id='con4']"));
		AccountName.sendKeys("Mine");
		logger.log(LogStatus.INFO, "Entered Lastname and Accountname");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//td[@id='bottomButtonRow']//input[@name='save']"));
		Save_button.click();
		logger.log(LogStatus.INFO, "Clicked on Save button");

		//Verifying if the new contact page with lastname is opened or not
		Text = driver.findElement(By.xpath("//h2[@class='topName']"));
		expectedText = Text.getText();
		actualText = "New";
		if(expectedText.equals(actualText)) {
			logger.log(LogStatus.PASS, "Correct new contact page with created lastname is opened");
		}
		else
			logger.log(LogStatus.FAIL, "New contact page with incorrect lastname is opened");

		reports.endTest(logger);
	}

	@Test
	public void TC26_Contacts_CreateNewView(){
		logger = reports.startTest("TC26_Contacts_CreateNewView");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		Reusable.popup();

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		Contacts_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Contacts tab");

		//Create New View
		WebElement Create_New_View = driver.findElement(By.xpath("//a[contains(text(),'Create New View')]"));
		Create_New_View.click();
		logger.log(LogStatus.INFO, "Clicked on Create New View link");

		//Using date whenever account is getting created, it makes as unique name
		Date d = new Date(System.currentTimeMillis());

		//View Name and View Unique Name
		WebElement View_Name = driver.findElement(By.xpath("//input[@id='fname']"));
		View_Name.sendKeys("New" + d);
		WebElement View_Unique_Name = driver.findElement(By.xpath("//input[@id='devname']"));
		View_Unique_Name.clear();
		View_Unique_Name.sendKeys("Acc");
		logger.log(LogStatus.INFO, "Entered View Name and View Unique Name");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//div[@class='pbBottomButtons']//input[@name='save']"));
		Save_button.click();
		logger.log(LogStatus.INFO, "Clicked on Save button");

		String ExpectedUrl = "//na114.salesforce.com/003?fcf";
		String actualUrl = driver.getCurrentUrl();
		System.out.println(actualUrl);
		if(actualUrl.contains(ExpectedUrl)) {
			logger.log(LogStatus.PASS, "New view name is showing as default dropwdown option");
		}
		else
			logger.log(LogStatus.FAIL, "New view name is not showing as default dropwdown option");

		reports.endTest(logger);
	}
	 */	
	/*	@Test
	public void TC27_Contacts_RecentlyCreated(){
		logger = reports.startTest("TC27_Contacts_RecentlyCreated");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		Reusable.popup();

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		Contacts_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Contacts tab");

		//Recently Viewed dropdown
		WebElement dropdown = driver.findElement(By.xpath("//*[@id=\"hotlist_mode\"]"));
		dropdown.click();
		Select dropdown_options = new Select(dropdown);
		dropdown_options.selectByVisibleText("Recently Created");
		logger.log(LogStatus.INFO, "Selected Recently Created option from dropdown");

		//using boolean value to verify if the recently created account to change the status if it displays in the list
		boolean Account_present_in_the_list = false;

		//Table Name column
		List<WebElement> Name_Column = driver.findElements(By.xpath("//table[contains(@class,'list')]/tbody/tr/th"));
		for(int i=0; i<Name_Column.size(); i++) {
			if(Name_Column.get(i).getText().equals("New")) {
				Account_present_in_the_list = true;
			}	
		}
		if(Account_present_in_the_list == true) {
			logger.log(LogStatus.PASS, "Recently Created Contact is getting displayed in the list");
		}
		else
			logger.log(LogStatus.FAIL, "Recently Created Contact is not getting displayed in the list");

		reports.endTest(logger);
	}

	@Test
	public void TC28_Contacts_MyContacts(){
		logger = reports.startTest("TC28_Contacts_MyContacts");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		Reusable.popup();

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		Contacts_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Contacts tab");

		//Recently Viewed dropdown
		WebElement dropdown = driver.findElement(By.xpath("//select[@id='fcf']"));
		dropdown.click();
		Select dropdown_options = new Select(dropdown);
		dropdown_options.selectByVisibleText("My Contacts");
		logger.log(LogStatus.INFO, "Selected My Contacts option from dropdown");

		WebElement Go_button = driver.findElement(By.xpath("//input[@name='go']"));
		Go_button.click();
		logger.log(LogStatus.INFO, "Clicked on Go button");

		String expectedUrl = "https://na114.salesforce.com/003?fcf=00B3k00000Als8R";
		String actualUrl = driver.getCurrentUrl();
		if(actualUrl.equals(expectedUrl)) {
			logger.log(LogStatus.PASS, "My accounts view page is opened");
		}
		else
			logger.log(LogStatus.FAIL, "My accounts view page is not opened");

		reports.endTest(logger);
	}

	@Test
	public void TC29_Contacts_View_Contact() throws InterruptedException{
		logger = reports.startTest("TC29_Contacts_View_Contact");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		Reusable.popup();

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		Contacts_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Contacts tab");

		//Clicking on name from Recent contacts
		WebElement New = driver.findElement(By.xpath("//*[@id=\"bodyCell\"]/div[3]/div[1]/div/div[2]/table/tbody/tr[2]/th/a"));
		New.click();
		logger.log(LogStatus.INFO, "Clicked on New account");

		//Verifying the top name of the account opened
		WebElement expected_PageText = driver.findElement(By.xpath("//h2[contains(@class,'topName')]"));
		String expectedText = "New";
		String actualText = expected_PageText.getText();
		if(expectedText.equals(expectedText)) {
			logger.log(LogStatus.PASS, "Correct account name page is opened");
		}
		else
			logger.log(LogStatus.FAIL, "Incorrect account name page is opened");

		reports.endTest(logger);
	}
	 */

	/*	@Test
	public void TC30_Contacts_CreateNewView_ErrorMessage(){
		logger = reports.startTest("TC30_Contacts_CreateNewView_ErrorMessage");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		Reusable.popup();

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		Contacts_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Contacts tab");

		//Create New View
		WebElement Create_New_View = driver.findElement(By.xpath("//a[contains(text(),'Create New View')]"));
		Create_New_View.click();
		logger.log(LogStatus.INFO, "Clicked on Create New View link");

		//View Unique Name
		WebElement View_Unique_Name = driver.findElement(By.xpath("//input[@id='devname']"));
		View_Unique_Name.sendKeys("EFGH");
		logger.log(LogStatus.INFO, "Entered Unique name as EFGH");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//div[@class='pbBottomButtons']//input[@name='save']"));
		Save_button.click();
		logger.log(LogStatus.INFO, "Clicked on save button");

		//Error message
		error = driver.findElement(By.xpath("//div[contains(text(),'You must enter a value')]"));
		String expectedError = "Error: You must enter a value";
		String actualError = error.getText();

		if(expectedError.equals(actualError)) {
			logger.log(LogStatus.PASS, "Actual and expected error messages are same");
		}
		else
			logger.log(LogStatus.FAIL, "Actual and expected error messages are not same");

		reports.endTest(logger);
	}

	@Test
	public void TC31_Contacts_CreateNewView_CancelButton(){
		logger = reports.startTest("TC31_Contacts_CreateNewView_CancelButton");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		Reusable.popup();

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		Contacts_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Contacts tab");

		//Create New View
		WebElement Create_New_View = driver.findElement(By.xpath("//a[contains(text(),'Create New View')]"));
		Create_New_View.click();
		logger.log(LogStatus.INFO, "Clicked on Create New View link");

		//View Name
		WebElement View_Name = driver.findElement(By.xpath("//input[@id='fname']"));
		View_Name.sendKeys("ABCD");
		logger.log(LogStatus.INFO, "Entered View name as ABCD");

		//View Unique Name
		WebElement View_Unique_Name = driver.findElement(By.xpath("//input[@id='devname']"));
		View_Unique_Name.sendKeys("EFGH");
		logger.log(LogStatus.INFO, "Entered Unique name as EFGH");

		//Cancel
		WebElement Cancel_button = driver.findElement(By.xpath("//div[@class='pbBottomButtons']//input[@name='cancel']"));
		Cancel_button.click();
		logger.log(LogStatus.INFO, "Clicked on Cancel button");

		String expectedUrl = "https://na114.salesforce.com/003/o";
		String actualUrl = driver.getCurrentUrl();
		if(expectedUrl.equals(actualUrl)) {
			logger.log(LogStatus.PASS, "Contacts Home Page is opened");
		}
		else
			logger.log(LogStatus.FAIL, "Contacts Home Page is not opened");

		reports.endTest(logger);
	}

	@Test
	public void TC33_Login_FirstName_LastName(){
		logger = reports.startTest("TC33_Login_FirstName_LastName");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		//Home Tab
		WebElement Home_Tab = driver.findElement(By.xpath("//li[@id='home_Tab']"));
		Home_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Home Tab");

		//Username link
		WebElement Username_Link = driver.findElement(By.xpath("//*[@id=\"ptBody\"]/div/div/span[1]/h1/a"));
		Username_Link.click();
		logger.log(LogStatus.INFO, "Clicked on Username link");

		//Verifying if the first and last name is getting displayed correctly or not
		WebElement Verify_FirstName_Lastname = driver.findElement(By.xpath("//span[@id='tailBreadcrumbNode']"));
		String Expected_FirstName_LastName = "Venkata Phani Divya Abcd ";
		String Actual_FirstName_LastName = Verify_FirstName_Lastname.getText();
		System.out.println(Actual_FirstName_LastName);
		if(Expected_FirstName_LastName.equals(Actual_FirstName_LastName)) {
			logger.log(LogStatus.PASS, "First and Lastname are getting displayed correctly");
		}
		else
			logger.log(LogStatus.FAIL, "First and Lastname are not getting displayed correctly");

		reports.endTest(logger);
	}

	@Test
	public void TC34_HomeTab_EditLastName() throws InterruptedException{
		logger = reports.startTest("TC34_HomeTab_EditLastName");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		//Home Tab
		WebElement Home_Tab = driver.findElement(By.xpath("//li[@id='home_Tab']"));
		Home_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Home Tab");

		//Leftside Username link
		WebElement Username = driver.findElement(By.xpath("//*[@id=\"mru0053k00000Aipov\"]/a/span"));
		Username.click();
		logger.log(LogStatus.INFO, "Clicked on Username link (leftside of the home page)");

		//Edit Profile
		WebElement Edit_Profile = driver.findElement(By.xpath("//*[@id=\"chatterTab\"]/div[2]/div[2]/div[1]/h3/div/div/a/img"));
		Edit_Profile.click();
		logger.log(LogStatus.INFO, "Clicked on Edit Profile");

		Thread.sleep(5000);

		//Edit Profile Iframe
		WebElement Edit_Profile_Iframe = driver.findElement(By.xpath("//iframe[@id='contactInfoContentId']"));
		Edit_Profile_Iframe.click();
		driver.switchTo().frame(Edit_Profile_Iframe);
		logger.log(LogStatus.INFO, "Switched to Edit Profile Iframe");

		//About Tab
		WebElement About_Tab = driver.findElement(By.xpath("//li[@id='aboutTab']"));
		About_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on About Tab");

		//About Tab Last Name
		WebElement LastName = driver.findElement(By.xpath("//input[@id='lastName']"));
		LastName.clear();
		LastName.sendKeys("Abcd");
		logger.log(LogStatus.INFO, "Entered LastName as 'Abcd'");

		//Save All button
		WebElement SaveAll_button = driver.findElement(By.xpath("//input[@class='zen-btn zen-primaryBtn zen-pas']"));
		SaveAll_button.click();
		logger.log(LogStatus.INFO, "Clicked on SaveAll button");

		//Verifying if the updated last name is getting displayed correctly or not
		WebElement Verify_Updated_Lastname = driver.findElement(By.xpath("//span[@id='tailBreadcrumbNode']"));
		String Expected_LastName = "Abcd";
		String Actual_LastName = Verify_Updated_Lastname.getText();
		if(Actual_LastName.contains(Expected_LastName)) {
			logger.log(LogStatus.PASS, "Updated Lastname is getting displayed");
		}
		else
			logger.log(LogStatus.FAIL, "Updated Lastname is not getting displayed");

		reports.endTest(logger);
	}

	@Test
	public void TC35_Tab_Customization() throws InterruptedException{
		logger = reports.startTest("TC35_Tab_Customization");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		//+ Tab
		WebElement Plus_Tab = driver.findElement(By.xpath("//img[@class='allTabsArrow']"));
		Plus_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on '+'");

		//Customize Tabs
		WebElement Customize_My_Tabs = driver.findElement(By.xpath("//input[@name='customize']"));
		Customize_My_Tabs.click();
		logger.log(LogStatus.INFO, "Clicked on Customize My Tabs");	

		/*These statements verifies if the "MobileLanding" is availble in the Available Fields
	 * If MobileLanding is available under Available Fields, It add the same to Selected Fields
	 * and save the modifications
	 */
	/*		WebElement Available_Fields_dropdown = driver.findElement(By.xpath("//select[@id='duel_select_0']"));
		Select Available_Fields_dropdown_list = new Select(Available_Fields_dropdown);
		List<WebElement> Available_Fields_dropdown_options = Available_Fields_dropdown_list.getOptions();
		for(int i=0; i<Available_Fields_dropdown_options.size(); i++) {
			if(Available_Fields_dropdown_options.get(i).getText().equals("MobileLanding")) {
				Available_Fields_dropdown_options.get(i).click();
				WebElement Add_arrow = driver.findElement(By.xpath("//img[@class='rightArrowIcon']"));
				Add_arrow.click();
			}
		}

		Thread.sleep(5000);

		//Selected Tabs
		WebElement Selected_Tabs = driver.findElement(By.xpath("//select[@id='duel_select_1']"));
		Select Selected_Tabs_Options = new Select(Selected_Tabs);
		Selected_Tabs_Options.selectByVisibleText("MobileLanding");

		//Remove button
		WebElement Remove_button = driver.findElement(By.xpath("//img[@class='leftArrowIcon']"));
		Remove_button.click();
		logger.log(LogStatus.INFO, "Clicked on Remove button to remove MobileLanding from Selected Tabs");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//input[@name='save']"));
		Save_button.click();
		logger.log(LogStatus.INFO, "Clicked on Save button");

		//Usermenu Logout
		WebElement Usermenu_dropdown = driver.findElement(By.xpath("//span[@id='userNavLabel']"));
		Usermenu_dropdown.click();
		WebElement Logout = driver.findElement(By.xpath("//a[contains(text(),'Logout')]"));
		Logout.click();
		logger.log(LogStatus.INFO, "Clicked on Logout from usermenu dropdown");

		//Validating expected Login Page and actual Page
		String expectedUrl = "https://na114.salesforce.com/secur/logout.jsp";
		String actualUrl = driver.getCurrentUrl();
		if(expectedUrl.equals(actualUrl)) {
			logger.log(LogStatus.PASS, "Correct SFDC login page is displayed");
		}
		else
			logger.log(LogStatus.FAIL, "Correct SFDC login page is not displayed");

		//Re-loggingin to the SFDC application
		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		//Validating if the MobileLanding tab is not displayed in the tab bar
		List<WebElement> All_Tabs_Options = driver.findElements(By.xpath("//div[@id='tabContainer']"));
		boolean flag = false;
		for(int i=0; i<All_Tabs_Options.size(); i++) {
			if(All_Tabs_Options.get(i).getText().equals("MobileLanding")) {
				flag = true;
			}
		}
		if(flag==false){
			logger.log(LogStatus.PASS, "MobileLanding Tab was removed from the All Tabs section");
		}
		else
			logger.log(LogStatus.FAIL, "MobileLanding Tab was not removed from the All Tabs section");

		reports.endTest(logger);
	}
	
	@Test
	public void TC36_Blocking_Event_In_Calender() throws InterruptedException, ParseException{
		logger = reports.startTest("TC36_Blocking_Event_In_Calender");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		//Home Tab
		WebElement Home_Tab = driver.findElement(By.xpath("//li[@id='home_Tab']"));
		Home_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Home Tab");

		popup();

		//Getting current date
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyyy");
		Date today = dateOnly.parse(dateOnly.format(new Date()));
		System.out.println(today.toString());

		//Current date link
		WebElement Current_Date = driver.findElement(By.xpath("//a[contains(text(),'Friday November 8, 2019')]"));
		System.out.println(Current_Date.getText());
		Current_Date.click();

		//8:00PM Link
		WebElement EightPM_link = driver.findElement(By.xpath("//a[contains(text(),'8:00 PM')]"));
		EightPM_link.click();
		logger.log(LogStatus.INFO, "Clicked on 8:00PM link");

		//Combo-box icon
		WebElement Combo_box_Icon = driver.findElement(By.xpath("//*[@id=\"ep\"]/div[2]/div[4]/table/tbody/tr[2]/td[2]/div/a/img"));
		Combo_box_Icon.click();
		logger.log(LogStatus.INFO, "Clicked on Combo box icon");

		String parentWindowHandler = driver.getWindowHandle();
		//System.out.println(parentWindowHandler);
		//System.out.println("Title of the new window: " + driver.getTitle());
		for(String handle : driver.getWindowHandles()) {
			if(!handle.equals(parentWindowHandler)) {
				//System.out.println(handle);
				driver.switchTo().window(handle);
				//System.out.println("Title of the new window: " + driver.getTitle());

				//Other option from popup
				WebElement Other = driver.findElement(By.xpath("//a[contains(text(),'Other')]"));
				Other.click();
				logger.log(LogStatus.INFO, "Clicked on other option from combo box popup");

			}
		}

		driver.switchTo().window(parentWindowHandler);

		//End Date Time
		WebElement End_Time = driver.findElement(By.xpath("//input[@id='EndDateTime_time']"));
		End_Time.clear();
		End_Time.sendKeys("9:30 PM");
		logger.log(LogStatus.INFO, "Entered end time as 9:30PM");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//div[contains(@class,'pbBottomButtons')]//input[1]"));
		Save_button.click();
		logger.log(LogStatus.INFO, "Clicked on save button");

		//Validating expected Login Page and actual Page
		String expectedUrl = "https://na174.salesforce.com/00U/c?md3=312&md0=2019&eventSaved=true";
		String actualUrl = driver.getCurrentUrl();
		System.out.println(actualUrl);
		if(expectedUrl.equals(actualUrl)) {
			logger.log(LogStatus.PASS, "Calendar for FirstName LastName page is displayed");
		}
		else
			logger.log(LogStatus.FAIL, "Calendar for FirstName LastName page is not displayed");

		reports.endTest(logger);
	}
*/
/*	@Test
	public void TC37_Blocking_Event_In_Calender_Weekly_Recurrence() throws InterruptedException, ParseException{

		logger = reports.startTest("TC37_Blocking_Event_In_Calender_Weekly_Recurrence");

		Reusable.validUsername();
		logger.log(LogStatus.INFO, "Entered username");
		Reusable.validPassword();
		logger.log(LogStatus.INFO, "Entered password");
		Reusable.Login();
		logger.log(LogStatus.INFO, "Clicked on Login");

		//Home Tab
		WebElement Home_Tab = driver.findElement(By.xpath("//li[@id='home_Tab']"));
		Home_Tab.click();
		logger.log(LogStatus.INFO, "Clicked on Home Tab");

		popup();

		//Getting current date
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyyy");
		Date today = dateOnly.parse(dateOnly.format(new Date()));
		System.out.println(today.toString());

		//Current date link
		WebElement Current_Date = driver.findElement(By.xpath("//a[contains(text(),'Friday November 8, 2019')]"));
		System.out.println(Current_Date.getText());
		Current_Date.click();

		//4:00PM Link
		WebElement FourPM_link = driver.findElement(By.xpath("//a[contains(text(),'4:00 PM')]"));
		FourPM_link.click();
		logger.log(LogStatus.INFO, "Clicked on 4:00PM link");

		//Combo-box icon
		WebElement Combo_box_Icon = driver.findElement(By.xpath("//*[@id=\"ep\"]/div[2]/div[4]/table/tbody/tr[2]/td[2]/div/a/img"));
		Combo_box_Icon.click();
		logger.log(LogStatus.INFO, "Clicked on Combo box icon");

		String parentWindowHandler = driver.getWindowHandle();
		for(String handle : driver.getWindowHandles()) {
			if(!handle.equals(parentWindowHandler)) {
				driver.switchTo().window(handle);

				//Other option from popup
				WebElement Other = driver.findElement(By.xpath("//a[contains(text(),'Other')]"));
				Other.click();
				logger.log(LogStatus.INFO, "Clicked on other option from combo box popup");
			}
		}

		driver.switchTo().window(parentWindowHandler);

		//End Date Time
		WebElement End_Time = driver.findElement(By.xpath("//input[@id='EndDateTime_time']"));
		End_Time.clear();
		End_Time.sendKeys("7:00 PM");
		logger.log(LogStatus.INFO, "Entered end time as 7:00PM");

		//Recurrence checkbox
		WebElement Recurrence_checkbox = driver.findElement(By.xpath("//*[@id=\"IsRecurrence\"]"));
		if(Recurrence_checkbox.isSelected()) {
			logger.log(LogStatus.INFO, "Recurrence checkbox has been already selected");
		}
		else {
			Recurrence_checkbox.click();
			logger.log(LogStatus.INFO, "Clicked on Recurrence_checkbox");
		}
		//Weekly radio button
		WebElement Weekly_radio_button = driver.findElement(By.xpath("//input[@id='rectypeftw']"));
		Weekly_radio_button.click();
		//Recurrence End date
		WebElement Recurrence_End_date = driver.findElement(By.xpath("//input[@id='RecurrenceEndDateOnly']"));
		Recurrence_End_date.click();
		Recurrence_End_date.sendKeys("11/22/2019");
		
/*		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//Calendar cal = Calendar.getInstance();
		cal.setTime(new Date()); // Now use today date.
		cal.add(Calendar.DATE, 14); // Adding 5 days
		Date date = cal.getTime();
*/
		//Save
/*		WebElement Save_button = driver.findElement(By.xpath("//div[contains(@class,'pbBottomButtons')]//input[1]"));
		Save_button.click();
		logger.log(LogStatus.INFO, "Clicked on save button");
				
		//Validating expected Login Page and actual Page
		String expectedUrl = "https://na174.salesforce.com/00U/c?md3=312&md0=2019&eventSaved=true";
		String actualUrl = driver.getCurrentUrl();
		System.out.println(actualUrl);
		if(expectedUrl.equals(actualUrl)) {
			logger.log(LogStatus.PASS, "Calendar for FirstName LastName page is displayed");
		}
		else
			logger.log(LogStatus.FAIL, "Calendar for FirstName LastName page is not displayed");

		//Month view
		WebElement Month_View_Icon = driver.findElement(By.xpath("//img[@class='monthViewIcon']"));
		Month_View_Icon.click();
		logger.log(LogStatus.INFO, "Clicked on month view");
		
		expectedUrl = "https://na174.salesforce.com/00U/c?cType=1&md0=2019&md1=10";
		actualUrl = driver.getCurrentUrl();
		System.out.println(actualUrl);
		if(expectedUrl.equals(actualUrl)) {
			logger.log(LogStatus.PASS, "Correct month view calender page is displayed");
		}
		else
			logger.log(LogStatus.FAIL, "Correct month view calender page is not displayed");
		
		reports.endTest(logger);
	}
*/
}
