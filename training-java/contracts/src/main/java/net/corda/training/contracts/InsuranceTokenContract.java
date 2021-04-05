package net.corda.training.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.training.states.InsuranceTokenType;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class InsuranceTokenContract extends EvolvableTokenContract implements Contract {

//    @Override
//    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
//        InsuranceTokenType outputState = (InsuranceTokenType) tx.getOutput(0);
//        if (!(tx.getCommand(0).getSigners().contains(outputState.getIssuer().getOwningKey())))
//            throw new IllegalArgumentException("Issuer Signature required.");
//    }

    @Override
    public void additionalCreateChecks(@NotNull final LedgerTransaction tx) {

        final InsuranceTokenType outputInsuranceTokenType = tx.outputsOfType(InsuranceTokenType.class).get(0);
        requireThat(require -> {
            require.using("Mileage cannot be negative.",
                    outputInsuranceTokenType.getMileage() >= 0);
            require.using("Price cannot be 0.",
                    outputInsuranceTokenType.getPrice() > 0);
            return null;
        });
    }

    @Override
    public void additionalUpdateChecks(@NotNull final LedgerTransaction tx) {
        final InsuranceTokenType inputInsuranceTokenType = tx.inputsOfType(InsuranceTokenType.class).get(0);
        final InsuranceTokenType outputInsuranceTokenType = tx.outputsOfType(InsuranceTokenType.class).get(0);

        requireThat(require -> {
            require.using("Registration Number Cannot be updated.",
                    outputInsuranceTokenType.getRegNo().equals(inputInsuranceTokenType.getRegNo()));
            require.using("Make Cannot be updated.",
                    outputInsuranceTokenType.getMake().equals(inputInsuranceTokenType.getMake()));
            require.using("Model Cannot be updated.",
                    outputInsuranceTokenType.getModel().equals(inputInsuranceTokenType.getModel()));
            require.using("Mileage Cannot be reduced.",
                    outputInsuranceTokenType.getMileage() >= inputInsuranceTokenType.getMileage());
            require.using("Price Cannot be 0.",
                    outputInsuranceTokenType.getPrice() > 0);

            return null;
        });

    }
}