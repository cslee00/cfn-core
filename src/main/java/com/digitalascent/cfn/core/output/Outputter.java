package com.digitalascent.cfn.core.output;

import java.io.OutputStream;

public interface Outputter {
    void output(Object obj, OutputStream os, boolean prettyPrint);
}
