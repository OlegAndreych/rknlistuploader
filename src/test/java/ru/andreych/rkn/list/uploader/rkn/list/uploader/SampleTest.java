package ru.andreych.rkn.list.uploader.rkn.list.uploader;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.Test;

public class SampleTest {

    @Test
    public void test() {
        final IPAddress ipAddress = new IPAddressString("172.20.88.1-30").getAddress();
        final IPAddress s = ipAddress.toPrefixBlock();

        final IPAddress prefixBlock = s.mergeToPrefixBlocks(s)[0];
        System.out.println(prefixBlock.isPrefixBlock());
        System.out.println("Is multiple: " + prefixBlock.isMultiple());
        System.out.println("Lower: " + prefixBlock.getLowerNonZeroHost().toCanonicalWildcardString() + ", upper: " + prefixBlock.getUpper());
    }
}
