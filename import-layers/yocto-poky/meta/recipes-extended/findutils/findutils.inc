SUMMARY = "find, locate, and xargs binaries"
DESCRIPTION = "The GNU Find Utilities are the basic directory searching utilities of the GNU operating system. \
These programs are typically used in conjunction with other programs to provide modular and powerful directory \
search and file locating capabilities to other commands."
HOMEPAGE = "http://www.gnu.org/software/findutils/"
BUGTRACKER = "http://savannah.gnu.org/bugs/?group=findutils"
SECTION = "console/utils"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz"

inherit autotools gettext texinfo update-alternatives upstream-version-is-even

ALTERNATIVE_${PN} = "find xargs"
ALTERNATIVE_PRIORITY = "100"

# diffutils assumes non-glibc compilation with uclibc and
# this causes it to generate its own implementations of
# standard functionality.  regex.c actually breaks compilation
# because it uses __mempcpy, there are other things (TBD:
# see diffutils.mk in buildroot)
EXTRA_OECONF_libc-uclibc = "--without-included-regex"

BBCLASSEXTEND = "native nativesdk"
