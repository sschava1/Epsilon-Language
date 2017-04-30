
import java.util.*;

import org.antlr.v4.runtime.misc.NotNull;

public class EpsilonListenerImplementation extends EpsilonBaseListener {
	
	int mainCnt = 0;
	int counter = 1;
	List<String> list = new ArrayList<String>();

	// -----------------Main method Construct--------------
	@Override
	public void enterMainDefinitionDeclaration(@NotNull EpsilonParser.MainDefinitionDeclarationContext ctx) {

		counter++;
		list.add("DEFN main");
	}

	@Override
	public void exitMainDefinitionDeclaration(@NotNull EpsilonParser.MainDefinitionDeclarationContext ctx) {
		counter++;
		list.add("EXITDEFN main");
	}

	@Override
	public void enterMainWhileIterator(@NotNull EpsilonParser.MainWhileIteratorContext ctx) {
		loopStart.push(counter);
	}

	@Override
	public void exitMainIfElseStatement(@NotNull EpsilonParser.MainIfElseStatementContext ctx) {
		int numberOfIfElse = numberOfIfElses.pop();
		int i = 0;
		while (i < numberOfIfElse) {
			int elementAt = numberOfifElseEnd.pop();
			String prevElement = list.get(elementAt);
			prevElement = prevElement + (" " + (counter));
			list.set(elementAt, prevElement);
			i++;
		}
	}

