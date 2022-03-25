package javafxUI;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;


/**
 * This class is what controls all the actions for each button using the inputs given by the
 * user in the GUI
 * @author Sumanth Rajkumar, Shantanu Jain
 */
public class BankTellerController {

    public static final String MISSING_OPENING_DATA = "Missing data for opening an account.";
    public static final String MISSING_CLOSING_DATA = "Missing data for closing an account.";
    public static final String MISSING_DEPOSIT_DATA = "Missing data for depositing to an account.";
    public static final String MISSING_WITHDRAW_DATA = "Missing data for withdrawing from an account.";
    public AccountDatabase accountDatabase = new AccountDatabase();
    @FXML
    private ToggleGroup oc_accountType, dw_accountType, collegeChecking;
    @FXML
    private TextArea output;
    @FXML
    private RadioButton camden, nB, newark;
    @FXML
    private TextField deposit, oc_firstName, oc_lastName;
    @FXML
    private TextField amount, dw_firstName, dw_lastName;
    @FXML
    private DatePicker oc_dob, dw_dob;
    @FXML
    private CheckBox loyal;
    @FXML
    private RadioButton oc_collegeC, oc_savings;


    /**
     * This method corresponds to the radio buttons in the open/close tab
     * of the GUI. Only when selecting the College Checking button, is
     * the college campus radio buttons are supposed to be available.
     * Only when selecting the Savings button, is the loyal customer
     * checkbox supposed to be available.
     */
    @FXML
    void selected()
    {
        if(oc_collegeC.isSelected())
        {
            camden.setDisable(false);
            nB.setDisable(false);
            newark.setDisable(false);
        }
        else
        {
            camden.setDisable(true);
            nB.setDisable(true);
            newark.setDisable(true);
        }
        loyal.setDisable(!oc_savings.isSelected());
    }

    /**
     * This function is a helper that checks if the user's entered first and last name
     * are valid
     * @param forOpen - a boolean that is true if it's the case of opening an account
     * @param forDeposit - a boolean that is true for the case of depositing to an account
     * @param isOpenClose - a boolean that is true for the case of opening or closing an account
     * @return - true if first name and last name are valid, false otherwise
     */
    private boolean validateFirstLastNames(boolean isOpenClose, boolean forOpen, boolean forDeposit)
    {

        TextField firstName = isOpenClose ? oc_firstName : dw_firstName;
        TextField lastName = isOpenClose ? oc_lastName : dw_lastName;

        if(firstName.getText() == null || firstName.getText().isBlank() || lastName.getText() == null || lastName.getText().isBlank())
        {
            if(isOpenClose)
            {
                output.appendText(forOpen ? MISSING_OPENING_DATA + "\n" : MISSING_CLOSING_DATA + "\n");
            }
            else
            {
                output.appendText(forDeposit ? MISSING_DEPOSIT_DATA + "\n" : MISSING_WITHDRAW_DATA + "\n");
            }
            return false;
        }
        return true;
    }

    /**
     * This function is a helper that checks if the user's entered dob is
     * in the future or not.
     * @param isOpenClose - a boolean that is true for the case of opening or closing an account
     * @return - date object if passes conditions, null if it doesn't
     */
    private Date validateAndParseDOB(boolean isOpenClose)
    {

        try
        {
            DatePicker dob = isOpenClose ? oc_dob : dw_dob;
            Date dOb = new Date(dob.getValue().toString());
            if(dOb.isInTheFuture())
            {
                output.appendText("Date of birth invalid, it's a future date." + "\n");
                return null;
            }
            return dOb;
        }
        catch(NullPointerException e)
        {
            output.appendText("Missing Date of Birth." + "\n");
            return null;
        }

    }

    /**
     * This function is a helper that populates the account object with the entered profile info in the GUI
     * @param forOpen - a boolean that is true if it's the case of opening an account
     * @param isOpenClose - a boolean that is true for the case of opening or closing an account
     * @param forDeposit - a boolean that is true for the case of depositing to an account
     * @param account - account being populated
     * @return - true if account object is successfully populated with holder info, false otherwise
     */
    private boolean populateHolder(boolean isOpenClose, boolean forOpen, boolean forDeposit, Account account)
    {
        if(!validateFirstLastNames(isOpenClose, forOpen, forDeposit))
        {
            return false;
        }
        Date dob = validateAndParseDOB(isOpenClose);
        if(dob == null)
        {
            return false;
        }
        if(isOpenClose)
        {
            account.setHolder(oc_firstName.getText().trim(), oc_lastName.getText().trim(), dob);
        }
        else
        {
            account.setHolder(dw_firstName.getText().trim(), dw_lastName.getText().trim(), dob);
        }
        return true;
    }

