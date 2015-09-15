SECTION = "devel"
SUMMARY = "GNU multiprecision arithmetic library"
DESCRIPTION = "GMP is a free library for arbitrary precision arithmetic, operating on signed integers, rational numbers, and floating point numbers"
HOMEPAGE = "http://gmplib.org/"

REVISION ?= ""
SRC_URI = "https://gmplib.org/download/${BPN}/${BP}${REVISION}.tar.bz2"

inherit autotools texinfo

ARM_INSTRUCTION_SET = "arm"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG ??= ""
PACKAGECONFIG[readline] = "--with-readline=yes,--with-readline=no,readline"
