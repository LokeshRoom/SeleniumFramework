import driverfactory.Browsers;
import operations.Operation;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import testngrunner.StartTest;

public class Tests {

    public Tests() throws Exception {
    }

    StartTest startTest = new StartTest(Browsers.REMOTE_CHROME, "QA", "Objects.yaml");

    @Test(description = "Launching Google Home page")
    public void google_Search() throws Exception {

        Operation operation = startTest.startTest();
        operation.goToUrl("Google_HomepageURL");
        operation.enterText("txt_SearchBox", "Google_HomePage", "Lokesh Kumar K");
        operation.enterText("txt_SearchBox", "Google_HomePage", Keys.ENTER);
        operation.quit();



    }

    @Test
    public void likedIn_Search() throws Exception {
        Operation operation = startTest.startTest();
        operation.goToUrl("LinkedIN_HomepageURL");
        //operation.click("btn_SearchBox", "LinkedIN_Homepage");
        operation.click("btn_People", "LinkedIN_Homepage");
        operation.enterText("txt_FirstName", "LinkedIN_Homepage", "Lokesh Kumar");
        operation.enterText("txt_LastName", "LinkedIN_Homepage", "K");
        operation.click("btn_SearchPeople", "LinkedIN_Homepage");
        operation.quit();
    }

    @Test
    public void test() {
        String s="aabddc";int N=0;
        for (int i = 1; i <= s.length(); i++) {
            N=N+2;
            if ((N/2)+1<=i && i<=N)
            System.out.println(i);
        }

}
    long substrCount(int n, String st) {
        long iPalCount = n;
        long iSpecialCnt = 0, iCurrCount = 0, iPrevCount = 0, iPrevPrevCount = 0;
           char[] s=st.toCharArray();
        for(int i = 1; i < n; i++) {
            char cPrev = s[i-1];
            char cCurr = s[i];

            if ( cPrev == cCurr ) {
                iCurrCount++;
                iPalCount += iCurrCount;

                if ( iSpecialCnt > 0 ) {
                    iSpecialCnt--;
                    iPalCount++;
                }
            }
            else {
                iCurrCount = 0;
                if ( i > 1 && (s[i-2] == cCurr) ) {
                    iSpecialCnt = iPrevPrevCount;
                    iPalCount++;
                }
                else {
                    iSpecialCnt = 0;
                }
            }

            if ( i > 1 ) {
                iPrevPrevCount = iPrevCount;
            }

            iPrevCount = iCurrCount;
        }

        return iPalCount;
    }}
