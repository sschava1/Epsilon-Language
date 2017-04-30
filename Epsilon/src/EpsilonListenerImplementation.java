
import java.util.*;

import org.antlr.v4.runtime.misc.NotNull;

public class EpsilonListenerImplementation extends EpsilonBaseListener {
	
	List<String> ind = new ArrayList<String>();	
	Stack<Integer> loopStart = new Stack<Integer>();
	Stack<Integer> loopCondition = new Stack<Integer>();
	Stack<Integer> ifElseCount = new Stack<Integer>();
	Stack<Integer> ifElseCondition = new Stack<Integer>();
	Stack<Integer> ifElseEnd = new Stack<Integer>();
	int mainCnt = 0;
	int lineCount = 1;

	@Override
	public void enterStart(@NotNull EpsilonParser.StartContext ctx) {
		if (mainCnt < 1) {
			ind.add("");
			mainCnt++;
		}
	}

	@Override
	public void exitStart(@NotNull EpsilonParser.StartContext ctx) {
		ind.add("EXIT ");
	}

	@Override
	public void exitPrint(@NotNull EpsilonParser.PrintContext ctx) {
		lineCount++;
		ind.add("PRINT ");
	}

	@Override
	public void enterDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		lineCount++;
		ind.add("DEFN " + ctx.IDENTIFIER());
		if (ctx.definitionParameters().identifierDeclaration() != null) {
			for (EpsilonParser.IdentifierDeclarationContext obj : ctx.definitionParameters().identifierDeclaration()) {
				System.out.println(obj.IDENTIFIER());
				lineCount++;
				ind.add("SAVE " + obj.IDENTIFIER());
			}
		}
	}

	@Override
	public void exitDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		lineCount++;
		ind.add("EXITDEFN "+ ctx.IDENTIFIER());
	}

	@Override
	public void exitDefinitionInvocation(@NotNull EpsilonParser.DefinitionInvocationContext ctx) {
		lineCount++;
		System.out.println(ctx.IDENTIFIER());
		ind.add("INVOKE " + ctx.IDENTIFIER());
		lineCount++;
		ind.add(" "+ctx.parameters());
	}

	@Override
	public void enterDataType(@NotNull EpsilonParser.DataTypeContext ctx) {
		
		if (ctx.NUMERIC() != null) {
			lineCount++;
			ind.add("PUSH " + ctx.NUMERIC());
		} else if (ctx.BOOL() != null) {
			lineCount++;
			ind.add("PUSH " + ctx.BOOL());
		} else {
			lineCount++;
			ind.add("PUSH " + ctx.IDENTIFIER());
		}
	}

	@Override
	public void exitDefinitionReturn(@NotNull EpsilonParser.DefinitionReturnContext ctx) {
		
		if (ctx.IDENTIFIER() != null) {
			lineCount++;
			ind.add("RETURN " + ctx.IDENTIFIER());
		} else if (ctx.NUMERIC() != null) {
			lineCount++;
			ind.add("RETURN " + ctx.NUMERIC());
		} else if (ctx.BOOL() != null) {
			lineCount++;
			ind.add("RETURN " + ctx.BOOL());
		} else {
			lineCount++;
			ind.add("SAVE temp");
			lineCount++;
			ind.add("RETURN temp");
		}
	}

	@Override
	public void enterMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) {
		ifElseCount.push(1);
	}

	@Override
	public void exitMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) {
		lineCount++;
		ind.add("PUSH True");		
		ind.add("CONDTRUEGOTO");
		ifElseEnd.add(lineCount);		
		Integer index = ifElseCondition.pop();
		String previous = ind.get(index);
		previous += " " + (lineCount + 1);
		ind.set(index, previous);
		lineCount++;
	}

	@Override
	public void enterIfelseStatement(@NotNull EpsilonParser.IfelseStatementContext ctx) {
	}

	public void exitIfelseStatement(@NotNull EpsilonParser.IfelseStatementContext ctx) {
		Integer count = ifElseCount.pop();
		for(int i=0;i<count;i++){
			Integer index = ifElseEnd.pop();
			String previous = ind.get(index);
			previous += " " + (lineCount);
			ind.set(index, previous);			
		}
	}
	
	@Override
	public void enterMainIfElseStatement(@NotNull EpsilonParser.MainIfElseStatementContext ctx) { }
	
	@Override
	public void exitMainIfElseStatement(@NotNull EpsilonParser.MainIfElseStatementContext ctx) {
		Integer count = ifElseCount.pop();
		for(int i=0;i<count;i++){
			Integer position = ifElseEnd.pop();
			String previous = ind.get(position);
			previous += " " + (lineCount);
			ind.set(position, previous);
	}
	}
	
	@Override 
	public void enterPrefElseIf(@NotNull EpsilonParser.PrefElseIfContext ctx) { }


	@Override 
	public void exitPrefElseIf(@NotNull EpsilonParser.PrefElseIfContext ctx) {
		ind.add("CONDFALSEGOTO");
		ifElseCondition.push(lineCount);
		lineCount++;
	}
	
	@Override 
	public void enterIfStatement(@NotNull EpsilonParser.IfStatementContext ctx) {
		ifElseCount.push(1);
	}
	
	@Override 
	public void exitIfStatement(@NotNull EpsilonParser.IfStatementContext ctx) {
		lineCount++;
		ind.add("PUSH True");		
		ind.add("CONDTRUEGOTO");
		ifElseEnd.add(lineCount);		
		Integer index = ifElseCondition.pop();
		String previous = ind.get(index);
		previous += " " + (lineCount + 1);
		ind.set(index, previous);
		lineCount++;
		}
	
	@Override 
	public void enterElseIfStatement(@NotNull EpsilonParser.ElseIfStatementContext ctx) {
		Integer cur = ifElseCount.pop();
    	ifElseCount.push(1 + cur);
	}
	
	@Override 
	public void exitElseIfStatement(@NotNull EpsilonParser.ElseIfStatementContext ctx) { 
		lineCount++;
		ind.add("PUSH True");		
		ifElseEnd.add(lineCount);
		ind.add("CONDTRUEGOTO");
		Integer index = ifElseCondition.pop();
		String previous = ind.get(index);
		previous += " " + (lineCount + 1);
		ind.set(index, previous);
		lineCount++;		
	}
	
	@Override
	public void enterPrefIf(@NotNull EpsilonParser.PrefIfContext ctx) {
	}


	@Override 
	public void exitPrefIf(@NotNull EpsilonParser.PrefIfContext ctx) {
		ind.add("CONDFALSEGOTO");
		ifElseCondition.push(lineCount);		
		lineCount++;
	}
	
	@Override 
	public void enterElseStatement(@NotNull EpsilonParser.ElseStatementContext ctx) { }

	@Override
	public void exitElseStatement(@NotNull EpsilonParser.ElseStatementContext ctx) {
	}

	
	@Override
	public void enterMainElseStatement(@NotNull EpsilonParser.MainElseStatementContext ctx) {
	}

	@Override
	public void exitMainElseStatement(@NotNull EpsilonParser.MainElseStatementContext ctx) {
	}

	@Override public void enterWhileIterator(@NotNull EpsilonParser.WhileIteratorContext ctx) {
		loopStart.push(lineCount);
	}
	
	
	@Override public void enterMainWhileIterator(@NotNull EpsilonParser.MainWhileIteratorContext ctx) {
		loopStart.push(lineCount);
		}

	@Override
	public void enterIdentifierDeclarationAssignment(
			@NotNull EpsilonParser.IdentifierDeclarationAssignmentContext ctx) {

	}

	@Override
	public void exitIdentifierDeclarationAssignment(@NotNull EpsilonParser.IdentifierDeclarationAssignmentContext ctx) {
		lineCount++;
		ind.add("SAVE " + ctx.IDENTIFIER());
	}

	@Override
	public void enterIdentifierAssignment(@NotNull EpsilonParser.IdentifierAssignmentContext ctx) {

	}
	

	@Override
	public void exitIdentifierAssignment(@NotNull EpsilonParser.IdentifierAssignmentContext ctx) {
		lineCount++;
		ind.add("SAVE " + ctx.IDENTIFIER());
	}

	@Override
	public void enterExpression(@NotNull EpsilonParser.ExpressionContext ctx) {

	}

	@Override
	public void exitExpression(@NotNull EpsilonParser.ExpressionContext ctx) {

		if(ctx.NUMERIC()!= null){
			lineCount++;
			ind.add("PUSH "+ ctx.NUMERIC());
		}

		if (ctx.FLOAT() != null){
			lineCount++;
			ind.add("PUSH "+ ctx.FLOAT());
		}

		if(ctx.IDENTIFIER() != null) {
			lineCount++;
			ind.add("PUSH "+ ctx.IDENTIFIER());
		}	
		if(ctx.ADD() != null) {
			lineCount++;
			ind.add("ADD");
		} 
		if (ctx.SUB() != null) {
			lineCount++;
			ind.add("SUB");
		} 
		if (ctx.MUL() != null) {
			lineCount++;
			ind.add("MUL");
		}
		if (ctx.DIV() != null) {
			lineCount++;
			ind.add("DIV");
		}
		if (ctx.MODULO() != null) {
			lineCount++;
			ind.add("MOD");
		}
		if (ctx.POWER() != null) {
			lineCount++;
			ind.add("POW");
		}	

	}

	
	@Override 
	public void exitWhileIterator(@NotNull EpsilonParser.WhileIteratorContext ctx) {
		lineCount++;
		ind.add("PUSH True");		
		ind.add("CONDTRUEGOTO " + loopStart.pop());
		Integer index = loopCondition.pop();
		String previous = ind.get(index);
		previous += " " + (lineCount + 1);
		ind.set(index, previous);
		lineCount++;
	}
	
	@Override 
	public void exitWhilePrefix(@NotNull EpsilonParser.WhilePrefixContext ctx) {
		loopCondition.push(lineCount);
		ind.add("CONDFALSEGOTO");
		lineCount++;
	}

	@Override public void enterMainWhileIterator(@NotNull EpsilonParser.MainWhileIteratorContext ctx) {
		loopStart.push(lineCount);
	}
	
	@Override
	public void enterBoolExpression(@NotNull EpsilonParser.BoolExpressionContext ctx) {
	}

	@Override
	public void exitBoolExpression(@NotNull EpsilonParser.BoolExpressionContext ctx) {
		if (ctx.LOGICAND() != null) {
			lineCount++;
			ind.add("AND");
		} else if (ctx.LOGICNOT() != null) {
			lineCount++;
			ind.add("NOT");
		} else if (ctx.LOGICOR() != null) {
			lineCount++;
			ind.add("OR");
		} else if (ctx.COMPARATORS() != null) {
			switch (ctx.COMPARATORS().toString()) {
			case ">":
				lineCount++;
				ind.add("GREATER");
				break;
			case "<":
				lineCount++;
				ind.add("LESSER");
				break;
			case ">=":
				lineCount++;
				ind.add("GREATEREQUAL");
				break;
			case "<=":
				lineCount++;
				ind.add("LESSEREQUAL");
				break;
			case "=":
				lineCount++;
				ind.add("EQUALS");
				break;
			}
		}
	}

	@Override
	public void enterMainDefinitionDeclaration(@NotNull EpsilonParser.MainDefinitionDeclarationContext ctx) { 
		
		lineCount++;
		ind.add("DEFN main");
	}
	
	@Override 
	public void exitMainDefinitionDeclaration(@NotNull EpsilonParser.MainDefinitionDeclarationContext ctx) {
		lineCount++;
		ind.add("EXITDEFN main");
	}
	
	
}
