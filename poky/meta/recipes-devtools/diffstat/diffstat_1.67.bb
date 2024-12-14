SUMMARY = "Tool to produce a statistics based on a diff"
DESCRIPTION = "diffstat reads the output of diff and displays a histogram of \
the insertions, deletions, and modifications per-file. It is useful for \
reviewing large, complex patch files."
HOMEPAGE = "http://invisible-island.net/diffstat/"
SECTION = "devel"
LICENSE = "X11"
LIC_FILES_CHKSUM = "file://COPYING;md5=6232ea974e4cbc4ee06b49ed53df6ece"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://run-ptest \
           file://avoid-check-user-break-cc.patch \
           "

SRC_URI[sha256sum] = "760ed0c99c6d643238d41b80e60278cf1683ffb94a283954ac7ef168c852766a"

inherit autotools gettext ptest

EXTRA_AUTORECONF += "--exclude=aclocal"

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_install_ptest() {
	cp -r ${S}/testing ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "nativesdk"
