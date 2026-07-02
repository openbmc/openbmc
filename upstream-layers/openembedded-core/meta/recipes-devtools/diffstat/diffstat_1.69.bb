SUMMARY = "Tool to produce a statistics based on a diff"
DESCRIPTION = "diffstat reads the output of diff and displays a histogram of \
the insertions, deletions, and modifications per-file. It is useful for \
reviewing large, complex patch files."
HOMEPAGE = "http://invisible-island.net/diffstat/"
SECTION = "devel"
LICENSE = "X11"
LIC_FILES_CHKSUM = "file://COPYING;md5=53d95e97e08db153daa45bfe4e4be37e"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://run-ptest \
           file://standard-autoconf.patch \
           file://avoid-check-user-break-cc.patch \
           "

SRC_URI[sha256sum] = "bb02464072f769dd9832fd999526734c90eb4d66fb56d5351540a750c88a77f6"

inherit autotools gettext ptest

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_install_ptest() {
	cp -r ${S}/testing ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "nativesdk"
