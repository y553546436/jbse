package jbse.algo.meta;

import static jbse.algo.Util.failExecution;
import static jbse.algo.Util.storeInArray;
import static jbse.bc.Offsets.INVOKESPECIALSTATICVIRTUAL_OFFSET;
import static jbse.common.Type.INT;

import java.util.function.Supplier;

import jbse.algo.Algo_INVOKEMETA;
import jbse.algo.BytecodeCooker;
import jbse.algo.StrategyDecide;
import jbse.algo.StrategyRefine;
import jbse.algo.StrategyUpdate;
import jbse.algo.meta.exc.UndefinedResultException;
import jbse.bc.ClassFile;
import jbse.bc.ClassHierarchy;
import jbse.dec.DecisionProcedureAlgorithms.Outcome;
import jbse.mem.Array;
import jbse.mem.Objekt;
import jbse.tree.DecisionAlternative_XASTORE;
import jbse.val.Calculator;
import jbse.val.Primitive;
import jbse.val.Reference;
import jbse.val.Simplex;
import jbse.val.Value;
import jbse.val.exc.InvalidTypeException;

/**
 * Meta-level implementation of {@link sun.misc.Unsafe#putObjectVolatile(Object, long, Object)}
 * in the case the object to write to is an array.
 * 
 * @author Pietro Braione
 */
public final class Algo_SUN_UNSAFE_PUTOBJECT_O_Array extends Algo_INVOKEMETA<
DecisionAlternative_XASTORE,
StrategyDecide<DecisionAlternative_XASTORE>, 
StrategyRefine<DecisionAlternative_XASTORE>, 
StrategyUpdate<DecisionAlternative_XASTORE>> {

    private Reference arrayReference; //set by cooker
    private Simplex index; //set by cooker
    private Value valueToStore; //set by cooker
    private Primitive inRange, outOfRange; //set by cooker
    
    @Override
    protected Supplier<Integer> numOperands() {
        return () -> 4;
    }
    
    @Override
    protected BytecodeCooker bytecodeCooker() {
        return (state) -> { 
        	final Calculator calc = this.ctx.getCalculator();
            try {
                this.arrayReference = (Reference) this.data.operand(1);
                this.index = (Simplex) calc.push((Simplex) this.data.operand(2)).narrow(INT).pop();            
                this.valueToStore = this.data.operand(3);
                final Array array = (Array) state.getObject(this.arrayReference);
                this.inRange = array.inRange(calc, this.index);
                this.outOfRange = array.outOfRange(calc, this.index);

                //checks
                final ClassFile arrayMemberType = array.getType().getMemberClass();
                if (arrayMemberType.isReference() || arrayMemberType.isArray()) {
                    final Reference valueToStoreRef = (Reference) this.valueToStore;
                    final Objekt o = state.getObject(valueToStoreRef);
                    final ClassHierarchy hier = state.getClassHierarchy();
                    if (!state.isNull(valueToStoreRef) &&
                        !hier.isAssignmentCompatible(o.getType(), arrayMemberType)) {
                        throw new UndefinedResultException("The Object x parameter to sun.misc.Unsafe.putObjectXxxx was not assignment-compatible with the Object o (array) parameter.");
                    }
                } else {
                    throw new UndefinedResultException("The Object o parameter to sun.misc.Unsafe.putObjectXxxx was an array whose member type is not a reference.");
                }
            } catch (ClassCastException | InvalidTypeException e) {
                //this should never happen now
                failExecution(e);
            }
        };
    }
    
    @Override
    protected Class<DecisionAlternative_XASTORE> classDecisionAlternative() {
        return DecisionAlternative_XASTORE.class;
    }
    
    @Override
    protected StrategyDecide<DecisionAlternative_XASTORE> decider() {
        return (state, result) -> {
            final Outcome o = this.ctx.decisionProcedure.decide_XASTORE(this.inRange, result);
            return o;
        };
    }

    @Override
    protected StrategyRefine<DecisionAlternative_XASTORE> refiner() {
        return (state, alt) -> {
            state.assume(this.ctx.getCalculator().simplify(this.ctx.decisionProcedure.simplify(alt.isInRange() ? this.inRange : this.outOfRange)));
        };
    }

    @Override
    protected StrategyUpdate<DecisionAlternative_XASTORE> updater() {
        return (state, alt) -> {
            if (alt.isInRange()) {
                storeInArray(state, this.ctx, this.arrayReference, this.index, this.valueToStore);
            } else {
                throw new UndefinedResultException("The long offset parameter to sun.misc.Unsafe.putObjectXxxx was out of range w.r.t. the Object o (array) parameter.");
            }
        };
    }

    @Override
    protected Supplier<Boolean> isProgramCounterUpdateAnOffset() {
        return () -> true;
    }

    @Override
    protected Supplier<Integer> programCounterUpdate() {
        return () -> INVOKESPECIALSTATICVIRTUAL_OFFSET;
    }
}
