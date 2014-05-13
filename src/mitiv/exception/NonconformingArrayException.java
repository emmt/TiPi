/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014 the MiTiV project, http://mitiv.univ-lyon1.fr/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mitiv.exception;

public class NonconformingArrayException extends RuntimeException {
    /*
     * Define a serial version number as this class implements Serializable
     * interface.
     */
    private static final long serialVersionUID = 1L;

    private static final String defaultMessage = "Nonconforming array dimensions.";
    private static final String message1 = "Incompatible 1st dimensions.";
    private static final String message2 = "Incompatible 2nd dimensions.";
    private static final String message3 = "Incompatible 3rd dimensions.";

    public NonconformingArrayException(String message) {
        super(message);
    }

    public NonconformingArrayException() {
        super(defaultMessage);
    }

    public NonconformingArrayException(int rank) {
        super(makeMessage(rank));
    }

    private static String makeMessage(int rank) {
        if (rank == 1) {
            return message1;
        } else if (rank == 2) {
            return message2;
        } else if (rank == 3) {
            return message3;
        } else {
            return defaultMessage;
        }
    }
}

/*
 * Local Variables:
 * mode: Java
 * tab-width: 8
 * indent-tabs-mode: nil
 * c-basic-offset: 4
 * fill-column: 78
 * coding: utf-8
 * ispell-local-dictionary: "american"
 * End:
 */
