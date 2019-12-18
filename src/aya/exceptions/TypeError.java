package aya.exceptions;

import aya.instruction.op.OpInstruction;
import aya.obj.Obj;

@SuppressWarnings("serial")
public class TypeError extends RuntimeException {
	String msg;
	
	public String getSimpleMessage() {
		return msg;
	}
	
	public TypeError(OpInstruction op, Obj... recieved) {
		super("Type error at (" + op.name + "):\n\tExpected (" 
				+ op.getDocTypeStr()
				+ ")\n\tReceived (" + listStr(recieved));
		
		msg = "Type error at (" + op.name + "):\n\tExpected (" 
				+ op.getDocTypeStr()
				+ ")\n\tReceived (" + listStr(recieved);
	}
	
	public TypeError(String s) {
		super(s);
		msg = s;
	}
	
	public static String listStr(Obj[] recieved) {
		String msg = "";
		for(int i = recieved.length-1; i>=0; i--) {
			msg += recieved[i].repr() + " ";
		}
		return msg  + ")";
	}
}
