package matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.junit.Ignore;

/**
 * Hamcrest doesn't support comparison between Integer and Long for values alone and I reckon I will need to do that
 * a few times for matching on model ids to make my test complete. It might be a little unnecessary as I can work around
 * this but it is interesting to try this out so why not?
 *
 * Note that I have not implemented this more than I have needed to/more than makes sense. I'm sure that this could be
 * extended to support matching to other Number type objects (and could maybe ever be made generic for Number) but I
 * don't think it is worth going down that route unless I have a reason to.
 */
public class LongMatcher extends DiagnosingMatcher<Long> {

    private Long val;

    public LongMatcher(Long val) {
        this.val = val;
    }

    @Override
    public boolean matches(Object item, Description mismatchDescription) {

        mismatchDescription.appendText("Value was: " + item);

        if (item == null) {
            return false;
        }

        if (item instanceof Long) {
            return val.equals((Long) item);
        }
        else if (item instanceof Integer) {
            Long valCompare = new Long((Integer) item);
            return val.equals(valCompare);
        }
        else {
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Expected value was " + val);
    }
}
