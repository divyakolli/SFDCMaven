package SFDCMaven.SFDCMaven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
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

public class OpportunityTabTest {
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
		reports = new ExtentReports("C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\ExtentReports\\SFDC_OpportunityTab.html", true);		
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

	@Test(priority=1)
	public void SelectUsermenu_Username_dropdown() throws IOException, InterruptedException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase15.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("SelectUsermenu_Username_dropdown");

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

	@Test(priority=2)
	public void Create_newOpportunity() throws InterruptedException, IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase16.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("Create_newOpportunity");

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

	@Test(priority=3)
	public void TestOpportunity_Pipeline_Report() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase17.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("TestOpportunity_Pipeline_Report");

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
		List<WebElement> NumberOf_Rows = driver.findElements(By.xpath("//table[@class='reportTable tabularReportTable']/tbody/tr"));
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

	@Test(priority=4)
	public void TestStuck_Opportunity_Report() throws IOException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase18.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("TestStuck_Opportunity_Report");

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
		List<WebElement> NumberOf_Rows = driver.findElements(By.xpath("//table[@class='reportTable tabularReportTable']/tbody/tr"));
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

	@Test(priority=5)
	public void TestQuarterly_Summary_Report() throws IOException, ParseException {
		String path = "C:\\Users\\divya\\OneDrive\\Desktop\\Selenium\\TestCases Excel\\Testcase19.xls";

		String[][] data = readExcel(path,"Sheet1");

		logger = reports.startTest("TestQuarterly_Summary_Report");

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
}
