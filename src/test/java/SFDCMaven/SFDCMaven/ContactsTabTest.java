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

public class ContactsTabTest {
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
		reports = new ExtentReports("C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\ExtentReports\\SFDC_ContactsTab.html", true);		
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
	public void Create_New_Contact() throws IOException, InterruptedException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase25.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Create_New_Contact");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		//New Contact
		WebElement New_Contact = driver.findElement(By.xpath("//input[@name='new']"));
		click_element(New_Contact, "New button");

		//LastName and AccountName
		WebElement LastName = driver.findElement(By.xpath("//input[@id='name_lastcon2']"));
		enter_data_textbox(LastName, data[1][3], "Last Name");
		WebElement AccountName = driver.findElement(By.xpath("//input[@id='con4']"));
		enter_data_textbox(AccountName, data[1][4], "Account Name");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//td[@id='bottomButtonRow']//input[@name='save']"));
		click_element(Save_button, "Save button");

		Thread.sleep(5000);

		//Verifying if the new contact page with lastname is opened or not
		WebElement CreatedContact_Text = driver.findElement(By.xpath("//h2[@class='topName']"));
		String actual_Contact_Name = CreatedContact_Text.getText();
		String expected_Contact_Name = data[1][5];
		actual_expected_result(actual_Contact_Name, expected_Contact_Name, "Newly Created contact name");
	}

	@Test(priority=2)
	public void Contacts_CreateNewView() throws IOException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase26.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Contacts_CreateNewView");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		//Create New View
		WebElement Create_New_View = driver.findElement(By.xpath("//a[contains(text(),'Create New View')]"));
		click_element(Create_New_View, "Create New View");

		sdf = new SimpleDateFormat("yyyy_MM_ddhh_mm_ss");
		Calendar cal = Calendar.getInstance();

		//View Name and View Unique Name
		WebElement View_Name = driver.findElement(By.xpath("//input[@id='fname']"));
		View_Name.clear();
		enter_data_textbox(View_Name, data[1][3]+sdf.format(cal.getTime()), "View Name");
		WebElement View_Unique_Name = driver.findElement(By.xpath("//input[@id='devname']"));
		View_Unique_Name.clear();
		View_Unique_Name.sendKeys(data[1][4]+sdf.format(cal.getTime()));

		//Using to verify the expected_contact_view_name text
		String expected_contact_view_name = View_Name.getAttribute("value");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//div[@class='pbBottomButtons']//input[@name='save']"));
		click_element(Save_button, "Save button");

		//Validationg if the dropdown has new created contact view or not
		WebElement dropdown = driver.findElement(By.name("fcf"));
		List<WebElement> dropdown_items = dropdown.findElements(By.tagName("option"));
		System.out.println(expected_contact_view_name);
		for(int i=0; i<dropdown_items.size(); i++) {
			//System.out.println(dropdown_items.get(i).getText());
			if((dropdown_items.get(i).getText()).equals(expected_contact_view_name)){
				logger.log(LogStatus.PASS, "Newly created view name is diplaying in the dropdown");
				break;
			}
		}
	}

	@Test(priority=3)
	public void Recently_Created_Contact() throws IOException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase27.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Recently_Created_Contact");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		//Recently Viewed dropdown
		WebElement dropdown = driver.findElement(By.xpath("//*[@id=\"hotlist_mode\"]"));
		click_element(dropdown, "dropdown");
		Select dropdown_options = new Select(dropdown);
		dropdown_options.selectByVisibleText(data[1][3]);
		logger.log(LogStatus.INFO, "Selected Recently Created option from dropdown");

		//using boolean value to verify if the recently created account to change the status if it displays in the list
		boolean Account_present_in_the_list = false;

		//Table Name column
		List<WebElement> Name_Column = driver.findElements(By.xpath("//table[contains(@class,'list')]/tbody/tr/th"));
		for(int i=0; i<Name_Column.size(); i++) {
			if(Name_Column.get(i).getText().equals(data[1][4])) {
				Account_present_in_the_list = true;
				break;
			}	
		}
		if(Account_present_in_the_list == true) {
			logger.log(LogStatus.PASS, "Recently Created Contact is getting displayed in the list");
		}
		else
			logger.log(LogStatus.FAIL, "Recently Created Contact is not getting displayed in the list");
	}

	@Test(priority=4)
	public void dropdown_MyContacts_View() throws IOException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase28.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("dropdown_MyContacts_View");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		//View dropdown
		WebElement dropdown = driver.findElement(By.name("fcf"));
		click_element(dropdown, "dropdown");
		Select dropdown_options = new Select(dropdown);
		List<WebElement> dropdown_elements = dropdown_options.getOptions();
		for(int i=0; i<dropdown_elements.size(); i++) {
			System.out.println(dropdown_elements.get(i).getText());
			if((dropdown_elements.get(i).getText()).equals(data[1][3])){
				dropdown_elements.get(i).click();
				logger.log(LogStatus.INFO, "Selected My Contacts option from view dropdown");
				break;
			}
		}

		//Validation 
		String actual_Url = driver.getCurrentUrl();
		System.out.println(actual_Url);
		String expected_Url = data[1][4];
		actual_expected_result(actual_Url, expected_Url, "My Contacts Page url");
	}

	@Test(priority=5)
	public void View_Contact_ContactPage() throws InterruptedException, IOException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase29.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("View_Contact_ContactPage");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		String expected_contact_name = data[1][3];

		//Validationg if the dropdown has new created contact view or not
		List<WebElement> rows = driver.findElements(By.xpath("//table[contains(@class,'list')]//tbody//tr"));
		System.out.println(rows.size());
		//List<WebElement> Name_Column = driver.findElements(By.xpath("//div[contains(@class,'pbBody')]//table//tbody//tr//th"));
		//System.out.println(Name_Column.size());

		List<WebElement> NumberOf_Columns = driver.findElements(By.xpath("//table[contains(@class,'list')]/tbody/tr/th"));
		System.out.println(NumberOf_Columns.size());

		//for printing particular column
		List<WebElement> Name_Column = driver.findElements(By.xpath("//table[contains(@class,'list')]/tbody/tr/th[1]"));
		System.out.println("Name column size is : " + Name_Column.size());
		for(int i=0; i<Name_Column.size(); i++) {
			System.out.println(Name_Column.get(i).getText());
		}

		//Verifying if the created account is within the Recent Contacts or not 
		for(int i=0; i<Name_Column.size(); i++) {
			if((Name_Column.get(i).getText()).equals(expected_contact_name)){

				//Clicking on test1 name element
				WebElement dataElement = driver.findElement(By.xpath("//tr//a[contains(text(),'test1')]"));
				dataElement.click();

				//Validating contacts page if it reflects the contact details or not
				WebElement Contact_Header = driver.findElement(By.xpath("//h2[@class='topName']"));
				if((Contact_Header.getText()).equals(expected_contact_name)) {
					logger.log(LogStatus.PASS, "Contact page related to contact name : "+data[1][3]+" with all the information is displayed");
					break;
				}
			}
		}
	}

	@Test(priority=6)
	public void Contacts_CreateNewView_ErrorMessage() throws IOException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase30.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Contacts_CreateNewView_ErrorMessage");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		//Create New View
		WebElement Create_New_View = driver.findElement(By.xpath("//a[contains(text(),'Create New View')]"));
		click_element(Create_New_View, "Create New View");

		//View Unique Name
		WebElement View_Unique_Name = driver.findElement(By.xpath("//input[@id='devname']"));
		enter_data_textbox(View_Unique_Name, data[1][1], "View Unique Name");

		//Save
		WebElement Save_button = driver.findElement(By.xpath("//div[@class='pbBottomButtons']//input[@name='save']"));
		click_element(Save_button, "Save button");

		//Error message validation
		error = driver.findElement(By.xpath("//div[contains(text(),'You must enter a value')]"));
		String actual_ErrorMessage = error.getText();
		String expected_ErrorMessage = data[1][4];
		actual_expected_result(actual_ErrorMessage, expected_ErrorMessage, "Error message");
	}

	@Test(priority=7)
	public void CreateNewView_CancelButton() throws IOException, InterruptedException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase31.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("CreateNewView_CancelButton");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		//Create New View
		WebElement Create_New_View = driver.findElement(By.xpath("//a[contains(text(),'Create New View')]"));
		click_element(Create_New_View, "Create New View");

		//View Name
		WebElement View_Name = driver.findElement(By.xpath("//input[@id='fname']"));
		View_Name.clear();
		enter_data_textbox(View_Name, data[1][3], "View Name");

		//View Unique Name
		WebElement View_Unique_Name = driver.findElement(By.xpath("//input[@id='devname']"));
		View_Unique_Name.sendKeys(data[1][4]);
		logger.log(LogStatus.INFO, "Entered view Unique Name");

		//Cancel
		WebElement Cancel_button = driver.findElement(By.xpath("//div[@class='pbBottomButtons']//input[@name='cancel']"));
		click_element(Cancel_button, "Cancel button");

		//Verifying if the correct contacts page is opened or not
		Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		actual_Page_Header = Text.getText();
		expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");

		//View dropdown
		boolean view_present = false;
		WebElement dropdown = driver.findElement(By.name("fcf"));
		click_element(dropdown, "dropdown");
		Select dropdown_options = new Select(dropdown);
		List<WebElement> dropdown_elements = dropdown_options.getOptions();
		for(int i=0; i<dropdown_elements.size(); i++) {
			if(!(dropdown_elements.get(i).getText()).equals(data[1][3])){
				view_present = true;
			}
		}		
		if(view_present == true) {
			logger.log(LogStatus.PASS, data[1][3]+" is not present in the view dropdown");
		}
		else
			logger.log(LogStatus.FAIL, data[1][3]+" is present in the view dropdown");
	}
	
	@Test(priority=8)
	public void New_SaveNew_buttons() throws IOException, InterruptedException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase32.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("New_SaveNew_buttons");

		//Username
		username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		enter_data_textbox(username, data[1][0], "username");

		//Password
		password = driver.findElement(By.id("password"));
		enter_data_textbox(password, data[1][1], "password");

		//Login
		Login = driver.findElement(By.id("Login"));
		click_element(Login, "Login");

		//Contacts
		WebElement Contacts_Tab = driver.findElement(By.xpath("//li[@id='Contact_Tab']"));
		click_element(Contacts_Tab, "Contacts Tab");

		//Popup box
		WebElement popupBox = driver.findElement(By.xpath("//*[@id=\"tryLexDialogX\"]"));
		click_element(popupBox, "popup Box");

		//Verifying if the correct contacts page is opened or not
		WebElement Text = driver.findElement(By.xpath("//h1[@class='pageType']"));
		String actual_Page_Header = Text.getText();
		String expected_Page_Header = data[1][2];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "Contacts Page");
		
		//New button
		WebElement New_Button = driver.findElement(By.xpath("//input[@name='new']"));
		click_element(New_Button, "New button");
		
		//LastName and AccountName
		WebElement LastName = driver.findElement(By.xpath("//input[@id='name_lastcon2']"));
		LastName.clear();
		enter_data_textbox(LastName, data[1][3], "Last Name");
		WebElement AccountName = driver.findElement(By.xpath("//input[@id='con4']"));
		AccountName.clear();
		enter_data_textbox(AccountName, data[1][4], "Account Name");
		
		//Save & New button
		WebElement Save_New = driver.findElement(By.name("save_new"));
		click_element(Save_New, "Save&New button");

		//New Contact Edit Page
		WebElement Header= driver.findElement(By.xpath("//h1[@class='pageType']"));
		actual_Page_Header = Header.getText();
		expected_Page_Header = data[1][5];
		actual_expected_result(actual_Page_Header, expected_Page_Header, "New Contact Edit Page name");
	}
}
