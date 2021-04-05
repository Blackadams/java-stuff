package net.corda.training.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens;
import net.corda.training.states.InsuranceTokenType;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

@StartableByRPC
@InitiatingFlow
public class RedeemTokenFlow extends FlowLogic<SignedTransaction> {

    private final String regNo;
    private final int amount;
    private final Party provider;

    public RedeemTokenFlow(String regNo, int amount, Party provider) {
        this.regNo = regNo;
        this.amount = amount;
        this.provider = provider;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        StateAndRef<InsuranceTokenType> stateAndRef = getServiceHub().getVaultService()
                .queryBy(InsuranceTokenType.class).getStates().stream()
                .filter(sf -> sf.getState().getData().getRegNo().equals(regNo)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Registration Number "+ regNo + " not found from vault."));

        InsuranceTokenType insuranceTokenType = stateAndRef.getState().getData();

        TokenPointer tokenPointer = insuranceTokenType.toPointer(InsuranceTokenType.class);

        Amount quantity = new Amount(amount, tokenPointer);

        return subFlow(new RedeemFungibleTokens(quantity, provider));
    }
}
