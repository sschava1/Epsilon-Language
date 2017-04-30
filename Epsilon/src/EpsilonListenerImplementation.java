
import java.util.*;

import org.antlr.v4.runtime.misc.NotNull;


public class EpsilonListenerImplementation extends EpsilonBaseListener {

	int mainCnt = 0;
	int counter = 0;
	List<String> list = new ArrayList<String>();

	// -----------------Main method Construct--------------
	@Override
	public void enterMainDefinitionDeclaration(@NotNull EpsilonParser.MainDefinitionDeclarationContext ctx) {
		counter += 1;
		list.add("DEFN main");
	}

	@Override
	public void exitMainDefinitionDeclaration(@NotNull EpsilonParser.MainDefinitionDeclarationContext ctx) {
		counter += 1;
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
	public void exitMainElseIfStatement(@NotNull EpsilonParser.MainElseIfStatementContext ctx) {
		counter += 1;
		int elementAt = numberOfifElseCondition.pop();
		String previousElement = list.get(elementAt);
		previousElement = previousElement + (" " + (counter + 1));
		list.set(elementAt, previousElement);
		list.add("PUSH True");
		numberOfifElseEnd.add(counter);
		list.add("CONDTRUEGOTO");
		counter += 1;
	}
	
	@Override
	public void enterMainElseIfStatement(@NotNull EpsilonParser.MainElseIfStatementContext ctx) {
		numberOfIfElses.push(numberOfIfElses.pop() + 1);
	}

	@Override
	public void enterMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) {
		numberOfIfElses.push(1);
	}