	@Override
	public void enterMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) {
		numberOfIfElses.push(1);
	}

	@Override
	public void exitMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) {
		counter++;
		numberOfifElseEnd.add(counter);
		int elementAt = numberOfifElseCondition.pop();
		String prevElement = list.get(elementAt);
		prevElement = prevElement + (" " + (counter + 1));
		list.set(elementAt, prevElement);
		list.add("PUSH True");
		list.add("CONDTRUEGOTO");
		counter++;
	}
	// --------------------------End of MAIN construct------------------

	// ----------Start of program, and declarations,assignments---------
	@Override
	public void enterStart(@NotNull EpsilonParser.StartContext ctx) {
		if (mainCnt < 1) {
			list.add("");
			mainCnt++;
		}
	}

	@Override
	public void exitStart(@NotNull EpsilonParser.StartContext ctx) {
		list.add("EXIT ");
	}

	@Override
	public void exitIdentifierDeclarationAssignment(@NotNull EpsilonParser.IdentifierDeclarationAssignmentContext ctx) {
		counter++;
		list.add("SAVE " + ctx.IDENTIFIER());
	}

	@Override
	public void exitIdentifierAssignment(@NotNull EpsilonParser.IdentifierAssignmentContext ctx) {
		counter++;
		list.add("SAVE " + ctx.IDENTIFIER());
	}

	@Override
	public void exitPrint(@NotNull EpsilonParser.PrintContext ctx) {
		counter++;
		list.add("PRINT ");
	}

	// -------------- METHODS/FUNCTION constructs---------------------------
	@Override
	public void enterDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		counter++;
		list.add("DEFN " + ctx.IDENTIFIER());
		if (ctx.definitionParameters().identifierDeclaration() != null) {
			for (EpsilonParser.IdentifierDeclarationContext obj : ctx.definitionParameters().identifierDeclaration()) {
				counter++;
				list.add("SAVE " + obj.IDENTIFIER());
			}
		}
	}

	@Override
	public void exitDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		counter++;
		list.add("EXITDEFN " + ctx.IDENTIFIER());
	}

	@Override
	public void exitDefinitionReturn(@NotNull EpsilonParser.DefinitionReturnContext ctx) {

		if (ctx.IDENTIFIER() != null) {
			counter++;
			list.add("RETURN " + ctx.IDENTIFIER());
		} else if (ctx.NUMERIC() != null) {
			counter++;
			list.add("RETURN " + ctx.NUMERIC());
		} else if (ctx.BOOL() != null) {
			counter++;
			list.add("RETURN " + ctx.BOOL());	
		}else if (ctx.STRING() != null) {
			counter++;
			list.add("RETURN " + ctx.STRING());		
		}
		else if (ctx.CHARACTER() != null) {
			counter++;
			list.add("RETURN " + ctx.CHARACTER());		
		}
		//recursion case
		else {
			counter++;
			list.add("SAVE temp");
			counter++;
			list.add("RETURN temp");
		}
	}

	@Override
	public void exitDefinitionInvocation(@NotNull EpsilonParser.DefinitionInvocationContext ctx) {
		counter++;
		list.add("INVOKE " + ctx.IDENTIFIER());
	}
	// ------------------End of function construct------------

	// --------------------Data types------------------------
	@Override
	public void enterDataType(@NotNull EpsilonParser.DataTypeContext ctx) {

		if (ctx.NUMERIC() != null) {
			counter++;
			list.add("PUSH " + ctx.NUMERIC());
		} else if (ctx.BOOL() != null) {
			counter++;
			list.add("PUSH " + ctx.BOOL());
		} else {
			counter++;
			list.add("PUSH " + ctx.IDENTIFIER());
		}
	}

	// ------------------IF-ELSE IF-ELSE Construct----------------------------
	Stack<Integer> numberOfIfElses = new Stack<Integer>();
	Stack<Integer> numberOfifElseCondition = new Stack<Integer>();
	Stack<Integer> numberOfifElseEnd = new Stack<Integer>();

	@Override
	public void enterIfStatement(@NotNull EpsilonParser.IfStatementContext ctx) {
		numberOfIfElses.push(1);
	}

	@Override
	public void enterElseIfStatement(@NotNull EpsilonParser.ElseIfStatementContext ctx) {
		numberOfIfElses.push(1 + numberOfIfElses.pop());
	}

	public void exitIfelseStatement(@NotNull EpsilonParser.IfelseStatementContext ctx) {
		int numberOfIfElse = numberOfIfElses.pop();
		int i = 0;
		while (i < numberOfIfElse) {
			int elementAt = numberOfifElseEnd.pop();
			String previousElement = list.get(elementAt);
			previousElement += " " + (counter);
			list.set(elementAt, previousElement);
			i++;
		}
	}

	@Override
	public void exitPrefElseIf(@NotNull EpsilonParser.PrefElseIfContext ctx) {
		list.add("CONDFALSEGOTO");
		numberOfifElseCondition.push(counter);
		counter++;
	}

	@Override
	public void exitPrefIf(@NotNull EpsilonParser.PrefIfContext ctx) {
		list.add("CONDFALSEGOTO");
		numberOfifElseCondition.push(counter);
		counter++;
	}

	@Override
	public void exitIfStatement(@NotNull EpsilonParser.IfStatementContext ctx) {
		counter++;
		numberOfifElseEnd.add(counter);
		Integer eleAt = numberOfifElseCondition.pop();
		String prevElement = list.get(eleAt);
		prevElement = prevElement + (" " + (counter + 1));
		list.set(eleAt, prevElement);
		list.add("PUSH True");
		list.add("CONDTRUEGOTO");
		counter++;
	}

	@Override
	public void exitElseIfStatement(@NotNull EpsilonParser.ElseIfStatementContext ctx) {
		counter++;
		Integer elementAt = numberOfifElseCondition.pop();
		String previousElement = list.get(elementAt);
		previousElement = previousElement + ( " " + (counter + 1));
		list.set(elementAt, previousElement);
		list.add("PUSH True");
		numberOfifElseEnd.add(counter);
		list.add("CONDTRUEGOTO");
		counter++;
	}

	// ------End of IF-ELSE IF-ELSE COnstruct -----------------

	// ------While loop COnstruct--------------------------------
	Stack<Integer> loopStart = new Stack<Integer>();
	Stack<Integer> loopCondition = new Stack<Integer>();

	@Override
	public void enterWhileIterator(@NotNull EpsilonParser.WhileIteratorContext ctx) {
		loopStart.push(counter);
	}

	@Override
	public void exitWhilePrefix(@NotNull EpsilonParser.WhilePrefixContext ctx) {
		loopCondition.push(counter);
		list.add("CONDFALSEGOTO");
		counter++;
	}

	@Override
	public void exitWhileIterator(@NotNull EpsilonParser.WhileIteratorContext ctx) {
		counter++;
		int elementAt = loopCondition.pop();
		String priorElement = list.get(elementAt);
		priorElement = priorElement + (" " + (counter + 1));
		list.set(elementAt, priorElement);
		list.add("PUSH True");
		list.add("CONDTRUEGOTO " + loopStart.pop());
		counter++;
	}

	// -------------------End of while loop construct------------------

	// ---------------Expression Construct------------------------
	@Override
	public void exitExpression(@NotNull EpsilonParser.ExpressionContext ctx) {

		if (ctx.NUMERIC() != null) {
			counter++;
			list.add("PUSH " + ctx.NUMERIC());
		}
		if (ctx.FLOAT() != null) {
			counter++;
			list.add("PUSH " + ctx.FLOAT());
		}
		if (ctx.IDENTIFIER() != null) {
			counter++;
			list.add("PUSH " + ctx.IDENTIFIER());
		}
		if (ctx.ADD() != null) {
			counter++;
			list.add("ADD");
		}
		if (ctx.SUB() != null) {
			counter++;
			list.add("SUB");
		}
		if (ctx.MUL() != null) {
			counter++;
			list.add("MUL");
		}
		if (ctx.DIV() != null) {
			counter++;
			list.add("DIV");
		}
		if (ctx.MODULO() != null) {
			counter++;
			list.add("MOD");
		}
		if (ctx.POWER() != null) {
			counter++;
			list.add("POW");
		}

	}
	//----------Boolean and comparators-----------------
	@Override
	public void exitBoolExpression(@NotNull EpsilonParser.BoolExpressionContext ctx) {
		if (ctx.LOGICAND() != null) {
			counter++;
			list.add("AND");
		} else if (ctx.LOGICNOT() != null) {
			counter++;
			list.add("NOT");
		} else if (ctx.LOGICOR() != null) {
			counter++;
			list.add("OR");
		} else if (ctx.COMPARATORS() != null) {
			switch (ctx.COMPARATORS().toString()) {
			case ">":
				counter++;
				list.add("GREATER");
				break;
			case "<":
				counter++;
				list.add("LESSER");
				break;
			case ">=":
				counter++;
				list.add("GREATEREQUAL");
				break;
			case "<=":
				counter++;
				list.add("LESSEREQUAL");
				break;
			case "=":
				counter++;
				list.add("EQUALS");
				break;
			}
		}
	}

}
