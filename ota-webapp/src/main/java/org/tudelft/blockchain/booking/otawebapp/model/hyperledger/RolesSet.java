package org.tudelft.blockchain.booking.otawebapp.model.hyperledger;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class RolesSet extends AbstractSet<String> {

    private Collection<String> collection;

    public RolesSet() {
        collection = new ArrayList<>();
    }

    @Override
    public boolean add(String s) {
        if (!collection.contains(s))
            return collection.add(s);
        return false;
    }

    @Override
    public Iterator<String> iterator() {
        return collection.iterator();
    }

    @Override
    public int size() {
        return collection.size();
    }
}
