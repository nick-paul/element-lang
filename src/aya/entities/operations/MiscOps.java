package aya.entities.operations;

import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.NUM;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.obj.Obj.STR;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import aya.Aya;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.SyntaxError;
import aya.exceptions.TypeError;
import aya.instruction.op.OpDocReader;
import aya.instruction.op.OpInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.GenericList;
import aya.obj.list.Str;
import aya.obj.list.StrList;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.RationalNum;
import aya.parser.CharacterParser;

public class MiscOps {	

	
	public static char FIRST_OP = '!';
	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static OpInstruction[] MATH_OPS = {
		/* 33 !  */ new OP_Fact(),
		/* 34 "  */ null,
		/* 35 #  */ new OP_HashCode(),
		/* 36 $  */ new OP_SysTime(),
		/* 37 %  */ null,
		/* 38 &  */ null,
		/* 39 '  */ null,
		/* 40 (  */ null,
		/* 41 )  */ null,
		/* 42 *  */ null,
		/* 43 +  */ null,
		/* 44 ,  */ null,
		/* 45 -  */ null,
		/* 46 .  */ null,
		/* 47 /  */ null,
		/* 48 0  */ null,
		/* 49 1  */ null,
		/* 50 2  */ null,
		/* 51 3  */ null,
		/* 52 4  */ null,
		/* 53 5  */ null,
		/* 54 6  */ null,
		/* 55 7  */ null,
		/* 56 8  */ null,
		/* 57 9  */ null,
		/* 58 :  */ null,
		/* 59 ;  */ null,
		/* 60 <  */ null, //new OP_ModSet(),
		/* 61 =  */ null,
		/* 62 >  */ null, //new OP_ModGet(),
		/* 63 ?  */ new OP_Help(),
		/* 64 @  */ null,
		/* 65 A  */ null, //new OP_Abs(),
		/* 66 B  */ null,
		/* 67 C  */ new OP_Acosine(),
		/* 68 D  */ new OP_MDate(),
		/* 69 E  */ null, //new OP_ScientificNotation(),
		/* 70 F  */ null,
		/* 71 G  */ null,
		/* 72 H  */ new OP_MParse_Date(),
		/* 73 I  */ null,
		/* 74 J  */ null,
		/* 75 K  */ null,
		/* 76 L  */ new OP_Log(),
		/* 77 M  */ null,
		/* 78 N  */ null,
		/* 79 O  */ null,
		/* 80 P  */ null,//new OP_PrintColor(),
		/* 81 Q  */ null,
		/* 82 R  */ null,
		/* 83 S  */ new OP_Asine(),
		/* 84 T  */ new OP_Atangent(),
		/* 85 U  */ null,
		/* 86 V  */ null,
		/* 87 W  */ null,
		/* 88 X  */ null,
		/* 89 Y  */ null,
		/* 90 Z  */ null,
		/* 91 [  */ null, //Matrix Literal
		/* 92 \  */ null,
		/* 93 ]  */ null, //No used for matrix literal, but ptobably shouldnt be used for anything
		/* 94 ^  */ null,
		/* 95 _  */ null,
		/* 96 `  */ null,
		/* 97 a  */ null,
		/* 98 b  */ null,
		/* 99 c  */ new OP_Cosine(),
		/* 100 d */ new OP_CastDouble(),
		/* 101 e */ new OP_Me(),
		/* 102 f */ null,
		/* 103 g */ null,
		/* 104 h */ new OP_MShow_Date(),
		/* 105 i */ null, //new OP_CastInt(),
		/* 106 j */ null,
		/* 107 k */ new OP_AddParserChar(),
		/* 108 l */ new OP_Ln(),
		/* 109 m */ new OP_HasMeta(),
		/* 110 n */ null,
		/* 111 o */ null,
		/* 112 p */ new OP_Primes(),
		/* 113 q */ null,
		/* 114 r */ new OP_To_Rat(),
		/* 115 s */ new OP_Sine(),
		/* 116 t */ new OP_Tangent(),
		/* 117 u */ new OP_Atan2(),
		/* 118 v */ null,
		/* 119 w */ null,
		/* 120 x */ null,
		/* 121 y */ null,
		/* 122 z */ null,
		/* 123 { */ null,
		/* 124 | */ null,
		/* 125 } */ null,
		/* 126 ~ */ null,
	};
	
