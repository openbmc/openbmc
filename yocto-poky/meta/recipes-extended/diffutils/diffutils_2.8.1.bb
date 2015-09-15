LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

require diffutils.inc

PR = "r7.0"

SRC_URI = "${GNU_MIRROR}/diffutils/diffutils-${PV}.tar.gz \
           file://diffutils_fix_for_automake-1.12.patch"

SRC_URI[md5sum] = "71f9c5ae19b60608f6c7f162da86a428"
SRC_URI[sha256sum] = "c5001748b069224dd98bf1bb9ee877321c7de8b332c8aad5af3e2a7372d23f5a"

do_configure_prepend () {
	chmod u+w ${S}/po/Makefile.in.in
}
