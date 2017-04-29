
import java.util.*;

import org.antlr.v4.runtime.misc.NotNull;

public class EpsilonListenerImplementation extends EpsilonBaseListener{

	int mainCnt = 0;
	int lineCount = 1;
	
	@Override 
	public void enterStart(@NotNull EpsilonParser.StartContext ctx) {
		if(mainCnt<1){
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
		if(ctx.definitionParameters().identifierDeclaration() != null){
			for(EpsilonParser.IdentifierDeclarationContext obj : ctx.definitionParameters().identifierDeclaration()){
				System.out.println(obj.IDENTIFIER());
				lineCount++;
				ind.add("SAVE " + obj.IDENTIFIER());
			}
		}		
	}
	
	@Override 
	public void exitDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		lineCount++;
		ind.add("EXITDEFN ");
	}
	
	@Override 
	public void exitDefinitionInvocation(@NotNull EpsilonParser.DefinitionInvocationContext ctx) {
		lineCount++;
		System.out.println(ctx.IDENTIFIER());
		ind.add("INVOKE " + ctx.IDENTIFIER());
	}
	
	@Override 
	public void enterDataType(@NotNull EpsilonParser.DataTypeContext ctx) {
		lineCount++;
		if(ctx.NUMERIC() != null){
			ind.add("PUSH " + ctx.NUMERIC());
		}
		else if (ctx.BOOL()!=null) {
			ind.add("PUSH " + ctx.BOOL());
		}
		else{
			ind.add("PUSH " + ctx.IDENTIFIER());
		}
	}
	
	@Override 
	public void exitDefinitionReturn(@NotNull EpsilonParser.DefinitionReturnContext ctx) {
		lineCount++;
		if(ctx.IDENTIFIER()!=null){
			ind.add("RETURN " + ctx.IDENTIFIER());
		}
		else if (ctx.NUMERIC()!=null) {
			ind.add("RETURN " + ctx.NUMERIC());
		}
		else if (ctx.BOOL() != null) {
			ind.add("RETURN " + ctx.BOOL());
		}
		else
		{
			ind.add("SAVE temp");
			lineCount++;
			ind.add("RETURN temp");
		}
	}
	
	@Override 
	public void enterMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override
	public void exitMainIfStatement(@NotNull EpsilonParser.MainIfStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	
	@Override 
	public void enterIfelseStatement(@NotNull EpsilonParser.IfelseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void exitIfelseStatement(@NotNull EpsilonParser.IfelseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void enterPrefElseIf(@NotNull EpsilonParser.PrefElseIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void exitPrefElseIf(@NotNull EpsilonParser.PrefElseIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void enterIfStatement(@NotNull EpsilonParser.IfStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void exitIfStatement(@NotNull EpsilonParser.IfStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void enterElseIfStatement(@NotNull EpsilonParser.ElseIfStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void exitElseIfStatement(@NotNull EpsilonParser.ElseIfStatementContext ctx) { }
	/*
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	
	@Override 
	public void enterPrefIf(@NotNull EpsilonParser.PrefIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void exitPrefIf(@NotNull EpsilonParser.PrefIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void enterElseStatement(@NotNull EpsilonParser.ElseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void exitElseStatement(@NotNull EpsilonParser.ElseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */

	@Override 
	public void enterMainElseStatement(@NotNull EpsilonParser.MainElseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override 
	public void exitMainElseStatement(@NotNull EpsilonParser.MainElseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	
	@Override 
	public void enterIdentifierDeclarationAssignment(@NotNull EpsilonParser.IdentifierDeclarationAssignmentContext ctx) {
		
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

	@Override public void enterExpression(@NotNull EpsilonParser.ExpressionContext ctx) { 
		
	
	}

	@Override public void exitExpression(@NotNull EpsilonParser.ExpressionContext ctx) { 
		
		lineCount++;
		if(ctx.ADD()!=null){
			ind.add("ADD");
		}
		else if(ctx.SUB() != null){
			ind.add("SUB");
		}
		else if(ctx.MUL() != null){
			ind.add("MUL");
		}
		else if(ctx.DIV()!= null){
			ind.add("DIV");
		}
		else if(ctx.POWER() != null){
			ind.add("POW");
		}
				
	}
	
	@Override 
	public void enterBoolExpression(@NotNull EpsilonParser.BoolExpressionContext ctx) {
		
		lineCount++;
		if(ctx.LOGICAND() != null){
			ind.add("AND");
		}
		else if(ctx.LOGICNOT() != null){
			ind.add("NOT");
		}
		else if(ctx.LOGICOR()!=null){
			ind.add("OR");
		}
		else if(ctx.COMPARATORS()!=null){
			switch(ctx.COMPARATORS().toString()){
			case ">":
				ind.add("GREATER");
				break;
			case "<":
				ind.add("LESSER");
				break;
			case ">=":
				ind.add("GREATEREQUAL");
				break;
			case "<=":
				ind.add("LESSEREQUAL");
				break;
			case "==":
				ind.add("EQUALS");
				break;	
}
		}
	}

	@Override 
	public void exitBoolExpression(@NotNull EpsilonParser.BoolExpressionContext ctx) { 
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