	/** Returns the operation bound to the character */
	public static OpInstruction getOp(char c) {
		OpInstruction op = getOpOrNull(c);
		if (op == null) {
			throw new SyntaxError("Misc operator 'M" + c + "' does not exist");
		} else {
			return op;
		}
	}
	
	public static OpInstruction getOpOrNull(char op) {
		if(op >= 33 && op <= 126) {
			return MATH_OPS[op-FIRST_OP];
		} else {
			return null;
		}
	}
	
}

// ! - 33
class OP_Fact extends OpInstruction {
	
	public OP_Fact() {
		init("M!");
		arg("N", "factorial");
		setOverload(1, "fact");
		vect();
	}
	
	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)){
			block.push(((Number)n).factorial());
		} else if (n.isa(NUMBERLIST)) {
			block.push( ((NumberList)n).factorial() );
		} else {
			throw new TypeError(this, n);
		}
	}
}

// # - 35
class OP_HashCode extends OpInstruction {
	
	public OP_HashCode() {
		init("M#");
		arg("A", "hash code of the object");
	}
	
	@Override
	public void execute(Block block) {
		block.push(Num.fromInt(block.pop().hashCode()));
	}
}

// $ - 36
class OP_SysTime extends OpInstruction {
	
	public OP_SysTime() {
		init("M$");
		arg("-", "system time in milliseconds");
	}
	
	@Override
	public void execute(Block block) {
		block.push(new Num(System.currentTimeMillis()));
	}
}



// ? - 63
class OP_Help extends OpInstruction {
	
	public OP_Help() {
		init("M?");
		arg("N", "list op descriptions where N=[0:std, 1:dot, 2:colon, 3:misc]");
		arg("S", "search all help data");
	}

	@Override
	public void execute(Block block) {
		Obj s = block.pop();
		
		if(s.isa(STR)) {
			String str = s.str();
			ArrayList<Str> items;
			
			if (str.length() == 0) {
				String[] ss = Aya.getInstance().getHelpData().getAllItems();
				items = new ArrayList<Str>(ss.length);
				for (String a : ss) {
					items.add(new Str(a));
				}
			} else {
				ArrayList<String> ss = Aya.getInstance().getHelpData().staticSearch(s.str());
				items = new ArrayList<Str>(ss.size());
				for (String a : ss) {
					items.add(new Str(a));
				}
			}
			
			block.push(new StrList(items));
			
		} else if (s.isa(NUMBER)) {
			block.push(new GenericList(OpDocReader.getAllOpDicts()));
		}
		else {
			throw new TypeError(this, s);
		}
	}
}

// C - 67
class OP_Acosine extends OpInstruction {
	
	public OP_Acosine() {
		init("MC");
		arg("N", "inverse cosine");
		setOverload(1, "acos");
		vect();
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;

		if(n.isa(NUMBER)) {
			block.push(((Number)n).acos());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).acos());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// D - 68
class OP_MDate extends OpInstruction {

	private Calendar cal; 

	public OP_MDate() {
		init("MD");
		arg("N", "given time in ms, return date params [day_of_week, year, month, day_of_month, hour, min, s]");
		cal = Calendar.getInstance();
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();

		if (a.isa(NUMBER)) {
			long timeStamp = ((Number)a).toLong();
			cal.setTimeInMillis(timeStamp);
			
			ArrayList<Number> fields = new ArrayList<Number>();
			
			fields.add(Num.fromInt(cal.get(Calendar.DAY_OF_WEEK)));
			fields.add(Num.fromInt(cal.get(Calendar.YEAR)));
			fields.add(Num.fromInt(cal.get(Calendar.MONTH)));
			fields.add(Num.fromInt(cal.get(Calendar.DAY_OF_MONTH)));
			fields.add(Num.fromInt(cal.get(Calendar.HOUR)));
			fields.add(Num.fromInt(cal.get(Calendar.MINUTE)));
			fields.add(Num.fromInt(cal.get(Calendar.SECOND)));

			block.push(new NumberItemList(fields));
		} else {
			throw new TypeError(this, a);
		}
	}
}


// H - 68
class OP_MParse_Date extends OpInstruction {
	
