package net.corda.training.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import net.corda.training.states.InsuranceTokenType;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CreateTokenFlow extends FlowLogic<SignedTransaction> {

    @NotNull
    private final Party notary;
    @NotNull
    private final String RegNo;
    @NotNull
    private final String make;
    @NotNull
    private final String model;
    private final long price;
    private final long mileage;
    private final Party issuer;
    @NotNull
    private final List<Party> observers;


    public CreateTokenFlow(@NotNull Party notary, @NotNull String regNo,
                           @NotNull String make, @NotNull String model, long price,
                           long mileage, Party issuer, @NotNull List<Party> observers) {
        this.notary = notary;
        this.RegNo = regNo;
        this.make = make;
        this.model = model;
        this.price = price;
        this.mileage = mileage;
        this.issuer = issuer;
        this.observers = observers;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        final Party techdomain = getOurIdentity();
//        if (!techdomain.getName().equals(TokenTypeConstants.TECH_DOMAIN)) {
//            throw new FlowException("Get token issued from Tech Domain.");
//        }
        final InsuranceTokenType newCar = new InsuranceTokenType(Collections.singletonList(techdomain),
                new UniqueIdentifier(), RegNo, make, model, mileage, price, issuer);

        final TransactionState<InsuranceTokenType> txState = new TransactionState<>(newCar, notary);
        return subFlow(new CreateEvolvableTokens(txState, observers));
    }
}
