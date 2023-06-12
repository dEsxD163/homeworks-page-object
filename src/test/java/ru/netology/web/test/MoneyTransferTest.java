package ru.netology.web.test;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPageV1;


import static com.codeborne.selenide.Selenide.open;

public class MoneyTransferTest {
  DashboardPage dashboardPage;

  @BeforeEach
  public void setUp() {
    open("http://localhost:9999");
    var loginPage = new LoginPageV1();
    var authInfo = DataHelper.getAuthInfo();
    var verificationPage = loginPage.validLogin(authInfo);
    var verificationCode = DataHelper.getValidCode(authInfo);
    dashboardPage = verificationPage.validVerify(verificationCode);
    balance(dashboardPage);
  }

  public void balance(DashboardPage dashboardPage) {
    int initialBalance = dashboardPage.getFirstCardBalance();
    int depositBalance = 10_000 - initialBalance;
    if (depositBalance > 0) {
      dashboardPage.topUpFirstCard().bankContribution(depositBalance, DataHelper.secondCardNumber());
    } else if (depositBalance < 0) {
      dashboardPage.topUpSecondCard().bankContribution(depositBalance, DataHelper.firstCardNumber());
    }
  }

  @Test
  void validTransferCard1() {
    int actual1 = dashboardPage.topUpFirstCard().bankContribution(4_000, DataHelper.secondCardNumber()).getFirstCardBalance();
    Assertions.assertEquals(14_000, actual1);
    int actual2 = dashboardPage.getSecondCardBalance();
    Assertions.assertEquals(6_000, actual2);
  }

  @Test
  void validTransferCard2() {
    int actual1 = dashboardPage.topUpSecondCard().bankContribution(6_000, DataHelper.firstCardNumber()).getSecondCardBalance();
    Assertions.assertEquals(16_000, actual1);
    int actual2 = dashboardPage.getFirstCardBalance();
    Assertions.assertEquals(4_000, actual2);
  }

  @Test
  void validTransfer999() {
    int actual1 = dashboardPage.topUpSecondCard().bankContribution(1, DataHelper.firstCardNumber()).getSecondCardBalance();
    Assertions.assertEquals(10_001, actual1);
    int actual2 = dashboardPage.getFirstCardBalance();
    Assertions.assertEquals(9_999, actual2);
  }

  @Test
  void validTransfer1() {
    int actual1 = dashboardPage.topUpSecondCard().bankContribution(9_999, DataHelper.firstCardNumber()).getSecondCardBalance();
    Assertions.assertEquals(19_999, actual1);
    int actual2 = dashboardPage.getFirstCardBalance();
    Assertions.assertEquals(1, actual2);
  }

  @Test
  void validTransfer0() {
    int actual1 = dashboardPage.topUpSecondCard().bankContribution(0, DataHelper.firstCardNumber()).getSecondCardBalance();
    Assertions.assertEquals(10_000, actual1);
    int actual2 = dashboardPage.getFirstCardBalance();
    Assertions.assertEquals(10_000, actual2);
  }

  @Test
  void validTransferAll() {
    int actual1 = dashboardPage.topUpSecondCard().bankContribution(10_000, DataHelper.firstCardNumber()).getSecondCardBalance();
    Assertions.assertEquals(20_000, actual1);
    int actual2 = dashboardPage.getFirstCardBalance();
    Assertions.assertEquals(0, actual2);
  }

  @Test
  void notAValidTransfer() {
    int actual1 = dashboardPage.topUpSecondCard().bankContribution(17_000, DataHelper.firstCardNumber()).getSecondCardBalance();
    Assertions.assertEquals(10_000, actual1);
    int actual2 = dashboardPage.getFirstCardBalance();
    Assertions.assertEquals(10_000, actual2);
  }
}