	public OP_MParse_Date() {
		init("MH");
		arg("SS", "parse a date using a given format and return the time in ms");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		
		if (a.isa(STR) && b.isa(STR)) {
			String df_str = a.str();
			String date_str = b.str();
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new AyaRuntimeException("Invalid date format: '" + df_str + "'");
			}
			
			Date date;
			try {
				date = df.parse(date_str);
			} catch (ParseException e) {
				throw new AyaRuntimeException("Cannot parse date: '" + date_str + "' as '" + df_str + "'");
			}
			block.push(new Num(date.getTime()));
		} else {
			throw new TypeError(this, a, b);
		}
		
	}
}


// L - 76
class OP_Log extends OpInstruction {
	
	public OP_Log() {
		init("ML");
		arg("N", "base-10 logarithm");
		setOverload(1, "log");
		vect();
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (overload().execute(block, a)) return;
		
		if(a.isa(NUMBER)) {
			block.push(((Number)a).log());
		} else if (a.isa(NUMBERLIST)) {
			block.push(((NumberList)a).log());
		} else {
			throw new TypeError(this, a);
		}
	}
}


// S - 83
class OP_Asine extends OpInstruction {
	
	public OP_Asine() {
		init("MS");
		arg("N", "inverse sine");
		vect();
		setOverload(1, "asin");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;

		if(n.isa(NUMBER)) {
			block.push(((Number)n).asin());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).asin());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// T - 84
class OP_Atangent extends OpInstruction {
	
	public OP_Atangent() {
		init("MT");
		arg("N", "inverse tangent");
		vect();
		setOverload(1, "atan");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
	
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).atan());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).atan());
		} else {
			throw new TypeError(this, n);
		}
	}
}


// c - 99
class OP_Cosine extends OpInstruction {
	
	public OP_Cosine() {
		init("Mc");
		arg("N", "cosine");
		vect();
		setOverload(1, "cos");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if(overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).cos());
			return;
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).cos());
		} else {
			throw new TypeError(this, n);
		}
	}
}

//d - 100
class OP_CastDouble extends OpInstruction {
	
	public OP_CastDouble() {
		init("Md");
		arg("N", "cast to double");
		arg("S", "parse double, if invalid, return 0.0");
		setOverload(1, "float");
		vect();
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
		if (overload().execute(block, a)) return;
		
		if(a.isa(STR)) {
			try {
				block.push(new Num(Double.parseDouble(a.str())));
			} catch (NumberFormatException e) {
				throw new AyaRuntimeException("Cannot cast string \""+ a.repr() + "\" to a double.");
			}
		} else if (a.isa(NUM)) {
			block.push(a); //Already a double
		} else if (a.isa(NUMBER)){
			block.push(new Num(((Number)a).toDouble()));
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_FLOAT);
		}
		else {
			throw new TypeError(this, a);
		}
	}
}

// e - 100
class OP_Me extends OpInstruction {
	
	public OP_Me() {
		init("Me");
		arg("N", "exponential function");
		vect();
		setOverload(1, "exp");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
	
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).exp());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).exp());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// h - 104
class OP_MShow_Date extends OpInstruction {
	
	public OP_MShow_Date() {
		init("Mh");
		arg("NS", "convert the time in ms to a date string according to a given format");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		if (a.isa(STR) && b.isa(NUMBER)) {
			String df_str = a.str();
			long time = ((Number)b).toLong();
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new AyaRuntimeException("Invalid date format: '" + df_str + "'");
			}
			
			Date date = new Date(time);
			String out;
			try {
				out = df.format(date);
			} catch (Exception e) {
				throw new AyaRuntimeException("Cannot parse time: '" + time + "' as date '" + df_str + "'");
			}
			block.push(new Str(out));
		} else {
			throw new TypeError(this, a, b);
		}
		
	}
}


// k - 107
class OP_AddParserChar extends OpInstruction {
	
	public OP_AddParserChar() {
		init("Mk");
		arg("CS", "add special character");
	}

