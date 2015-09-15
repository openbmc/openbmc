SUMMARY = "Tool to produce a statistics based on a diff"
DESCRIPTION = "diffstat reads the output of diff and displays a histogram of \
the insertions, deletions, and modifications per-file. It is useful for \
reviewing large, complex patch files."
HOMEPAGE = "http://invisible-island.net/diffstat/"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://install-sh;endline=42;md5=b3549726c1022bee09c174c72a0ca4a5"

SRC_URI = "ftp://invisible-island.net/diffstat/diffstat-${PV}.tgz \
           file://run-ptest \
"

SRC_URI[md5sum] = "ba889da4c06b547aa2d78fa96800ae6f"
SRC_URI[sha256sum] = "2032e418b43bae70d548e32da901ebc4ac12972381de1314bebde0b126fb0123"

S = "${WORKDIR}/diffstat-${PV}"

inherit autotools gettext ptest

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_configure () {
	if [ ! -e ${S}/acinclude.m4 ]; then
		mv ${S}/aclocal.m4 ${S}/acinclude.m4
	fi
	autotools_do_configure
}

do_install_ptest() {
	cp -r ${S}/testing ${D}${PTEST_PATH}
}
