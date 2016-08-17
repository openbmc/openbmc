SUMMARY = "This is the set of GNU shar utilities."
HOMEPAGE = "http://www.gnu.org/software/sharutils/"
SECTION = "console/utils"
LICENSE="GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit gettext autotools-brokensep

SRC_URI = "ftp://ftp.gnu.org/gnu/sharutils/sharutils-4.14.tar.gz \
	   file://fix-for-cross-compiling.patch \
	  "

SRC_URI[md5sum] = "5686c11131b4c4c0841f8f3ef34d136a"
SRC_URI[sha256sum] = "90f5107c167cfd1b299bb211828d2586471087863dbed698f53109cd5f717208"

do_configure () {
	oe_runconf
}
