package aya.parser.tokens;

import aya.instruction.variable.GetKeyVariableInstruction;
import aya.variable.Variable;

public class KeyVarToken extends StdToken {

	public KeyVarToken(String data) {
		super(data, Token.KEY_VAR);
	}
	
	public long getID() {
		return Variable.encodeString(data);
	}

	@Override
	public Object getAyaObj() {
		return new GetKeyVariableInstruction(getID());
	}

	@Override
	public String typeString() {
		return "keyvar";
	}
		
}
