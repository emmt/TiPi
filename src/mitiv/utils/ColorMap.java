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

package mitiv.utils;

/*
 * Copyright 1999-2004 Carnegie Mellon University.  
 * Portions Copyright 2002-2004 Sun Microsystems, Inc.  
 * Portions Copyright 2002-2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "README" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

import java.util.Arrays;
import java.awt.Color;

/**
 * Color map representation - each entry in the map has an RGB value
 * associated with it
 *
 * @author Ron Weiss (ronw@ee.columbia.edu)
 *
 */
public class ColorMap
{
    int size;
    /**
     * Red component
     */
    public byte r[];
    /**
     * Green component
     */
    public byte g[];
    /**
     * Blue component
     */
    public byte b[];
    public Color table[];

    /**
     * Create a color map that looks like Matlab's jet color map
     * @return A colormap with 64 value
     */
    public static ColorMap getJet()
    {
        return getJet(64);
    }

    /**
     * Create a color map witn n entries that looks like Matlab's jet
     * color map
     * @param n 
     * @return 
     */
    public static ColorMap getJet(int n)
    {
        byte r[] = new byte[n];
        byte g[] = new byte[n];
        byte b[] = new byte[n];
        
        int maxval = 255;
        Arrays.fill(g, 0, n/8, (byte)0);
        for(int x = 0; x < n/4; x++)
            g[x+n/8] = (byte)(maxval*x*4/n);
        Arrays.fill(g, n*3/8, n*5/8, (byte)maxval);
        for(int x = 0; x < n/4; x++)
            g[x+n*5/8] = (byte)(maxval-(maxval*x*4/n));
        Arrays.fill(g, n*7/8, n, (byte)0);

        for(int x = 0; x < g.length; x++)
            b[x] = g[(x+n/4) % g.length];
        Arrays.fill(b, n*7/8, n, (byte)0);
        Arrays.fill(g, 0, n/8, (byte)0);;
        for(int x = n/8; x < g.length; x++)
            r[x] = g[(x+n*6/8) % g.length];
        
        ColorMap cm = new ColorMap();
        cm.size = n;
        cm.r = r;
        cm.g = g;
        cm.b = b;
        cm.table = new Color[n];
        for(int x = 0; x < n; x++)
            cm.table[x] = new Color(cm.getColor(x));
        return cm;
    }


    /**
     * Get the RGB value associated with an entry in this ColorMap
     * @param idx 
     * @return 
     */
    public int getColor(int idx)
    {
        int pixel = ((r[idx] << 16) & 0xff0000)
            | ((g[idx] << 8) & 0xff00)
            | (b[idx] & 0xff);

        return pixel;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(500);
        for(int x = 0; x < size; x++)
        {
            s.append(x+": {"+r[x]+",\t"+g[x]+",\t"+b[x]+"}\t");
            if(x%3 == 2)
                s.append("\n");
        }

        return s.toString();
    }

    /**
     * 
     * test purposes
     * @param args
     */
    public static void main(String[] args)
    {
        ColorMap jet = getJet();
        ColorMap jet128 = getJet(128);
        int c = jet128.r[0];
        System.out.println(c);

        
        System.out.println("Jet:\n"+jet+"\n\nJet128:\n"+jet128);
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
