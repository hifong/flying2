package org.lilystudio.smarty4j.statement.modifier;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

public class $fixwidth extends Modifier {
	  private static ParameterCharacter[] definitions = {
	      new ParameterCharacter(ParameterCharacter.INTEGER, new ConstInteger(80)) };

	
	public Object execute(Object obj, Context context, Object[] values) {
	    final int fixedlen = ((Integer) values[0]).intValue();
	    
	    String text = obj == null?"":obj.toString();
	    if(text.length() > fixedlen) {
	    	return text;
	    } else {
	    	final int patchlen = fixedlen - text.length();
	    	for(int i = 0 ; i < patchlen; i ++) {
	    		text += " ";
	    	}
	    	return text;
	    }
	}

	
	public ParameterCharacter[] getDefinitions() {
		return definitions;
	}
}
