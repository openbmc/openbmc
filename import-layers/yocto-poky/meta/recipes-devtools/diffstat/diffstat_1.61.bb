SUMMARY = "Tool to produce a statistics based on a diff"
DESCRIPTION = "diffstat reads the output of diff and displays a histogram of \
the insertions, deletions, and modifications per-file. It is useful for \
reviewing large, complex patch files."
HOMEPAGE = "http://invisible-island.net/diffstat/"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://install-sh;endline=42;md5=b3549726c1022bee09c174c72a0ca4a5"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://run-ptest \
"

SRC_URI[md5sum] = "c048a32d55d8bd6724f382baf41f325f"
SRC_URI[sha256sum] = "25359e0c27183f997b36c9202583b5dc2df390c20e22a92606af4bf7856a55ee"

S = "${WORKDIR}/diffstat-${PV}"

inherit autotools gettext ptest

EXTRA_AUTORECONF += "--exclude=aclocal"

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_install_ptest() {
	cp -r ${S}/testing ${D}${PTEST_PATH}
}
