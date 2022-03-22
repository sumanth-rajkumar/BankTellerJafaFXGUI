/**
 * This class is what controls all the actions for each button using the inputs given by the
 * user in the GUI
 * @author Sumanth Rajkumar, Shantanu Jain
 */
package edu.rutgers.sumanth.cs213;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.time.format.DateTimeFormatter;


public class BankTellerController {

    public AccountDatabase accountDatabase = new AccountDatabase();
    @FXML
    private ToggleGroup oc_accountType, dw_accountType;
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
    private RadioButton oc_checking, oc_collegeC, oc_savings, oc_moneyMarket;
    @FXML
    private RadioButton dw_checking, dw_collegeC, dw_savings, dw_moneyMarket;

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
     * This method is a helper that checks if the first and last name have any numbers
     * @param s - String that will be checked to see if there are numbers
     * @return true if there are numbers, false otherwise
     */
    private boolean checkIfFirstAndLastNameHaveNumbers(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            if(s.charAt(i) >= '0' && s.charAt(i) <= '9')
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    /**
     * This function is a helper that checks if the user's entered first and last name
     * are valid
     * @param forOpen - a boolean that is true if it's the case of opening an account
     */
    private boolean validateFirstLastNames(boolean isOpenClose, boolean forOpen)
    {

        TextField firstName = isOpenClose ? oc_firstName : dw_firstName;
        TextField lastName = isOpenClose ? oc_lastName : dw_lastName;

        if(firstName.getText() == null || firstName.getText().isBlank())
        {
            output.setText("First Name cannot be blank!");
            return false;
        }
        if(checkIfFirstAndLastNameHaveNumbers(firstName.getText()))
        {
            output.setText("First Name cannot have numbers!");
            return false;
        }
        if(lastName.getText() == null || lastName.getText().isBlank())
        {
            output.setText("Last Name cannot be blank!");
            return false;
        }
        if(checkIfFirstAndLastNameHaveNumbers(lastName.getText()))
        {
            output.setText("Last Name cannot have numbers!");
            return false;
        }
        return true;
    }

    /**
     * This function is a helper that checks if the user's entered dob is
     * in the future or not.
     * @param forOpen - a boolean that is true if it's the case of opening an account
     */
    private Date validateAndParseDOB(boolean isOpenClose, boolean forOpen)
    {

        DatePicker dob = isOpenClose ? oc_dob : dw_dob;
        try
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            Date dOb = new Date(formatter.format(dob.getValue()));
            if(dOb.isInTheFuture())
            {
                output.setText("Date of birth invalid, it's a future date.");
                return null;
            }
            return dOb;
        }
        catch(Exception e)
        {
            output.setText("Date of birth can't be blank.");
            return null;
        }
    }

    /**
     * This function is a helper that populates the account object with the entered profile info in the GUI
     * @param forOpen - a boolean that is true if it's the case of opening an account
     * @param isOpenClose - a boolean that is true for the case of opening or closing an account
     * @param account - account being populated
     */
    private boolean populateHolder(boolean isOpenClose, boolean forOpen, Account account)
    {
        if(!validateFirstLastNames(isOpenClose, forOpen)) {
            return false;
        }

        Date dob = validateAndParseDOB(isOpenClose, forOpen);
        if(dob==null){
            return false;
        }
        account.setHolder(oc_firstName.getText(), oc_lastName.getText(), dob);
        return true;
    }

