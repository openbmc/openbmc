SUMMARY = "find, locate, and xargs binaries"
DESCRIPTION = "The GNU Find Utilities are the basic directory searching utilities of the GNU operating system. \
These programs are typically used in conjunction with other programs to provide modular and powerful directory \
search and file locating capabilities to other commands."
HOMEPAGE = "http://www.gnu.org/software/findutils/"
BUGTRACKER = "http://savannah.gnu.org/bugs/?group=findutils"
SECTION = "console/utils"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.xz \
          file://run-ptest \
          "

inherit autotools gettext texinfo update-alternatives ptest

ALTERNATIVE:${PN} = "find xargs"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native nativesdk"
