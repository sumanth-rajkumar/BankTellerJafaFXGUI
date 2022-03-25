package javafxUI;

/**
 * Profile class defines the information of an account holder, which are first name, last name and
 * Date of Birth.
 * @author Sumanth Rajkumar, Shantanu Jain
 */
public class Profile {
    private String fName;
    private String lName;
    private Date dob;

    /**
     * Constructor that creates a Profile based off the passed in first name,
     * last name, and dob
     * @param fName - String representing the first name
     * @param lName - String representing the last name
     * @param dob - Date object representing the dob
     */
    public Profile(String fName, String lName, Date dob)
    {
        this.fName = fName;
        this.lName = lName;
        this.dob = dob;
    }

    /**
     * This method checks whether the parameter is an instance of profile or not.
     * @param obj - object being compared with
     * @return true if the parameter is an instance of Profile, otherwise returns false.
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Profile)
        {
            Profile profile = (Profile) obj;
            return profile.fName.equalsIgnoreCase(this.fName) && profile.lName.equalsIgnoreCase(this.lName) && (profile.dob.compareTo(this.dob) == 0);
        }
        return false;
    }

    /**
     * This method gives the first name, last name and date of birth of a user.
     * @return a string that contains the first name, last name and date of birth of a user.
     */
    @Override
    public String toString()
    {
        return this.fName + " " + this.lName + " " + this.dob.toString();
    }
}
