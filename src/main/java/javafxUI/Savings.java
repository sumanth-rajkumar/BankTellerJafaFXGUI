package javafxUI;


/**
 * Savings class is the extension of Account class. It contains the data and specific operations
 * needed to run a Savings account.
 * @author Sumanth Rajkumar, Shantanu Jain
 */
public class Savings extends Account {
    private static final double ANNUAL_INTEREST = 0.3;
    private static final String ACCOUNT_TYPE = "Savings";
    private static final int EXPECTED_BALANCE = 300;
    private static final int MONTHLY_FEE = 6;
    private static final double LOYALTY_BONUS = 0.15;
    private static final int INTEREST_PERCENT_DIVIDED_BY_TOTAL_MONTHS = 1200;
    private boolean loyalty;

    public static final String LOYAL = "1";
    public static final String NON_LOYAL = "0";

    /**
     * Checks if the passed in object is an instance of Savings or not, overrides method in Account class.
     * @param obj - object
     * @return true if profile of passed in object is equal to the profile of this
     * Account otherwise false.
     */
    @Override
    public boolean equals(Object obj)
    {
        if((obj instanceof Savings) && !(obj instanceof MoneyMarket))
        {
            Savings account = (Savings) obj;
            return account.holder.equals(this.holder);
        }
        return false;
    }

    /**
     * This function sets the passed in account object's new balance and sets the boolean
     * closed variable to false by polymorphism and sets the new loyalty,
     * overrides method in Account class.
     * @param newAccount - account object that has to be reopened
     */
    @Override
    public void reOpen(Account newAccount)
    {
        super.reOpen(newAccount);
        this.loyalty = ((Savings)newAccount).loyalty;
    }

    /**
     * This function is used to get the annual interest rate without loyalty bonus of a Savings account.
     * @return - double representing the annual interest without loyalty bonus
     */
    protected double getBaseAnnualInterestRate(){
        return ANNUAL_INTEREST;
    }

    /**
     * This function is used to get the annual interest rate after loyalty rate is added.
     * @return - double representing the annual interest after loyalty bonus
     */
    protected double getLoyaltyBonusRate(){
        return LOYALTY_BONUS;
    }

    /**
     * This function is used to get the annual interest rate of a Savings account depending
     * on whether the holder has been loyal or not.
     * @return - double representing the annual interest depending on loyalty status
     */
    protected double getAnnualInterestRate()
    {
        if(this.isLoyal())
        {
            return getBaseAnnualInterestRate() + getLoyaltyBonusRate();
        }
        return getBaseAnnualInterestRate();
    }

    /**
     * This function returns a monthly interest, which in this case would be balance*annualInterest/1200,
     * overrides method in Account class.
     * @return The monthly interest that an account holder gets on his account.
     */
    @Override
    public double monthlyInterest() {
        return getBalance()*getAnnualInterestRate()/ INTEREST_PERCENT_DIVIDED_BY_TOTAL_MONTHS;
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
        if(balance >= EXPECTED_BALANCE)
        {
            return 0;
        }
        else
        {
            return MONTHLY_FEE;
        }
    }

    /**
     * This function gives the name of the account type, overrides method in Account class.
     * @return a String, which is the name of the account type.
     */
    @Override
    public String getType()
    {
        return ACCOUNT_TYPE;
    }

    /**
     * This function gives the account info, profile info, closing status, and loyalty status
     * overrides method in Account class.
     * @return a String that describes the account type and weather the account holder
     * is loyal or not.
     */
    @Override
    public String toString()
    {
        if(this.isClosed())
        {
            return getType() + "::" + super.toString() + "::CLOSED";
        }
        else if(this.isLoyal()) {
            return getType() + "::" + super.toString() + "::Loyal";
        }

        return getType() + "::" + super.toString();
    }

    /**
     * This method sets the loyalty of the account holder.
     * @param loyalty - false for not loyal, true for loyal
     */
    public void setLoyalty(boolean loyalty)
    {
        this.loyalty = loyalty;
    }

    /**
     * This method gives the loyalty status of an account holder
     * @return true if account holder is loyal, false if account holder is not loyal.
     */
    public boolean isLoyal()
    {
        return this.loyalty;
    }


}
