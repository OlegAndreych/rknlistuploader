package ru.andreych.rkn.list.uploader.rkn.list.uploader;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.Test;

public class SampleTest {

    @Test
    public void test() {
        final IPAddress ipAddress = new IPAddressString("172.20.88.1-3").getAddress();

        System.out.println("Is multiple: " + ipAddress.isMultiple());
        System.out.println("Lower: " + ipAddress.getLower() + ", upper: " + ipAddress.getUpper());
    }
}
