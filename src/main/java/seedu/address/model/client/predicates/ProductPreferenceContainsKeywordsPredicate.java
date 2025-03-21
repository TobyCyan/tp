package seedu.address.model.client.predicates;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.client.Client;
import seedu.address.model.client.ProductPreference;

/**
 * Tests that a {@code Client}'s {@code Product Preference} matches any of the keywords given.
 */
public class ProductPreferenceContainsKeywordsPredicate implements Predicate<Client> {
    private final List<String> keywords;

    public ProductPreferenceContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Client client) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(
                        client.getProductPreference().map(ProductPreference::toString).orElse(""),
                        keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ProductPreferenceContainsKeywordsPredicate)) {
            return false;
        }

        ProductPreferenceContainsKeywordsPredicate otherProductPreferenceContainsKeywordsPredicate =
                (ProductPreferenceContainsKeywordsPredicate) other;
        return keywords.equals(otherProductPreferenceContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
