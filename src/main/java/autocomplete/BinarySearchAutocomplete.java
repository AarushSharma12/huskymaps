package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Binary search implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class BinarySearchAutocomplete implements Autocomplete {
    /**
     * {@link List} of added autocompletion terms.
     */
    private final List<CharSequence> elements;

    /**
     * Constructs an empty instance.
     */
    public BinarySearchAutocomplete() {
        elements = new ArrayList<>();
    }

    // @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        elements.addAll(terms);
        elements.sort(CharSequence::compare);
    }

    // @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        List<CharSequence> result = new ArrayList<>();
        int i = Collections.binarySearch(elements, prefix, CharSequence::compare);

        int start = i;
        if (i < 0) {
            start = -(start + 1);
        }

        boolean running = true;
        while(running){
            result.add(elements.get(start));
            if(!Autocomplete.isPrefixOf(prefix, elements.get(start+1))){
                running = false;
            }
            start++;
        }
        return result;
    }
}
