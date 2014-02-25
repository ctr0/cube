package c3c.cube;

import java.io.FileNotFoundException;
import java.io.IOException;

import c3c.ast.Annotation;
import c3c.ast.BinaryExpr;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.ComplUnit;
import c3c.ast.IExpr;
import c3c.ast.LexExpr;
import c3c.ast.NewExpr;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.ast.visitor.ExprVisitor;

public abstract class CubePrep extends Parser {
	
	public CubePrep(ComplUnit unit) throws FileNotFoundException, IOException {
		super(unit);
	}

	@Override
	public void consume() {
		super.consume();
		for (;;) {
			switch (id()) {
			case Id.__SHARP:
				Annotation anno = parseAnnotation(getLexemeAndConsumeToken());
				if (anno != null) {
					addAnnotation(anno);
				}
				continue;
			case Id.__INCLUDE:
				Annotation include = parseAnnotation(getAndConsumeToken());
				continue;
			case Id.__IF:
				Annotation dir = parseAnnotation(getAndConsumeToken());
				IExpr expr = dir.getExpr();
				if (expr != null && eval(expr)) {
					return; // process conditional if
				}
				skipBlockDir();
				if (id() == Id._ELSE) {
					return; // process conditional else
				}
				break;
			case Id.__ENDIF:
				break;
			}
			return;
		}
	}

	protected abstract Annotation parseAnnotation(IToken token);
	
	private boolean eval(IExpr expr) {
		
		return false;
	}
	
	private void skipBlockDir() {
		int ident = 1;
		while (ident > 0) {
			super.consume();
			switch (id()) {
				case Id.__IF:
				case Id.__IFDEF:
				case Id.__IFNDEF: 	ident++; 		continue;
				case Id.__ELSE: 	if (ident > 1) 	continue;
				//$FALL-THROUGH$
				case Id.__ENDIF: 	ident--;		continue;
				default: 							continue;
				case Id._EOF: 						return;
		}	}
		super.consume();
	}
	
	private static class ExprEvaluator implements ExprVisitor {

		@Override
		public void visitExpr(Lexeme expr) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visitExpr(LexExpr expr) {
			
		}

		@Override
		public CallExprVisitor visitExpr(CallExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UnaryExprVisitor visitExpr(UnaryExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BinaryExprVisitor visitExpr(BinaryExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TernaryExprVisitor visitExpr(TernaryExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CastExprVisitor visitExpr(CastExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public NewExprVisitor visitExpr(NewExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