    /**
     * This function is a helper that populates the account object with
     * the entered profile info and initial deposit info from the GUI
     * @param account - account being populated.
     * @param isOpenClose - a boolean that is true for the case of opening or closing an account
     * @param errorMessage - String that represents the error message to give at a certain exception
     */
    private boolean populateHolderAndBalance(boolean isOpenClose, Account account, String errorMessage)
    {
        if(!populateHolder(isOpenClose,true, account))
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
            output.setText("Amount can't be blank or invalid.");
            return false;
        }
        if(balance <= 0d)
        {
            output.setText(errorMessage);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        if(!populateHolderAndBalance(true, newAccount,"Initial deposit cannot be 0 or negative."))
        {
            return;
        }

        Account existing = accountDatabase.getAccountIfExists(newAccount);
        if(existing!=null)
        {
            if(!existing.isClosed() || !existing.getType().equals(newAccount.getType()))
            {
                output.setText(oc_firstName.getText() + " " + oc_lastName.getText() + " " + formatter.format(oc_dob.getValue()) + " " + " same account(type) is in the database.");
            }
            else
            {
                accountDatabase.reOpen(newAccount);
                output.setText("Account reopened.");
            }
        }
        else
        {
            if(newAccount instanceof  MoneyMarket)
            {
                MoneyMarket moneyMarket = (MoneyMarket) newAccount;
                if(!moneyMarket.hasMinimumInitialDeposit())
                {
                    output.setText("Minimum of $" + MoneyMarket.ExpectedBalance + " to open a MoneyMarket account.");
                    return;
                }

            }
            accountDatabase.open(newAccount);
            output.setText("Account opened.");
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
            default -> output.setText("Invalid Account Type");
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

        if(!newark.isSelected() && !nB.isSelected() && !camden.isSelected())
        {
            output.setText("Choose a college campus");
        }
        else
        {
            CollegeChecking collegeChecking = new CollegeChecking();
            checkExistingAndOpenAccount(collegeChecking);
        }
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
            default -> output.setText("Invalid Account Type");
        }

    }

    /**
     * This function is a helper that checks if account exists or already closed
     * before closing an account.
     * @param account - account being closed
     */
    private void closeExistingAccount(Account account)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        if(!populateHolder(true,false, account))
        {
            return;
        }
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing!=null)
        {
            if(!existing.isClosed())
            {
                accountDatabase.close(existing);
                output.setText("Account closed.");
            }
            else
            {
                output.setText("Account is closed already.");
            }
        }
        else
        {
            output.setText(oc_firstName.getText() + " " + oc_lastName.getText() + " " + formatter.format(oc_dob.getValue()) + " " + account.getShortType() + " is not in the database.");
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
        if(!newark.isSelected() && !nB.isSelected() && !camden.isSelected()){
            output.setText("Choose a college campus");
        }
        else {
            CollegeChecking ClgCheck = new CollegeChecking();
            closeExistingAccount(ClgCheck);
        }
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
            default -> output.setText("Invalid Account Type");
        }
    }

    /**
     * This function is a helper that checks if account exists or closed before depositing an amount to an account.
     * @param account - account being deposited to.
     */
    private void depositToAccount(Account account)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        if(!populateHolderAndBalance(false, account,"Deposit - amount cannot be 0 or negative."))
        {
            return;
        }
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing!=null && existing.getType().equals(account.getType()))
        {
            if(!existing.isClosed())
            {
                accountDatabase.deposit(account);
                output.setText("Deposit - balance updated.");
            }
            else
            {
                output.setText("Account is closed already.");
            }
        }
        else
        {
            output.setText(dw_firstName.getText() + " " + dw_lastName.getText() + " " + formatter.format(dw_dob.getValue()) + " " + account.getShortType() + " is not in the database.");
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
            default -> output.setText("Invalid Account Type");
        }
    }

    /**
     * This function is a helper that checks if account exists or closed and if
     * the amount being withdrawn is sufficient before withdrawing an amount from an account.
     * @param account - account being withdrawn.
     */
    private void withDrawFromAccount(Account account)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        if(!populateHolderAndBalance(false, account, "Withdraw - amount cannot be 0 or negative."))
        {
            return;
        }
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing!=null && existing.getType().equals(account.getType()))
        {
            if(!existing.isClosed())
            {
                if(accountDatabase.withdraw(account))
                {
                    output.setText("Withdraw - balance updated.");
                }
                else
                {
                    output.setText("Withdraw - insufficient fund.");
                }
            }
            else
            {
                output.setText("Account is closed already.");
            }
        }
        else
        {
            output.setText(dw_firstName.getText() + " " + dw_lastName.getText() + " " + formatter.format(dw_dob.getValue()) + " " + account.getShortType() + " is not in the database.");
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
            output.setText(accountDatabase.print());
        }
        else
        {
            output.setText("Account Database is empty!");
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
            output.setText(accountDatabase.printByAccountType());
        }
        else
        {
            output.setText("Account Database is empty!");
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
            output.setText(accountDatabase.printFeeAndInterest());
        }
        else
        {
            output.setText("Account Database is empty!");
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
            output.setText(accountDatabase.printWithUpdatedBalance());

        }
        else
        {
            output.setText("Account Database is empty!");
        }
    }
    
}

