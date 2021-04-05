package net.corda.training.flows;


import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokensHandler;
import net.corda.training.states.InsuranceTokenType;
import kotlin.Unit;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;
import java.util.UUID;

@InitiatingFlow
@StartableByRPC
public class SpendTokenFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class SpendTokenFlowInitiator extends FlowLogic<String> {
        private final String tokenId;
        private final long amount;
        private final Party provider;


        public SpendTokenFlowInitiator(String tokenId, long amount, Party provider) {
            this.tokenId = tokenId;
            this.amount = amount;
            this.provider = provider;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            UUID uuid = UUID.fromString(tokenId);

            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                    null, Arrays.asList(uuid), null, Vault.StateStatus.UNCONSUMED
            );

            StateAndRef<InsuranceTokenType> tokenTypeStateAndRef = getServiceHub().getVaultService()
                    .queryBy(InsuranceTokenType.class, queryCriteria).getStates().get(0);

            InsuranceTokenType insuranceTokenState = tokenTypeStateAndRef.getState().getData();

            Amount<TokenType> quantity = new Amount(amount, insuranceTokenState.toPointer());

            SignedTransaction stx = subFlow(new MoveFungibleTokens(quantity, provider));
            return "\nSpend "+this.amount +" insurance tokens to"
                    + this.provider.getName().getOrganisation() +".\nTransaction ID: "+stx.getId();
        }
    }
    @InitiatedBy(SpendTokenFlowInitiator.class)
    public static class SpendTokenFlowResponder extends FlowLogic<Unit> {
        private FlowSession counterSession;

        public SpendTokenFlowResponder(FlowSession counterSession) {
            this.counterSession = counterSession;
        }

        @Suspendable
        @Override
        public Unit call() throws FlowException {
            return subFlow(new MoveFungibleTokensHandler(counterSession));
        }
    }

}
