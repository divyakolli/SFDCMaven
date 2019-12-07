package SFDCMaven.SFDCMaven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class UsermenuDropdownTest{
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
		reports = new ExtentReports("C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\ExtentReports\\SFDC_UsermenuDropdown.html", true);		
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
	public void Usermenu_dropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase5.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Usermenu_dropdown");

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

	@Test(priority=2)
	public void Select_MyProfile_usermenuDropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase6.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Select_MyProfile_usermenuDropdown");

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

		UserMenudropdown();

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

		//Share button - File link
		Share_Button = driver.findElement(By.xpath("//*[@id=\"publishersharebutton\"]"));
		click_element(Share_Button, "Share Button button");

		//Uploading photo, Mouse Hover
		WebElement moderator = driver.findElement(By.xpath("//span[@class='profileImage chatter-avatarFull chatter-avatar']//img[@class='chatter-photo']"));
		Actions action = new Actions(driver);
		action.moveToElement(moderator).build().perform();

		//Upload Photo
		WebElement Upload_Photo = driver.findElement(By.xpath("//*[@id=\"uploadLink\"]"));
		click_element(Upload_Photo, "Upload Photo button");

		//Upload photo link
		WebElement Photo_link = driver.findElement(By.xpath("//iframe[@id=\"uploadPhotoContentId\"]"));
		driver.switchTo().frame(Photo_link);

		//Upload photo link
		WebElement Choose_File_Photo = driver.findElement(By.xpath("//*[@id=\"j_id0:uploadFileForm:uploadInputFile\"]"));
		Choose_File_Photo.sendKeys("C:\\PIC.jpg");
		logger.log(LogStatus.INFO, "Selected photo to upload");

		//Save button
		WebElement Save_button = driver.findElement(By.id("j_id0:uploadFileForm:uploadBtn"));
		click_element(Save_button, "Save button");
		
		//Crop photo save
		WebElement Crop_photo_save = driver.findElement(By.id("j_id0:j_id7:save"));
		click_element(Crop_photo_save, "save button");
		
		//Image verification
		WebElement ImageFile = driver.findElement(By.xpath("//img[contains(@alt,'Venkata Phani Divya Abcd')]"));
		Boolean ImagePresent = (Boolean) ((JavascriptExecutor)driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", ImageFile);
		if (!ImagePresent) {
			logger.log(LogStatus.FAIL, "Image is not being displayed as the profile photo");
		  } else {
			  logger.log(LogStatus.PASS, "Image is being displayed as the profile photo");
		}
	}

	@Test(priority=3)
	public void MySettings_Usemenu_Dropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase7.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("MySettings_Usemenu_Dropdown");

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

	@Test(priority=4)
	public void Developer_Console() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase8.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Developer_Console");

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

	@Test(priority=5)
	public void Logout_UsermenuDropdown() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase9.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Logout_UsermenuDropdown");

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
}