	@Override
	public void exitMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) {
		counter += 1;
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
			counter += 1;
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
		counter += 1;
		list.add("SAVE " + ctx.IDENTIFIER());
	}

	@Override
	public void exitIdentifierAssignment(@NotNull EpsilonParser.IdentifierAssignmentContext ctx) {
		counter += 1;
		list.add("SAVE " + ctx.IDENTIFIER());
	}

	@Override
	public void exitPrint(@NotNull EpsilonParser.PrintContext ctx) {
		counter += 1;
		list.add("PRINT ");
	}

	// -------------- METHODS/FUNCTION constructs---------------------------
	@Override
	public void enterDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		counter += 1;
		list.add("DEFN " + ctx.IDENTIFIER());
		if (ctx.definitionParameters().identifierDeclaration() != null) {
			for (EpsilonParser.IdentifierDeclarationContext obj : ctx.definitionParameters().identifierDeclaration()) {
				counter += 1;
				list.add("SAVE " + obj.IDENTIFIER());
			}
		}
	}

	@Override
	public void exitDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		counter += 1;
		list.add("EXITDEFN " + ctx.IDENTIFIER());
	}

	@Override
	public void exitDefinitionReturn(@NotNull EpsilonParser.DefinitionReturnContext ctx) {

		if (ctx.IDENTIFIER() != null) {
			counter += 1;
			list.add("RETURN " + ctx.IDENTIFIER());
		} else if (ctx.NUMERIC() != null) {
			counter += 1;
			list.add("RETURN " + ctx.NUMERIC());
		} else if (ctx.BOOL() != null) {
			counter += 1;
			list.add("RETURN " + ctx.BOOL());
		} else if (ctx.STRING() != null) {
			counter += 1;
			list.add("RETURN " + ctx.STRING());
		} else if (ctx.CHARACTER() != null) {
			counter += 1;
			list.add("RETURN " + ctx.CHARACTER());
		}
		// recursion case
		else {
			counter += 1;
			list.add("SAVE temp");
			counter += 1;
			list.add("RETURN temp");
		}
	}

	@Override
	public void exitDefinitionInvocation(@NotNull EpsilonParser.DefinitionInvocationContext ctx) {
		counter += 1;
		List<EpsilonParser.ParametersContext> paramContextList = ctx.parameters();
		for(EpsilonParser.ParametersContext paramContext : paramContextList){
			if(paramContext.IDENTIFIER() != null){
				
				counter += 1;
				list.add("PARAM " + paramContext.IDENTIFIER());
			}
				
			else if (paramContext.NUMERIC() != null){
				
				counter += 1;
				list.add("PARAM " + paramContext.NUMERIC());
			}
				
			else if (paramContext.STRING() != null){
				counter += 1;
				list.add("PARAM " + paramContext.STRING());
			}
				
		} 
		counter += 1;
		list.add("INVOKE " + ctx.IDENTIFIER());
		

	
	}
	// ------------------End of function construct------------

	// --------------------Data types------------------------
	@Override
	public void enterDataType(@NotNull EpsilonParser.DataTypeContext ctx) {

		if (ctx.NUMERIC() != null) {
			counter += 1;
			list.add("PUSH " + ctx.NUMERIC());
		} else if (ctx.BOOL() != null) {
			counter += 1;
			list.add("PUSH " + ctx.BOOL());
		} else if (ctx.STRING() != null) {
			counter += 1;
			list.add("RETURN " + ctx.STRING());
		} else if (ctx.CHARACTER() != null) {
			counter += 1;
			list.add("RETURN " + ctx.CHARACTER());
		} else {
			counter += 1;
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
		numberOfIfElses.push(numberOfIfElses.pop() + 1);
	}

	public void exitIfelseStatement(@NotNull EpsilonParser.IfelseStatementContext ctx) {
		int numberOfIfElse = numberOfIfElses.pop();
		int i = 0;
		while (i < numberOfIfElse) {
			int elementAt = numberOfifElseEnd.pop();
			String previousElement = list.get(elementAt);
			previousElement = previousElement + (" " + (counter));
			list.set(elementAt, previousElement);
			i++;
		}
	}

	@Override
	public void exitPrefElseIf(@NotNull EpsilonParser.PrefElseIfContext ctx) {
		list.add("CONDFALSEGOTO");
		numberOfifElseCondition.push(counter);
		counter += 1;
	}

	@Override
	public void exitPrefIf(@NotNull EpsilonParser.PrefIfContext ctx) {
		list.add("CONDFALSEGOTO");
		numberOfifElseCondition.push(counter);
		counter += 1;
	}

	@Override
	public void exitIfStatement(@NotNull EpsilonParser.IfStatementContext ctx) {
		counter += 1;
		numberOfifElseEnd.add(counter);
		int eleAt = numberOfifElseCondition.pop();
		String prevElement = list.get(eleAt);
		prevElement = prevElement + (" " + (counter + 1));
		list.set(eleAt, prevElement);
		list.add("PUSH True");
		list.add("CONDTRUEGOTO");
		counter += 1;
	}

	@Override
	public void exitElseIfStatement(@NotNull EpsilonParser.ElseIfStatementContext ctx) {
		counter += 1;
		int elementAt = numberOfifElseCondition.pop();
		String previousElement = list.get(elementAt);
		previousElement = previousElement + (" " + (counter + 1));
		list.set(elementAt, previousElement);
		list.add("PUSH True");
		numberOfifElseEnd.add(counter);
		list.add("CONDTRUEGOTO");
		counter += 1;
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
		counter += 1;
	}

	@Override
	public void exitWhileIterator(@NotNull EpsilonParser.WhileIteratorContext ctx) {
		counter += 1;
		int elementAt = loopCondition.pop();
		String priorElement = list.get(elementAt);
		priorElement = priorElement + (" " + (counter + 1));
		list.set(elementAt, priorElement);
		list.add("PUSH True");
		list.add("CONDTRUEGOTO " + loopStart.pop());
		counter += 1;
	}

	// -------------------End of while loop construct------------------

	// ---------------Expression Construct------------------------
	@Override
	public void exitExpression(@NotNull EpsilonParser.ExpressionContext ctx) {

		if (ctx.NUMERIC() != null) {
			counter += 1;
			list.add("PUSH " + ctx.NUMERIC());
		}
		if (ctx.FLOAT() != null) {
			counter += 1;
			list.add("PUSH " + ctx.FLOAT());
		}
		if (ctx.IDENTIFIER() != null) {
			counter += 1;
			list.add("PUSH " + ctx.IDENTIFIER());
		}
		if (ctx.ADD() != null) {
			counter += 1;
			list.add("ADD");
		}
		if (ctx.SUB() != null) {
			counter += 1;
			list.add("SUB");
		}
		if (ctx.MUL() != null) {
			counter += 1;
			list.add("MUL");
		}
		if (ctx.DIV() != null) {
			counter += 1;
			list.add("DIV");
		}
		if (ctx.MODULO() != null) {
			counter += 1;
			list.add("MOD");
		}
		if (ctx.POWER() != null) {
			counter += 1;
			list.add("POW");
		}

	}

	// ----------Boolean and comparators-----------------
	@Override
	public void exitBoolExpression(@NotNull EpsilonParser.BoolExpressionContext ctx) {
		if (ctx.LOGICAND() != null) {
			counter += 1;
			list.add("AND");
		} else if (ctx.LOGICNOT() != null) {
			counter += 1;
			list.add("NOT");
		} else if (ctx.LOGICOR() != null) {
			counter += 1;
			list.add("OR");
		} else if (ctx.COMPARATORS() != null) {
			switch (ctx.COMPARATORS().toString()) {
			case ">":
				counter += 1;
				list.add("GREATER");
				break;
			case "<":
				counter += 1;
				list.add("LESSER");
				break;
			case ">=":
				counter += 1;
				list.add("GREATEREQUAL");
				break;
			case "<=":
				counter += 1;
				list.add("LESSEREQUAL");
				break;
			case "=":
				counter += 1;
				list.add("EQUALS");
				break;
			case "!=":
				counter += 1;
				list.add("UNEQUALS");
				break;
			default:
				break;
			}
		}
	}

}
