SUMMARY = "GNU multiprecision arithmetic library"
DESCRIPTION = "GMP is a free library for arbitrary precision arithmetic, operating on signed integers, rational numbers, and floating point numbers"
HOMEPAGE = "http://gmplib.org/"
SECTION = "devel"

inherit autotools texinfo multilib_header

PACKAGECONFIG ??= ""
PACKAGECONFIG[readline] = "--with-readline=yes,--with-readline=no,readline"

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
