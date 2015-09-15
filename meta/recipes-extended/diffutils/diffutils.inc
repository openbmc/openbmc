SUMMARY = "Diffutils contains tools used for finding differences between files"
DESCRIPTION = "Diffutils contains the GNU diff, diff3, \
sdiff, and cmp utilities. These programs are usually \
used for creating patch files."
SECTION = "base"

inherit autotools texinfo update-alternatives gettext

# diffutils assumes non-glibc compilation with uclibc and
# this causes it to generate its own implementations of
# standard functionality.  regex.c actually breaks compilation
# because it uses __mempcpy, there are other things (TBD:
# see diffutils.mk in buildroot)
EXTRA_OECONF_libc-uclibc = "--without-included-regex"

ALTERNATIVE_${PN} = "diff cmp"
ALTERNATIVE_PRIORITY = "100"

