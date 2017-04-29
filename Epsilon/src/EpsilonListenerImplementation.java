
import java.util.*;

import org.antlr.v4.runtime.misc.NotNull;

public class EpsilonListenerImplementation extends EpsilonBaseListener{

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
		if(mainCnt<1){
			ind.add("");
			mainCnt++;
		}
	}
	
	@Override
	public void exitStart(@NotNull EpsilonParser.StartContext ctx) {
		ind.add("EXIT");
	}
	
	@Override 
	public void exitPrint(@NotNull EpsilonParser.PrintContext ctx) { 
		lineCount++;
		ind.add("PRINT");
	}
	
	@Override 
	public void enterDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) { 
		lineCount++;
		ind.add("DEFN" + ctx.IDENTIFIER());
		if(ctx.definitionParameters().identifierDeclaration() != null){
			for(EpsilonParser.IdentifierDeclarationContext obj : ctx.definitionParameters().identifierDeclaration()){
				System.out.println(obj.IDENTIFIER());
				lineCount++;
				ind.add("SAVE" + obj.IDENTIFIER());
			}
		}		
	}
	
	@Override 
	public void exitDefinitionDeclaration(@NotNull EpsilonParser.DefinitionDeclarationContext ctx) {
		lineCount++;
		ind.add("EXITDEFN");
	}
	
	@Override 
	public void exitDefinitionInvocation(@NotNull EpsilonParser.DefinitionInvocationContext ctx) {
		lineCount++;
		System.out.println(ctx.IDENTIFIER());
		ind.add("INVOKE" + ctx.IDENTIFIER());
	}
	
	@Override 
	public void enterDataType(@NotNull EpsilonParser.DataTypeContext ctx) {
		lineCount++;
		if(ctx.NUMERIC() != null){
			ind.add("PUSH" + ctx.NUMERIC());
		}
		else if (ctx.BOOL()!=null) {
			ind.add("PUSH" + ctx.BOOL());
		}
		else{
			ind.add("PUSH" + ctx.IDENTIFIER());
		}
	}
	
	@Override 
	public void exitDefinitionReturn(@NotNull EpsilonParser.DefinitionReturnContext ctx) {
		lineCount++;
		if(ctx.IDENTIFIER()!=null){
			ind.add("RETURN" + ctx.IDENTIFIER());
		}
		else if (ctx.NUMERIC()!=null) {
			ind.add("RETURN" + ctx.NUMERIC());
		}
		else if (ctx.BOOL() != null) {
			ind.add("RETURN" + ctx.BOOL());
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
	public void exitIfelseStatement(@NotNull EpsilonParser.IfelseStatementContext ctx) {
		Integer count = ifElseCount.pop();
		for(int i=0;i<count;i++){
			Integer pos = ifElseEnd.pop();
			String prev = ind.get(pos);
			prev += " " + (lineCount);
			ind.set(pos, prev);			
		}
	}
	
	@Override 
	public void enterPrefElseIf(@NotNull EpsilonParser.PrefElseIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
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
		Integer pos = ifElseCondition.pop();
		String prev = ind.get(pos);
		prev += " " + (lineCount + 1);
		ind.set(pos, prev);
		lineCount++;
		}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
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
		Integer pos = ifElseCondition.pop();
		String prev = ind.get(pos);
		prev += " " + (lineCount + 1);
		ind.set(pos, prev);
		lineCount++;		
	}
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
	public void exitPrefIf(@NotNull EpsilonParser.PrefIfContext ctx) {
		ind.add("CONDFALSEGOTO");
		ifElseCondition.push(lineCount);		
		lineCount++;
	}
	
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
	
	
}
