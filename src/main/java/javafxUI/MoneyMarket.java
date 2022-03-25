package javafxUI;

/**
 * MoneyMarket class is the extension of Savings class. It contains the data and specific operations
 * needed to handle a Money Market account.
 * @author Sumanth Rajkumar, Shantanu Jain
 */
public class MoneyMarket extends Savings {



    private static final double annualInterest = 0.8;
    private static final String accountType = "Money Market Savings";
    public static final int ExpectedBalance = 2500;
    private static final int monthlyFee = 10;
    private static final int maximumWithdrawals = 3;
    private int withdrawCounter = 0;

    /**
     * Checks if the passed in object is an instance of Money Market or not,
     * overrides method in Account class.
     * @param obj - object
     * @return true if profile of passed in object is equal to the profile of this
     * Account otherwise false.
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof MoneyMarket)
        {
            MoneyMarket account = (MoneyMarket) obj;
            return account.holder.equals(this.holder);
        }
        return false;
    }

    /**
     * This function is used to get the annual interest rate without loyalty bonus of a Savings account,
     * overrides method in Savings class.
     * @return - double representing the annual interest without loyalty bonus
     */
    @Override
    protected double getBaseAnnualInterestRate(){
        return annualInterest;
    }

    /**
     * This function is used to close a Money Market account and reset the withdrawal counter to 0,
     * overrides method in Account class.
     */
    @Override
    public void close()
    {
       super.close();
       this.withdrawCounter = 0;
    }

    /**
     * This function deducts the balance from an account, the amount that should be deducted from the
     * balance is passed in as a parameter, overrides the method in Account class.
     * @param amount - The amount that needs to be deducted or withdrawn from an account.
     */
    @Override
    public void withdraw(double amount)
    {
        super.withdraw(amount);
        withdrawCounter += 1;
    }

    /**
     * This function returns a monthly fee that an account holder needs to pay on a monthly basis,
     * overrides method in Account class.
     * @return - a double that represents a monthly fee that an account holder needs to pay on a monthly basis
     * depending on the balance.
     */
    @Override
    public double fee()
    {
        if(isClosed())
        {
            return 0;
        }
        if(balance >= ExpectedBalance && withdrawCounter <= maximumWithdrawals)
        {
            return 0;
        }
        else
        {
            return monthlyFee;
        }
    }

    /**
     *
     * @return a string that describes the profile info,
     * how many withdraws an account holder had, and if they are loyal or not.
     */
    @Override
    public String toString()
    {
        if(this.isLoyal())
        {
            return super.toString() + "::withdrawal: " + withdrawCounter;
        }
        else if(this.isClosed())
        {
            return super.toString() + "::withdrawal: " + withdrawCounter;
        }
        return super.toString() + "::withdrawal: " + withdrawCounter;
    }

    /**
     * This function gives the name of the account type, overrides method in Account class.
     * @return a String, which is the name of the account type.
     */
    @Override
    public String getType()
    {
        return accountType;
    }

    /**
     * This method returns a different way of saying the name of an account type,
     * overrides method in Account class.
     * @return a String, which is the name of the account type.
     */
    @Override
    public String getShortType()
    {
        return "Money Market";
    }


    /**
     * This method checks whether an account holder is loyal or not and acts as a helper method
     * to determine if the initial deposit is greater than or equal to 2500,
     * overrides method in Savings class.
     * @return true if the account holder is loyal, false if the account holder is not loyal.
     */
    @Override
    public boolean isLoyal()
    {
        return this.balance >= ExpectedBalance;
    }

    /**
     * This method determines if the initial deposit is greater than or equal to 2500.
     * @return true if the initial deposit is greater than or equal to 2500, false if not.
     */
    public boolean hasMinimumInitialDeposit()
    {
       return isLoyal();
    }



}
