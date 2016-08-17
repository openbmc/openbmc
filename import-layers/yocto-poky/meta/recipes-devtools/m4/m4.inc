SUMMARY = "Traditional Unix macro processor"
DESCRIPTION = "GNU m4 is an implementation of the traditional Unix macro processor.  It is mostly SVR4 \
compatible although it has some extensions (for example, handling more than 9 positional parameters to macros). \
GNU M4 also has built-in functions for including files, running shell commands, doing arithmetic, etc."

inherit autotools texinfo

EXTRA_OEMAKE += "'infodir=${infodir}'"
LDFLAGS_prepend_libc-uclibc = " -lrt "
SRC_URI = "${GNU_MIRROR}/m4/m4-${PV}.tar.gz"
