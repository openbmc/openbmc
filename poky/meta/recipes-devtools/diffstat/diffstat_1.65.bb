SUMMARY = "Tool to produce a statistics based on a diff"
DESCRIPTION = "diffstat reads the output of diff and displays a histogram of \
the insertions, deletions, and modifications per-file. It is useful for \
reviewing large, complex patch files."
HOMEPAGE = "http://invisible-island.net/diffstat/"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6450594049f0eed1052e634ddcbeec28"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://run-ptest \
           file://avoid-check-user-break-cc.patch \
           "

SRC_URI[sha256sum] = "8cf27424926debcd85921755c3915559288244bd103f62d740dc6b83b557a28a"

inherit autotools gettext ptest

EXTRA_AUTORECONF += "--exclude=aclocal"

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_install_ptest() {
	cp -r ${S}/testing ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "nativesdk"
