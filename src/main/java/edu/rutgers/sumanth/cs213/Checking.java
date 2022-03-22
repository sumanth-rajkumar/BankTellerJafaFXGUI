/**
 * Checking class is the extension of Account class. It contains the data and specific operations
 * needed to run a checking account.
 * @author Sumanth Rajkumar, Shantanu Jain
 */

package edu.rutgers.sumanth.cs213;


public class Checking extends Account
{

    private static final double annualInterest = 0.1;
    private static final String accountType = "Checking";
    private static final int minimumBalanceToWaiveFee = 1000;
    private static final int monthlyFee = 25;
    private static final int interestPercentDividedByTotalMonths = 1200;

    /**
     * Checks if the passed in object is an instance of Checking or not, overrides method in Account class.
     * @param obj - object
     * @return true if profile of passed in object is equal to the profile of this
     * Account otherwise false.
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Checking)
        {
            Checking account = (Checking) obj;
            return account.holder.equals(holder);
        }
        return false;
    }

    /**
     * This function is used to get the annual interest rate of a Checking account.
     * @return - double representing the annual interest
     */
    protected double getAnnualInterestRate(){
        return annualInterest;
    }

    /**
     * This function returns a monthly interest, which in this case would be balance*annualInterest/1200,
     * overrides method from Account class.
     * @return - double representing the monthly interest that an account holder gets on his account.
     */
    @Override
    public double monthlyInterest() {
        return getBalance()*getAnnualInterestRate()/interestPercentDividedByTotalMonths;
    }

    /**
     * This function returns a monthly fee that an account holder needs to pay on a monthly basis,
     * overrides method from Account class.
     * @return - a double representing a monthly fee that an account holder needs to pay on a monthly basis
     * depending on the balance.
     */
    @Override
    public double fee()
    {
        if(balance >= minimumBalanceToWaiveFee)
        {
            return 0;
        }
        else
        {
            return monthlyFee;
        }
    }

    /**
     * This function gives the name of the account type, overrides method in Account class.
     * @return a String, which is the name of the account type.
     */
    @Override
    public String getType() {
        return accountType;
    }

    /**
     * This function gives the account info, profile info, and closing status,
     * overrides method in Account class.
     * @return the type of account, profile info of holder and whether it's closed.
     */
    @Override
    public String toString()
    {
        if(this.isClosed())
        {
            return getType() + "::" + super.toString() + "::CLOSED";
        }
        return getType() + "::" + super.toString();
    }
}
