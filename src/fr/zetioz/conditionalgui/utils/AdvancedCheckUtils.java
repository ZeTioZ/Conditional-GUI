package fr.zetioz.conditionalgui.utils;

import java.math.BigDecimal;

public final class AdvancedCheckUtils {
	private AdvancedCheckUtils() {}
	
	public static String[] mathDecoder(String stringToDecode)
	{
		String[] stringDecoded = null;
		if((stringToDecode.contains("UP_") && !stringToDecode.contains("DOWN_") && !stringToDecode.contains("EQUALS_")) //Looking for "UP_" keyword
				|| (stringToDecode.contains("DOWN_") && !stringToDecode.contains("UP_") && !stringToDecode.contains("EQUALS_")) //Looking for "DOWN_" keyword
				|| (stringToDecode.contains("EQUALS_") && !stringToDecode.contains("UP_") && !stringToDecode.contains("DOWN_"))) //Looking for "EQUAL_" keyword
			{
				stringDecoded = stringToDecode.contains("UP_") ? stringToDecode.split("UP_") : stringToDecode.contains("DOWN_") ? stringToDecode.split("DOWN_") : stringToDecode.split("EQUALS_");
				stringDecoded[0] = stringToDecode.contains("UP_") ? "UP_" : stringToDecode.contains("DOWN_") ? "DOWN_" : "EQUALS_";
			}
		return stringDecoded;
	}
	
	public static boolean checkMath(String mathCondition, int playerValue, int configValue)
	{
		return((mathCondition != null
		    && (    (mathCondition.equals("UP_") && playerValue > configValue)
			     || (mathCondition.equals("DOWN_") && playerValue < configValue)
			     || (mathCondition.equals("EQUALS_") && playerValue == configValue)
		       )
		    )
		   || (mathCondition == null && playerValue >= configValue));
	}
	
	public static boolean checkMath(String mathCondition, double playerValue, double configValue)
	{
		return((mathCondition != null
		    && (    (mathCondition.equals("UP_") && playerValue > configValue)
			     || (mathCondition.equals("DOWN_") && playerValue < configValue)
			     || (mathCondition.equals("EQUALS_") && playerValue == configValue)
		       )
		    )
		   || (mathCondition == null && playerValue >= configValue));
	}
	
	public static boolean checkMath(String mathCondition, BigDecimal playerValue, BigDecimal configValue)
	{
		return((mathCondition != null
		    && (    (mathCondition.equals("UP_") && playerValue.compareTo(configValue) == 1)
			     || (mathCondition.equals("DOWN_") && playerValue.compareTo(configValue) == -1)
			     || (mathCondition.equals("EQUALS_") && playerValue.compareTo(configValue) == 0)
		       )
		    )
		   || (mathCondition == null && playerValue.compareTo(configValue) >= 0));
	}
}
