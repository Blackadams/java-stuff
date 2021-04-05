package net.corda.training.states;

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import net.corda.training.contracts.InsuranceTokenContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

// *********
// * State *
// *********
@BelongsToContract(InsuranceTokenContract.class)
public class InsuranceTokenType extends EvolvableTokenType {

    //private variables
    public static final int FRACTION_DIGITS = 0;
    @NotNull
    private final List<Party> maintainers;
    @NotNull
    private final UniqueIdentifier uniqueIdentifier;
    @NotNull
    private final String RegNo;
    @NotNull
    private final String make;
    @NotNull
    private final String model;
    private final long mileage;
    private long price;
    private final Party issuer;



    /* Constructor of your Corda state */
    public InsuranceTokenType(@NotNull List<Party> maintainers, @NotNull UniqueIdentifier uniqueIdentifier,
                              @NotNull String regNo, @NotNull String make, @NotNull String model,
                              long mileage, long price, Party issuer) {
        Validate.notNull(maintainers, "Maintainer cannot be empty.");
        Validate.notNull(issuer, "Issuer cannot be empty.");
        Validate.notNull(uniqueIdentifier, "Unique identifier cannot be empty.");
        Validate.notBlank(regNo, "Registration Number cannot be empty.");
        Validate.notBlank(make, "Make cannot be empty.");
        Validate.notBlank(model, "Model cannot be empty.");
        Validate.isTrue(mileage >=0 , "Mileage cannot be negative.");
        Validate.isTrue(price > 0, "Price cannot be 0.");
        this.maintainers = maintainers;
        this.issuer = issuer;
        this.uniqueIdentifier = uniqueIdentifier;
        this.RegNo = regNo;
        this.make = make;
        this.model = model;
        this.mileage = mileage;
        this.price = price;

    }

    //getters
    @Override
    public int getFractionDigits() {
        return FRACTION_DIGITS;
    }

    @Override
    @NotNull
    public List<Party> getMaintainers() {
        return maintainers;
    }

    @NotNull
    public UniqueIdentifier getLinearId() {
        return uniqueIdentifier;
    }

    @NotNull
    public String getRegNo() {
        return RegNo;
    }

    @NotNull
    public String getMake() {
        return make;
    }

    @NotNull
    public String getModel() {
        return model;
    }

    public long getMileage() {
        return mileage;
    }

    public long getPrice() {
        return price;
    }

    public Party getIssuer() {
        return issuer;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InsuranceTokenType that = (InsuranceTokenType) o;
        return getFractionDigits() == that.getFractionDigits() &&
                Double.compare(that.getMileage(), getMileage()) == 0 &&
                Double.compare(that.getPrice(),getPrice()) == 0 &&
                maintainers.equals(that.maintainers) &&
                uniqueIdentifier.equals(that.uniqueIdentifier) &&
                getRegNo().equals(that.getRegNo()) &&
                getMake().equals(that.getMake()) &&
                getModel().equals(that.getModel()) &&
                issuer.equals(that.issuer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFractionDigits(), maintainers, uniqueIdentifier, getRegNo(), getMake(),
                getModel(), getMileage(), getPrice(), issuer);
    }

    public TokenPointer<InsuranceTokenType> toPointer() {
        LinearPointer<InsuranceTokenType> linearPointer = new LinearPointer<>(this.uniqueIdentifier, InsuranceTokenType.class);
        return new TokenPointer<>(linearPointer, FRACTION_DIGITS);
    }
}