package info.jtrac.mylar.domain;

import info.jtrac.mylar.exception.InvalidRefIdException;

public class ItemRefId {

    private long sequenceNum;
    private String prefixCode;
    
    public ItemRefId(String refId) throws InvalidRefIdException {
        int pos = refId.indexOf('-');
        if (pos == -1) {
            throw new InvalidRefIdException("invalid ref id");
        }
        try {
            sequenceNum = Long.parseLong(refId.substring(pos + 1));
        } catch (NumberFormatException e) {
            throw new InvalidRefIdException("invalid ref id");
        }
        prefixCode = refId.substring(0, pos).toUpperCase();
    }

    public String getPrefixCode() {
        return prefixCode;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }	
	
}