	@Override
	public void execute(Block block) {
		final Obj obj_name = block.pop();
		final Obj obj_char = block.pop();
		
		if (obj_name.isa(STR) && obj_char.isa(CHAR)) {
			String str = obj_name.str();
			if (str.length() > 0 && CharacterParser.lalpha(str)) {
				CharacterParser.add_char(str, ((Char)obj_char).charValue());
			} else {
				throw new AyaRuntimeException("Cannot create special character using " + str);
			}
		} else {
			throw new TypeError(this, obj_char, obj_name);
		}
	}
}


// l - 108
class OP_Ln extends OpInstruction {
	
	public OP_Ln() {
		init("Ml");
		arg("N", "natural logarithm");
		vect();
		setOverload(1, "ln");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).ln());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).ln());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// m - 109
class OP_HasMeta extends OpInstruction {
	
	public OP_HasMeta() {
		init("Mm");
		arg("D", "true if the dict has a metatable, leave D on stack");
	}

	@Override
	public void execute(Block block) {
		final Obj d = block.pop();

		if (d.isa(DICT)) {
			block.push(((Dict)d).hasMetaTable() ? Num.ONE : Num.ZERO);
		} else {
			throw new TypeError(this, d);
		}
	}
}

// p - 112
class OP_Primes extends OpInstruction {
	
	public OP_Primes() {
		init("Mp");
		arg("N", "list primes up to N");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();

		if (a.isa(NUMBER)) {
			int i = ((Number)a).toInt();
			if (i < 0) {
				throw new AyaRuntimeException("Mp: Input must be positive");
			}
			block.push(NumberItemList.primes(i));
		} else {
			throw new TypeError(this, a);
		}
	}

}

// r - 114
class OP_To_Rat extends OpInstruction {
	
	public OP_To_Rat() {
		init("Mr");
		arg("N", "convert to rational number");
		vect();
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if(n.isa(NUMBER)) {
			if (n.isa(Obj.RATIONAL_NUMBER)) {
				block.push(n);
			} else {
				block.push( new RationalNum(((Number)n).toDouble()) );
			}
		} else if (n.isa(NUMBERLIST)) {
			ArrayList<Number> nl = ((NumberList)n).toArrayList();
			ArrayList<Number> ns = new ArrayList<Number>(nl.size());
			for (Number j : nl) {
				if (j.isa(Obj.RATIONAL_NUMBER)) {
					ns.add(j);
				} else {
					ns.add( new RationalNum(((Number)j).toDouble()) );
				}
			}
			block.push(new NumberItemList(ns));
		} else {
			throw new TypeError(this, n);
		}
	}
}


// s - 115
class OP_Sine extends OpInstruction {
	
	public OP_Sine() {
		init("Ms");
		arg("N", "sine");
		vect();
		setOverload(1, "sin");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if(n.isa(NUMBER)) {
			block.push(((Number)n).sin());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).sin());
		} else {
			throw new TypeError(this, n);
		}
	}
}




// t - 116
class OP_Tangent extends OpInstruction {
	
	public OP_Tangent() {
		init("Mt");
		arg("N", "tangent");
		vect();
		setOverload(1, "tan");
	}

	@Override
	public void execute(Block block) {
		Obj n = block.pop();
		
		if (overload().execute(block, n)) return;
		
		if (n.isa(NUMBER)) {
			block.push(((Number)n).tan());
		} else if (n.isa(NUMBERLIST)) {
			block.push(((NumberList)n).tan());
		} else {
			throw new TypeError(this, n);
		}
	}
}


// u - 117
class OP_Atan2 extends OpInstruction {
	
	public OP_Atan2() {
		init("Mu");
		arg("NN", "y x Mu => atan2(y,x)");
	}

	@Override
	public void execute(Block block) {
		Obj x = block.pop();
		Obj y = block.pop();

		if (x.isa(NUMBER) && y.isa(NUMBER)) {
			double ny = ((Num)y).toDouble();
			double nx = ((Num)x).toDouble();
			block.push(new Num(Math.atan2(ny, nx)));
		} else {
			throw new TypeError(this, x, y);
		}
	}
}











