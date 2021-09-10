SUMMARY = "Tool to produce a statistics based on a diff"
DESCRIPTION = "diffstat reads the output of diff and displays a histogram of \
the insertions, deletions, and modifications per-file. It is useful for \
reviewing large, complex patch files."
HOMEPAGE = "http://invisible-island.net/diffstat/"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f605b1986cc3b808ec0e4fa9d0e0f2d9"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://run-ptest \
           file://avoid-check-user-break-cc.patch \
           file://0001-aclocal.m4-add-missing-header-defines.patch \
           "

SRC_URI[sha256sum] = "b8aee38d9d2e1d05926e6b55810a9d2c2dd407f24d6a267387563a4436e3f7fc"

inherit autotools gettext ptest

EXTRA_AUTORECONF += "--exclude=aclocal"

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_install_ptest() {
	cp -r ${S}/testing ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "nativesdk"
