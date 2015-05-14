package jbse.algo.meta;

import static jbse.algo.Util.exitFromAlgorithm;
import static jbse.algo.Util.ensureStringLiteral;
import static jbse.algo.Util.failExecution;
import static jbse.algo.Util.throwVerifyError;
import static jbse.algo.Util.valueString;

import java.util.function.Supplier;

import jbse.algo.InterruptException;
import jbse.algo.StrategyUpdate;
import jbse.algo.exc.SymbolicValueNotAllowedException;
import jbse.common.exc.ClasspathException;
import jbse.dec.exc.DecisionException;
import jbse.mem.State;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.tree.DecisionAlternative_NONE;
import jbse.val.Reference;

public final class Algo_JAVA_STRING_INTERN extends Algo_INVOKEMETA {
    public Algo_JAVA_STRING_INTERN() {
        super(false);
    }
    
    private String valueString; //set by cookMore
    
    @Override
    protected Supplier<Integer> numOperands() {
        return () -> 1;
    }
    
    @Override
    protected void cookMore(State state) 
    throws DecisionException, ClasspathException, 
    SymbolicValueNotAllowedException, InterruptException {
        super.cookMore(state);
        try {
            this.valueString = valueString(state, (Reference) this.data.operand(0));
            if (this.valueString == null) {
                //TODO remove this limitation
                throw new SymbolicValueNotAllowedException("Cannot intern a symbolic String object.");
            }
            if (state.hasStringLiteral(this.valueString)) {
                //nothing to do
            } else {
                ensureStringLiteral(state, this.valueString, this.ctx.decisionProcedure);
            }
        } catch (ClassCastException e) {
            throwVerifyError(state);
            exitFromAlgorithm();
        } catch (ThreadStackEmptyException e) {
            failExecution(e);
        }
    }
    
    @Override
    protected StrategyUpdate<DecisionAlternative_NONE> updater() {
        return (state, alt) -> {
            state.pushOperand(state.referenceToStringLiteral(this.valueString));
        };
    }
}
