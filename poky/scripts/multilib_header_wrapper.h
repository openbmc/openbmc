/*
 * Copyright (C) 2005-2011 by Wind River Systems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

#pragma once

#if defined (__bpf__)
#define __MHWORDSIZE			64
#elif defined (__arm__)
#define __MHWORDSIZE			32
#elif defined (__aarch64__) && defined ( __LP64__)
#define __MHWORDSIZE			64
#elif defined (__aarch64__)
#define __MHWORDSIZE			32
#else
#include <bits/wordsize.h>
#if defined (__WORDSIZE)
#define __MHWORDSIZE			__WORDSIZE
#else
#error "__WORDSIZE is not defined"
#endif
#endif

#if __MHWORDSIZE == 32

#ifdef _MIPS_SIM

#if _MIPS_SIM == _ABIO32
#include <ENTER_HEADER_FILENAME_HERE-32.h>
#elif _MIPS_SIM == _ABIN32
#include <ENTER_HEADER_FILENAME_HERE-n32.h>
#else
#error "Unknown _MIPS_SIM"
#endif

#else /* _MIPS_SIM is not defined */
#include <ENTER_HEADER_FILENAME_HERE-32.h>
#endif

#elif __MHWORDSIZE == 64
#include <ENTER_HEADER_FILENAME_HERE-64.h>
#else
#error "Unknown __WORDSIZE detected"
#endif /* matches #if __WORDSIZE == 32 */
  
