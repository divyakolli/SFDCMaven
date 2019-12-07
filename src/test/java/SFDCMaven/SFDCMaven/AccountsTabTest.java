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

public class AccountsTabTest {
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
		reports = new ExtentReports("C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\ExtentReports\\SFDC_AccountsTab.html", true);		
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
	public void Create_Account() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase10.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Create_Account");

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

	@Test(priority=2)
	public void Create_New_View() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase11.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Create_New_View");

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

	@Test(priority=3)
	public void Edit_View() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase12.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Edit_View");

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
		WebElement Available_Fields_dropdown = driver.findElement(By.xpath("//*[@id='colselector_select_0']"));
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
		if(Element_available = false) {
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


	@Test(priority=4)
	public void Accounts_MergeAccounts() throws IOException, InterruptedException{
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase13.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Accounts_MergeAccounts");

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

	@Test(priority=5)
	public void Create_account_report() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase14.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Create_account_report");

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

		//Date Field dropdown and options
		WebElement dropdown = driver.findElement(By.xpath("//img[@id='ext-gen148']"));
		click_element(dropdown, "dropdown");
		List<WebElement> options= driver.findElements(By.xpath("//div[@class='x-combo-list-item']"));
		for (int i=0; i<options.size(); i++) {
			System.out.println(options.get(i).getText());
			if((options.get(i).getText()).equals(data[1][6])) {	
				options.get(i).click();
				logger.log(LogStatus.INFO, "Selected Created Date from the dropdown");
			}
		}
		
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
}
