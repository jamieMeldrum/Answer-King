package matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.junit.Ignore;

import java.math.BigDecimal;

/**
 * Matcher that should be able to match BigDecimals and Doubles to a BigDecimal to see if they are numerically
 * equivalent. Use compareTo as equals on BigDecimal is documented as not working for numerical comparison.
 *
 * Note that I have not implemented this more than I have needed to/more than makes sense. I'm sure that this could be
 * extended to support matching to other Number type objects (and could maybe ever be made generic for Number) but I
 * don't think it is worth going down that route unless I have a reason to.
 */
public class BigDecimalMatcher extends DiagnosingMatcher<BigDecimal> {

    private BigDecimal val;

    public BigDecimalMatcher(BigDecimal val) {
        this.val = val;
    }

    @Override
    protected boolean matches(Object item, Description mismatchDescription) {

        mismatchDescription.appendText("Value was: " + item);

        if (item == null) {
            return false;
        }

        if (item instanceof BigDecimal) {
            int compareTo = val.compareTo((BigDecimal) item);
            return compareTo == 0;
        }
        else if (item instanceof Double) {
            BigDecimal valCompare = new BigDecimal((Double) item);
            int compareTo = val.compareTo(valCompare);
            return compareTo == 0;
        }
        else {
            return false;
        }
    }

    @Override
    public void describeTo(final Description mismatchDescription) {
        mismatchDescription.appendText("Expected value was " + val);
    }
}
