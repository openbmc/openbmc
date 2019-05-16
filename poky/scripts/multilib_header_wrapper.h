/*
 * Copyright (C) 2005-2011 by Wind River Systems, Inc.
 *
 * SPDX-License-Identifier: MIT
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
  
