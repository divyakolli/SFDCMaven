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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class LeadsTabTest {
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
		reports = new ExtentReports("C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\ExtentReports\\SFDC_LeadsTab.html", true);		
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
	public void Leads_Tab() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase20.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Leads_Tab");

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

	@Test(priority=2)
	public void Leads_Select_View() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase21.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Leads_Select_View");

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

	@Test(priority=3)
	public void Leads_Default_View() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase22.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Leads_Default_View");

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

	@Test(priority=4)
	public void TodaysLead_View() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase23.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("TodaysLead_View");

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

	@Test(priority=5)
	public void New_Lead() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase24.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("New_Lead");

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
}