    /**
     * This function is a helper that populates the account object with
     * the entered profile info and initial deposit info from the GUI
     * @param account - account being populated.
     * @param isOpenClose - a boolean that is true for the case of opening or closing an account
     * @param errorMessage - String that represents the error message to give at a certain exception
     * @return - true if account object is successfully populated with holder and balance info, false
     * otherwise
     */
    private boolean populateHolderAndBalance(boolean isOpenClose, Account account, String errorMessage)
    {
        if(!populateHolder(isOpenClose,true, false, account))
        {
            return false;
        }

        double balance;
        TextField amt = isOpenClose ? deposit : amount;
        try
        {
            balance = Double.parseDouble(amt.getText());
        }
        catch (Exception e)
        {
            output.appendText("Amount can't be blank or invalid." + "\n");
            return false;
        }
        if(balance <= 0d)
        {
            output.appendText(errorMessage + "\n");
            return false;
        }
        account.setBalance(balance);
        return true;
    }



    /**
     * This function is a helper that checks if account exists or closed before opening an account.
     * @param newAccount - account being opened.
     */
    private void checkExistingAndOpenAccount(Account newAccount)
    {

        if(!populateHolderAndBalance(true, newAccount,"Initial deposit cannot be 0 or negative."))
        {
            return;
        }
        Date dob = new Date(oc_dob.getValue().toString());
        Account existing = accountDatabase.getAccountIfExists(newAccount);
        if(existing != null)
        {
            if(!existing.isClosed() || !existing.getType().equals(newAccount.getType()))
            {
                output.appendText(oc_firstName.getText() + " " + oc_lastName.getText() + " " + dob + " " + " same account(type) is in the database." + "\n");
            }
            else
            {
                accountDatabase.reOpen(newAccount);
                output.appendText("Account reopened." + "\n");
            }
        }
        else
        {
            if(newAccount instanceof MoneyMarket)
            {
                MoneyMarket moneyMarket = (MoneyMarket) newAccount;
                if(!moneyMarket.hasMinimumInitialDeposit())
                {
                    output.appendText("Minimum of $" + MoneyMarket.ExpectedBalance + " to open a MoneyMarket account." + "\n");
                    return;
                }

            }
            accountDatabase.open(newAccount);
            output.appendText("Account opened." + "\n");
        }
    }

