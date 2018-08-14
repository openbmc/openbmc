SUMMARY = "RULI stands for Resolver User Layer Interface It's a library	built on top of an asynchronous DNS stub resolver"

HOMEPAGE = "http://www.nongnu.org/ruli/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS = "liboop"

SRC_URI = "http://download.savannah.gnu.org/releases/ruli/ruli_${PV}.orig.tar.gz \
           file://Makefile.patch \
           file://0001-Fix-build-with-format-string-checks.patch \
           file://0001-src-ruli_addr.c-Add-missing-format-string.patch \
           "

SRC_URI[md5sum] = "e73fbfdeadddb68a703a70cea5271468"
SRC_URI[sha256sum] = "11d32def5b514748fbd9ea8c88049ae99e1bb358efc74eb91a4d268a3999dbfa"

do_install1() {
    install -d ${D}${includedir}/ruli
    install -d ${D}${libdir}
    install -m 0644 ${S}/src/ruli*.h ${D}${includedir}/ruli
    install -m 0644 ${S}/src/libruli.so ${D}${libdir}
    install -m 0644 ${S}/src/libruli.so.4 ${D}${libdir}
}
