SUMMARY = "Tool to produce a statistics based on a diff"
DESCRIPTION = "diffstat reads the output of diff and displays a histogram of \
the insertions, deletions, and modifications per-file. It is useful for \
reviewing large, complex patch files."
HOMEPAGE = "http://invisible-island.net/diffstat/"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a3d0bb117493e804b0c1a868ddf23321"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://run-ptest \
           file://avoid-check-user-break-cc.patch \
           file://0001-aclocal.m4-add-missing-header-defines.patch \
           "

SRC_URI[md5sum] = "b9272ec8af6257103261ec3622692991"
SRC_URI[sha256sum] = "7eddd53401b99b90bac3f7ebf23dd583d7d99c6106e67a4f1161b7a20110dc6f"

inherit autotools gettext ptest

EXTRA_AUTORECONF += "--exclude=aclocal"

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_install_ptest() {
	cp -r ${S}/testing ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "nativesdk"
