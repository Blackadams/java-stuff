package net.corda.training.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.utilities.FungibleTokenBuilder;
import net.corda.training.states.InsuranceTokenType;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@InitiatingFlow
@StartableByRPC
public class IssueTokenFlow extends FlowLogic<SignedTransaction> {

    @NotNull
    private final InsuranceTokenType car;
    @NotNull
    private final Party insurance;
    @NotNull
    private final AbstractParty holder;
    @NotNull
    private int amount;


    public IssueTokenFlow(@NotNull InsuranceTokenType car, @NotNull Party insurance,
                          @NotNull AbstractParty holder, @NotNull int amount) {
        this.car = car;
        this.insurance = insurance;
        this.holder = holder;
        this.amount = amount;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        final TokenPointer<InsuranceTokenType> carPointer = car.toPointer(InsuranceTokenType.class);
        final IssuedTokenType carWithInsurance = new IssuedTokenType(insurance, carPointer);
        final FungibleToken insuranceToken = new FungibleTokenBuilder()
                .ofTokenType(carPointer)
                .withAmount(amount)
                .issuedBy(getOurIdentity())
                .heldBy(insurance)
                .buildFungibleToken();
       SignedTransaction stx = subFlow(new IssueTokens(Arrays.asList(insuranceToken)));
       return stx;
    }
}
