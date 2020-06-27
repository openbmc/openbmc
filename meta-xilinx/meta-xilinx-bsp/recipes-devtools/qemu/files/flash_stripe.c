/*
 * Stripe a flash image across multiple files.
 *
 * Copyright (C) 2019 Xilinx, Inc.  All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>

/* N way (num) in place bit striper. Lay out row wise bits column wise
 * (from element 0 to N-1). num is the length of x, and dir reverses the
 * direction of the transform. be determines the bit endianess scheme.
 * false to lay out bits LSB to MSB (little endian) and true for big endian.
 *
 * Best illustrated by examples:
 * Each digit in the below array is a single bit (num == 3, be == false):
 *
 * {{ 76543210, }  ----- stripe (dir == false) -----> {{ FCheb630, }
 *  { hgfedcba, }                                      { GDAfc741, }
 *  { HGFEDCBA, }} <---- upstripe (dir == true) -----  { HEBgda52, }}
 *
 * Same but with be == true:
 *
 * {{ 76543210, }  ----- stripe (dir == false) -----> {{ 741gdaFC, }
 *  { hgfedcba, }                                      { 630fcHEB, }
 *  { HGFEDCBA, }} <---- upstripe (dir == true) -----  { 52hebGDA, }}
 */

static inline void stripe8(uint8_t *x, int num, bool dir, bool be)
{
    uint8_t r[num];
    memset(r, 0, sizeof(uint8_t) * num);
    int idx[2] = {0, 0};
    int bit[2] = {0, be ? 7 : 0};
    int d = dir;

    for (idx[0] = 0; idx[0] < num; ++idx[0]) {
        for (bit[0] = be ? 7 : 0; bit[0] != (be ? -1 : 8); bit[0] += be ? -1 : 1) {
            r[idx[!d]] |= x[idx[d]] & 1 << bit[d] ? 1 << bit[!d] : 0;
            idx[1] = (idx[1] + 1) % num;
            if (!idx[1]) {
                bit[1] += be ? -1 : 1;
            }
        }
    }
    memcpy(x, r, sizeof(uint8_t) * num);
}

int main (int argc, char *argv []) {
#ifdef UNSTRIPE
    bool unstripe = true;
#else
    bool unstripe = false;
#endif

#ifdef FLASH_STRIPE_BE
    bool be = true;
#else
    bool be = false;
#endif

    int i;

    const char *exe_name = argv[0];
    argc--;
    argv++;

    if (argc < 2) {
        fprintf(stderr, "ERROR: %s requires at least two args\n", exe_name);
        return 1;
    }

    const char *single_f = argv[0];
    int single;
    
    if (unstripe) {
        single = creat(single_f, 0644);
    } else {
        single = open(single_f, 0);
    }
    if (single == -1) {
        perror(argv[0]);
        return 1;
    }

    argv++;
    argc--;
    
    int multiple[argc];
    
    for (i = 0; i < argc; ++i) {
        if (unstripe) {
            multiple[i] = open(argv[i], 0);
        } else {
            multiple[i] = creat(argv[i], 0644);
        }
        if (multiple[i] == -1) {
            perror(argv[i]);
            return 1;
        }
    }

    while (true) {
        uint8_t buf[argc];
        for (i = 0; i < argc; ++i) {
            switch (read(!unstripe ? single : multiple[
#if defined(FLASH_STRIPE_BW) && defined (FLASH_STRIPE_BE)
                                                       argc - 1 -
#endif
                                                       i], &buf[i], 1)) {
            case 0:
                if (i == 0) {
                    goto done;
                } else if (!unstripe) {
                    fprintf(stderr, "WARNING:input file %s is not multiple of "
                            "%d bytes, padding with garbage byte\n", single_f,
                            argc);
                }
                break;
            case -1:
                perror(unstripe ? argv[i] : single_f);
                return 1;
            }
        }

#ifndef FLASH_STRIPE_BW
        stripe8(buf, argc, unstripe, be);
#endif

        for (i = 0; i < argc; ++i) {
            switch (write(unstripe ? single : multiple[
#if defined(FLASH_STRIPE_BW) && defined (FLASH_STRIPE_BE)
                                                       argc - 1 -
#endif
                                                       i], &buf[i], 1)) {
            case -1:
                perror(unstripe ? single_f : argv[i]);
                return 1;
            case 0:
                i--; /* try again */
            }
        }
    }

done:
    close(single);
    for (i = 0; i < argc; ++i) {
        close(multiple[argc]);
    }
    return 0;
}