    /**
     * This method corresponds to the open button in the GUI. Opens an account when
     * button is clicked depending on the user's entered input.
     */
    @FXML
    void open()
    {
        RadioButton selectedRadioButton = (RadioButton) oc_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId())
        {
            case "C" -> caseCheckingForOpen();
            case "CC" -> caseCollegeCheckingForOpen();
            case "S" -> caseSavingsForOpen();
            case "MM" -> caseMoneyMarketForOpen();
            default -> output.appendText("Invalid Account Type" + "\n");
        }
    }

    /**
     * This function is used to open a Checking account.
     */
    private void caseCheckingForOpen()
    {
        Checking checking = new Checking();
        checkExistingAndOpenAccount(checking);
    }

    /**
     * This function is used to open a College Checking account.
     */
    private void caseCollegeCheckingForOpen()
    {
        RadioButton selectedRadioButton = (RadioButton) collegeChecking.getSelectedToggle();
        College college = College.valueOf(selectedRadioButton.getId());
        CollegeChecking collegeChecking = new CollegeChecking(college);
        checkExistingAndOpenAccount(collegeChecking);

    }

    /**
     * This function is used to open a Savings account.
     */
    private void caseSavingsForOpen()
    {
        boolean loyalty;
        Savings savings = new Savings();
        loyalty = loyal.isSelected();
        savings.setLoyalty(loyalty);
        checkExistingAndOpenAccount(savings);
    }

    /**
     * This function is used to open a Money Market account.
     */
    private void caseMoneyMarketForOpen()
    {
        MoneyMarket savings = new MoneyMarket();
        checkExistingAndOpenAccount(savings);

    }

    /**
     * This function corresponds to the close button in the GUI. Closes an account when
     * button is clicked depending on the user's entered input.
     */
    @FXML
    void close()
    {
        RadioButton selectedRadioButton = (RadioButton) oc_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId())
        {
            case "C" -> caseCheckingForClose();
            case "CC" -> caseCollegeCheckingForClose();
            case "S" -> caseSavingsForClose();
            case "MM" -> caseMoneyMarketForClose();
            default -> output.appendText("Invalid Account Type" + "\n");
        }

    }

    /**
     * This function is a helper that checks if account exists or already closed
     * before closing an account.
     * @param account - account being closed
     */
    private void closeExistingAccount(Account account)
    {


        if(!populateHolder(true,false, false, account))
        {
            return;
        }
        Date dob = new Date(oc_dob.getValue().toString());
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing != null && existing.getType().equals(account.getType()))
        {
            if(!existing.isClosed())
            {
                accountDatabase.close(existing);
                output.appendText("Account closed."+ "\n");
            }
            else
            {
                output.appendText("Account is closed already." + "\n");
            }
        }
        else
        {
            output.appendText(oc_firstName.getText() + " " + oc_lastName.getText() + " " + dob + " " + account.getShortType() + " is not in the database." + "\n");
        }

    }

    /**
     * This function is used to close a Checking account.
     */
    private void caseCheckingForClose()
    {
        Checking checking = new Checking();
        closeExistingAccount(checking);
    }

    /**
     * This function is used to close a College Checking account.
     */
    private void caseCollegeCheckingForClose()
    {
        CollegeChecking ClgCheck = new CollegeChecking();
        closeExistingAccount(ClgCheck);

    }

    /**
     * This function is used to close a Savings account.
     */
    private void caseSavingsForClose()
    {
        Savings savings = new Savings();
        closeExistingAccount(savings);
    }

    /**
     * This function is used to close a Money Market account.
     */
    private void caseMoneyMarketForClose()
    {
        MoneyMarket moneyMarket = new MoneyMarket();
        closeExistingAccount(moneyMarket);
    }

    /**
     * This function corresponds to the deposit button in the GUI. Deposits to an account when
     * button is clicked depending on the user's entered input.
     */
    @FXML
    void deposit()
    {
        RadioButton selectedRadioButton = (RadioButton) dw_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId())
        {
            case "C" -> caseCheckingForDeposit();
            case "CC" -> caseCollegeCheckingForDeposit();
            case "S" -> caseSavingsForDeposit();
            case "MM" -> caseMoneyMarketForDeposit();
            default -> output.appendText("Invalid Account Type" + "\n");
        }
    }

    /**
     * This function is a helper that checks if account exists or closed before depositing an amount to an account.
     * @param account - account being deposited to.
     */
    private void depositToAccount(Account account)
    {


        if(!populateHolder(false, false, true, account))
        {
            return;
        }
        if(!populateHolderAndBalance(false, account,"Deposit - amount cannot be 0 or negative."))
        {
            return;
        }
        Date dob = new Date(dw_dob.getValue().toString());
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing != null && existing.getType().equals(account.getType()))
        {
            if(!existing.isClosed())
            {
                accountDatabase.deposit(account);
                output.appendText("Deposit - balance updated." + "\n");
            }
            else
            {
                output.appendText("Cannot deposit into a closed account." + "\n");
            }
        }
        else
        {
            output.appendText(dw_firstName.getText() + " " + dw_lastName.getText() + " " + dob + " " + account.getShortType() + " is not in the database." + "\n");
        }

    }

    /**
     * This function is used to deposit to a College Checking account.
     */
    private void caseCollegeCheckingForDeposit()
    {
        CollegeChecking clgCheck = new CollegeChecking();
        depositToAccount(clgCheck);

    }

    /**
     * This function is used to deposit to a Checking account.
     */
    private void caseCheckingForDeposit()
    {
        Checking checking = new Checking();
        depositToAccount(checking);
    }

    /**
     * This function is used to deposit to a Savings account.
     */
    private void caseSavingsForDeposit()
    {
        Savings savings = new Savings();
        depositToAccount(savings);
    }

    /**
     * This function is used to deposit to a Money Market account.
     */
    private void caseMoneyMarketForDeposit()
    {
        MoneyMarket moneyMarket = new MoneyMarket();
        depositToAccount(moneyMarket);
    }

    /**
     * This function corresponds to the withdraw button in the GUI. Withdraws from an account when
     * button is clicked depending on the user's entered input.
     */
    @FXML
    void withdraw()
    {
        RadioButton selectedRadioButton = (RadioButton) dw_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId())
        {
            case "C" -> caseCheckingForWithdraw();
            case "CC" -> caseCollegeCheckingForWithdraw();
            case "S" -> caseSavingsForWithdraw();
            case "MM" -> caseMoneyMarketForWithdraw();
            default -> output.appendText("Invalid Account Type" + "\n");
        }
    }

    /**
     * This function is a helper that checks if account exists or closed and if
     * the amount being withdrawn is sufficient before withdrawing an amount from an account.
     * @param account - account being withdrawn.
     */
    private void withDrawFromAccount(Account account)
    {


        if(!populateHolderAndBalance(false, account, "Withdraw - amount cannot be 0 or negative."))
        {
            return;
        }
        Date dob = new Date(dw_dob.getValue().toString());
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing != null && existing.getType().equals(account.getType()))
        {
            if(!existing.isClosed())
            {
                if(accountDatabase.withdraw(account))
                {
                    output.appendText("Withdraw - balance updated." + "\n");
                }
                else
                {
                    output.appendText("Withdraw - insufficient fund." + "\n");
                }
            }
            else
            {
                output.appendText("Cannot withdraw from a closed account." + "\n");
            }
        }
        else
        {
            output.appendText(dw_firstName.getText() + " " + dw_lastName.getText() + " " + dob + " " + account.getShortType() + " is not in the database." + "\n");
        }

    }

    /**
     * This function is used to withdraw an amount from a Checking account.
     */
    private void caseCheckingForWithdraw()
    {
        Checking checking = new Checking();
        withDrawFromAccount(checking);

    }

    /**
     * This function is used to withdraw an amount from a College Checking account.
     */
    private void caseCollegeCheckingForWithdraw()
    {
        CollegeChecking clgCheck = new CollegeChecking();
        withDrawFromAccount(clgCheck);
    }

    /**
     * This function is used to withdraw an amount from a Savings account.
     */
    private void caseSavingsForWithdraw()
    {
        Savings savings = new Savings();
        withDrawFromAccount(savings);
    }

    /**
     * This function is used to withdraw an amount from a Money Market account.
     */
    private void caseMoneyMarketForWithdraw()
    {
        MoneyMarket moneyMarket = new MoneyMarket();
        withDrawFromAccount(moneyMarket);
    }

    /**
     * This function corresponds to the print all accounts button in the GUI. Prints all the accounts in
     * the database in the order they were added in when button is clicked.
     */
    @FXML
    void print()
    {
        if(accountDatabase.getNumAcct() > 0)
        {
            output.appendText("\n" + "*list of accounts in the database*" + "\n" + accountDatabase.print() + "*end of list*" + "\n\n");
        }
        else
        {
            output.appendText("Account Database is empty!" + "\n");
        }
    }

    /**
     * This function corresponds to the print all accounts by types button in the GUI. Prints all the accounts in
     * the database by their account type alphabetically when button is clicked.
     */
    @FXML
    void printByAccountType()
    {
        if(accountDatabase.getNumAcct() > 0)
        {
            output.appendText("\n" + "*list of accounts by account type." + "\n" + accountDatabase.printByAccountType() + "*end of list." + "\n\n");
        }
        else
        {
            output.appendText("Account Database is empty!" + "\n");
        }
    }

    /**
     * This function corresponds to the calculate interest and fees button in the GUI. Prints all the accounts in
     * the database with the calculated fees and interest rate when button is clicked.
     */
    @FXML
    void printWithFeeAndInterest()
    {
        if(accountDatabase.getNumAcct() > 0)
        {
            output.appendText("\n" + "*list of accounts with fee and monthly interest" + "\n" + accountDatabase.printFeeAndInterest() + "*end of list." + "\n\n");
        }
        else
        {
            output.appendText("Account Database is empty!" + "\n");
        }
    }

    /**
     * This function corresponds to the apply interests and fees button in the GUI. Prints all the accounts in
     * the database with the updated balance from the calculated fees, interest rate, deposits, withdrawals, and closures
     * when button is clicked.
     */
    @FXML
    void updateBalance()
    {
        if(accountDatabase.getNumAcct() > 0)
        {
            output.appendText("\n" + "*list of accounts with updated balance" + "\n" + accountDatabase.printWithUpdatedBalance() + "*end of list." + "\n\n");

        }
        else
        {
            output.appendText("Account Database is empty!" + "\n");
        }
    }

}

