package fr.zetioz.conditionalgui.utils;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public final class EnumCheckUtils {

	private EnumCheckUtils() {}
	
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName)
    {
	    if (enumName == null)
	    {
	        return false;
	    }
	    
	    try
	    {
	        Enum.valueOf(enumClass, enumName);
	        return true;
	    }
	    catch (IllegalArgumentException ex)
	    {
	        return false;
	    }
    }
}
