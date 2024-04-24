package j2b.ruleDefinitions.input;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sailpoint.sail4j.annotation.IgnoredBySailPointRule;
import com.sailpoint.sail4j.annotation.SailPointRule;
import com.sailpoint.sail4j.annotation.SailPointRule.RuleType;
import com.sailpoint.sail4j.annotation.SailPointRuleMainBody;

import sailpoint.api.SailPointContext;
import sailpoint.object.Application;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

@SailPointRule(name = "ActiveDirectorySAMAccountName", type = RuleType.FIELD_VALUE, 
	referencedRules = {"Rule-Library-General", "Rule-Library-ActiveDirectory-Search"})
public class ActiveDirectorySAMAccountNameFieldValueRule {

	private static Log ruleLog = LogFactory.getLog("rule.library.active.directory");
	
	@IgnoredBySailPointRule
	private String notUsed;
	
	@IgnoredBySailPointRule
	//Dummy method. The real method is actually inside one of referenced rule libraries.
	public boolean userNameExistInActiveDirectory(SailPointContext context, Identity identity, Application application,
			String strUserName) throws GeneralException {
		return false;
	}

	//Construct will be ignored when generating Rule
	public ActiveDirectorySAMAccountNameFieldValueRule() {
		
	}

	public String getActiveDirectoryUniqueName(SailPointContext context, Identity identity,
			Application adApplication, boolean escapeName) throws GeneralException {
		ruleLog.debug("=== Generate Active Directory Unique username ===");
		
		String strUserName = "";
		try {
			String strFirstName = identity.getFirstname();
			String strLastName = identity.getLastname();
			
			// Code to handle unusual case of names are blank
			if (StringUtils.isEmpty(strFirstName) || StringUtils.isEmpty(strLastName)) {
				ruleLog.debug("=== strFirstName and or strLastName is null/empty ===");
				return "";
			}

			// Code to Replace special characters from the last name
			if (!StringUtils.isBlank(strLastName)) {
				strLastName = strLastName.replaceAll("[^a-zA-Z0-9]+", "").toUpperCase();
			}
			if (!StringUtils.isBlank(strFirstName)) {
				strFirstName = strFirstName.replaceAll("[^a-zA-Z0-9]+", "").toUpperCase();
			}
			
			// Code to get the first initial of First Name in a variable
			String strFirstNameSub = StringUtils.substring(strFirstName, 0, 1);
		
			String strLastNameSub = "";

			strUserName = strLastName + strFirstNameSub;
			ruleLog.debug("adAccountName created initially: " + strUserName);

			int iLength = StringUtils.length(strUserName);
			ruleLog.debug("Length of the adAccountName created initially: " + iLength);
			// Code to get the basic adAccountName
			if (iLength > 8) {
				strLastNameSub = StringUtils.substring(strLastName, 0, 8 - iLength);
				ruleLog.debug("strLastNameSub 8-iLength" + strLastNameSub);
				strUserName = strLastNameSub + strFirstNameSub;
				ruleLog.debug("adAccountName created after checking length: " + strUserName);
			}
			do {
				// Check if the calculated username exists in AD
				if (!userNameExistInActiveDirectory(context, identity, adApplication, strUserName)) {
					break;
				}
				// Check the numeric value added to create the unique adAccountName
				String strUser = strUserName.replaceAll("[^0-9]", "");
				int iNumber = 0;
				if (!StringUtils.isBlank(strUser)) {
					iNumber = Integer.valueOf(strUser).intValue();
				}
				int iLengthNumber = String.valueOf(iNumber + 1).length();
				// Create the adAccountName by adding numeric values to make it generic
				strLastNameSub = StringUtils.substring(strLastName, 0, 7 - iLengthNumber);
				strUserName = strLastNameSub + strFirstNameSub;
				iNumber++;
				strUserName += iNumber;
				ruleLog.debug("adAccountName generated with numbers after duplicate found: " + strUserName);
					
			} while (true);
			
			ruleLog.debug("Final User ID created for the new Identity: " + strUserName);
		} catch (Exception ex) {
			ruleLog.error("Exception while calcualting Identity adAccountName: ", ex);
			return "";
		}
			return strUserName;
		}
		
		@SailPointRuleMainBody
		public String executeRule(SailPointContext context, Identity identity, Application application) 
				throws GeneralException {
		    String userName = getActiveDirectoryUniqueName(context, identity, application, true);
		   
			return userName;
		}
	}
