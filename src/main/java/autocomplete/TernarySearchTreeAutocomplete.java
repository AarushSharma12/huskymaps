package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Ternary search tree (TST) implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class TernarySearchTreeAutocomplete implements Autocomplete {
    /**
     * The overall root of the tree: the first character of the first autocompletion term added to this tree.
     */
    private Node overallRoot;
    private int n = 0;

    /**
     * Constructs an empty instance.
     */
    public TernarySearchTreeAutocomplete() {
        overallRoot = null;
    }

    public int size(){
        return n;
    }

    public boolean contains(CharSequence key){
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Node x = get(key);
        return x != null && x.isTerm;
    }

    private Node get(CharSequence key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return get(overallRoot, key, 0);
    }

    private Node get(Node x, CharSequence key, int d) {
        if (x == null) return null;
        char c = key.charAt(d);
        if (c < x.data) return get(x.left, key, d);
        else if (c > x.data) return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid, key, d + 1);
        else return x;
    }

    public void put(CharSequence key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        overallRoot = put(overallRoot, key, 0);
    }

    private Node put(Node x, CharSequence key, int d) {
        char c = key.charAt(d);

        if (x == null) {
            x = new Node(c);
        }

        if (c < x.data) {
            x.left = put(x.left, key, d);
        } else if (c > x.data) {
            x.right = put(x.right, key, d);
        } else if (d < key.length() - 1) {
            x.mid = put(x.mid, key, d + 1);
        } else {
            if (!x.isTerm) {
                x.isTerm = true;
                n++; // Increment n only if this node was not already a term
            }
        }
        return x;
    }

    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        for (CharSequence term : terms){
            put(term);
        }
    }

    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        if(prefix == null){
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        List<CharSequence> results = new ArrayList<>();

        if (prefix.length() == 0) {
            // Corrected: Use length() instead of isEmpty()
            collect(overallRoot, "", results);
            return results;
        }

        Node x = get(overallRoot, prefix, 0);
        if (x == null) {
            return results;
        }

        if(x.isTerm){
            results.add(prefix.toString());
        }

        collect(x.mid, prefix.toString(), results);

        return results;
    }

    private void collect(Node x, String prefix, List<CharSequence> results){
        if (x == null) return;

        collect(x.left, prefix, results);
        String newPrefix = prefix + x.data;

        if (x.isTerm) {
            results.add(newPrefix);
        }

        collect(x.mid, newPrefix, results);
        collect(x.right, prefix, results); // Corrected: Call collect on x.right instead of x.left again
    }

    /**
     * A search tree node representing a single character in an autocompletion term.
     */
    private static class Node {
        private final char data;
        private boolean isTerm;
        private Node left;
        private Node mid;
        private Node right;

        public Node(char data) {
            this.data = data;
            this.isTerm = false;
            this.left = null;
            this.mid = null;
            this.right = null;
        }
    }
}
