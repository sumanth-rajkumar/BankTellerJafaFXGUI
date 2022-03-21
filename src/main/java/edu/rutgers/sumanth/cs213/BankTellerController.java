package edu.rutgers.sumanth.cs213;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDate;


public class BankTellerController {
    public static final String missingOpeningData = "Missing data for opening an account.";
    public static final String missingClosingData = "Missing data for closing an account.";
    public AccountDatabase accountDatabase = new AccountDatabase();

    @FXML
    private ToggleGroup oc_accountType;

    @FXML
    private ToggleGroup dw_accountType;

    @FXML
    private TextArea output;

    @FXML
    private RadioButton camden;
    
    @FXML
    private TextField deposit, oc_firstName, oc_lastName;

    @FXML
    private TextField amount, dw_firstName, dw_lastName;

    @FXML
    private DatePicker oc_dob, dw_dob;

    @FXML
    private RadioButton nB;

    @FXML
    private RadioButton newark;

    @FXML
    private CheckBox loyal;

    @FXML
    private RadioButton oc_checking, oc_collegeC, oc_savings, oc_moneyMarket;
    @FXML
    private RadioButton dw_checking, dw_collegeC, dw_savings, dw_moneyMarket;

    @FXML
    void selected() {
        if(oc_collegeC.isSelected())
        {
            camden.setDisable(false);
            nB.setDisable(false);
            newark.setDisable(false);
        }
        else {
            camden.setDisable(true);
            nB.setDisable(true);
            newark.setDisable(true);
        }
        loyal.setDisable(!oc_savings.isSelected());
    }

    /**
     * This function is a helper that checks if the user enters info only until last name
     * @param forOpen - a boolean that says if it's the case of opening and account
     */
    private boolean validateFirstLastNames(boolean isOpenClose, boolean forOpen){

        TextField firstName = isOpenClose ? oc_firstName : dw_firstName;
        TextField lastName = isOpenClose ? oc_lastName : dw_lastName;

        if(firstName.getText()==null || firstName.getText().isBlank()){
            output.setText("First Name cannot be blank!");
            return false;
        }
        if(lastName.getText()==null || lastName.getText().isBlank()){
            output.setText("Last Name cannot be blank!");
            return false;
        }
        return true;
    }

    /**
     * This function is a helper that checks if the user enters info only until dob
     * and validates the dob
     * @param forOpen - a boolean that says if it's the case of opening and account
     */
    private Date validateAndParseDOB(boolean isOpenClose, boolean forOpen){

        DatePicker dob = isOpenClose?oc_dob:dw_dob;

        try {
            LocalDate ldate = dob.getValue();
            Date dOb = new Date(ldate.getYear(), ldate.getMonth().getValue(), ldate.getDayOfMonth());
            if(dOb.isInTheFuture())
            {
                output.setText("Date of birth invalid.");
                return null;
            }
            return dOb;
        }
        catch(Exception e){
            output.setText("Date of birth invalid.");
            return null;
        }
    }

    /**
     * This function is a helper that populates the account object with given profile info
     * @param forOpen - a boolean that says if it's the case of opening and account
     * @param account - account being populated
     */
    private boolean populateHolder(boolean isOpenClose, boolean forOpen, Account account)
    {
        if(!validateFirstLastNames(isOpenClose,forOpen)) {
            return false;
        }

        Date dob = validateAndParseDOB(isOpenClose,forOpen);
        if(dob==null){
            return false;
        }
        account.setHolder(oc_firstName.getText(), oc_lastName.getText(), dob);
        return true;
    }

    /**
     * This function is a helper that populates the account object with
     * given profile info and initial deposit info
     * @param account - account being populated.
     */
    private boolean populateHolderAndBalance(boolean isOpenClose, Account account, String errorMessage)
    {
        if(!populateHolder(isOpenClose,true, account)){
            return false;
        }

        double balance;
        TextField amt = isOpenClose ? deposit : amount;
        try {
            balance = Double.parseDouble(amt.getText());
        }catch (Exception e){
            output.setText("Not a valid amount.");
            return false;
        }
        if(balance <= 0d){
            output.setText(errorMessage);
            return false;
        }
        account.setBalance(balance);
        return true;
    }

    /**
     * This function is a helper that checks if account exists or closed
     * before opening an account.
     * @param newAccount - account being deposited to.
     */
    private void checkExistingAndOpenAccount(Account newAccount){
        if(!populateHolderAndBalance(true,newAccount,"Initial deposit cannot be 0 or negative.")){
            return;
        }

        Account existing = accountDatabase.getAccountIfExists(newAccount);
        if(existing!=null) {
            if(!existing.isClosed() || !existing.getType().equals(newAccount.getType())){
                output.setText(oc_firstName.getText() + " " + oc_lastName.getText() + " " + oc_dob.getValue() + " " + " same account(type) is in the database.");
            }else{
                accountDatabase.reOpen(newAccount);
                output.setText("Account reopened.");
            }
        }else {
            if(newAccount instanceof  MoneyMarket){
                MoneyMarket moneyMarket = (MoneyMarket) newAccount;
                if(!moneyMarket.hasMinimumInitialDeposit()){
                    output.setText("Minimum of $" + MoneyMarket.ExpectedBalance + " to open a MoneyMarket account.");
                    return;
                }

            }
            accountDatabase.open(newAccount);
            output.setText("Account opened.");
        }
    }

    @FXML
    void open() {
        RadioButton selectedRadioButton = (RadioButton) oc_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId()) {
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

        if(!newark.isSelected() && !nB.isSelected() && !camden.isSelected()){
            output.setText("Choose a college campus");
        }
        else {

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
     * This function is used to close an account.
     */
    @FXML
    void close()
    {
        RadioButton selectedRadioButton = (RadioButton) oc_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId()) {
            case "C" -> caseCheckingForClose();
            case "CC" -> caseCollegeCheckingForClose();
            case "S" -> caseSavingsForClose();
            case "MM" -> caseMoneyMarketForClose();
            default -> output.setText("Invalid Account Type");
        }

    }

    /**
     * This function is a helper that checks if account exists or closed
     * before closing an account.
     * @param account - account being deposited to.
     */
    private void closeExistingAccount(Account account){
        if(!populateHolder(true,false, account)){
            return;
        }
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing!=null) {
            if(!existing.isClosed()){
                accountDatabase.close(existing);
                output.setText("Account closed.");
            }else{
                output.setText("Account is closed already.");
            }
        }else{
            output.setText(oc_firstName.getText() + " " + oc_lastName.getText() + " " + oc_dob.getValue() + " " + account.getShortType() + " is not in the database.");
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
     * This function is used to deposit to an account.
     */
    @FXML
    void deposit()
    {
        RadioButton selectedRadioButton = (RadioButton) dw_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId()) {
            case "C" -> caseCheckingForDeposit();
            case "CC" -> caseCollegeCheckingForDeposit();
            case "S" -> caseSavingsForDeposit();
            case "MM" -> caseMoneyMarketForDeposit();
            default -> output.setText("Invalid Account Type");
        }
    }

    /**
     * This function is a helper that checks if account exists or closed
     * before depositing an amount to an account.
     * @param account - account being deposited to.
     */
    private void depositToAccount(Account account){
        if(!populateHolderAndBalance(false,account,"Deposit - amount cannot be 0 or negative.")){
            return;
        }
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing!=null && existing.getType().equals(account.getType())) {
            if(!existing.isClosed()){
                accountDatabase.deposit(account);
                output.setText("Deposit - balance updated.");
            }else{
                output.setText("Account is closed already.");
            }
        }else{
            output.setText(dw_firstName.getText() + " " + dw_lastName.getText() + " " + dw_dob.getValue() + " " + account.getShortType() + " is not in the database.");
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
     * This function is used to withdraw an amount from an account.
     */
    @FXML
    void withdraw()
    {
        RadioButton selectedRadioButton = (RadioButton) dw_accountType.getSelectedToggle();
        switch (selectedRadioButton.getId()) {
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
     * @param account - account being deposited to.
     */
    private void withDrawFromAccount(Account account){
        if(!populateHolderAndBalance(false, account, "Withdraw - amount cannot be 0 or negative.")){
            return;
        }
        Account existing = accountDatabase.getAccountIfExists(account);
        if(existing!=null && existing.getType().equals(account.getType())) {
            if(!existing.isClosed()){
                if(accountDatabase.withdraw(account)) {
                    output.setText("Withdraw - balance updated.");
                }else{
                    output.setText("Withdraw - insufficient fund.");
                }
            }else{
                output.setText("Account is closed already.");
            }
        }else{
            output.setText(dw_firstName.getText() + " " + dw_lastName.getText() + " " + dw_dob.getValue() + " " + account.getShortType() + " is not in the database.");
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
     * This function is used to print all accounts
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
     * This function is used to print all accounts by order account type.
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
     * This function is used to print all accounts with calculated fees and interests.
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
     * This function is used to update the balances of all accounts and print them.
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

