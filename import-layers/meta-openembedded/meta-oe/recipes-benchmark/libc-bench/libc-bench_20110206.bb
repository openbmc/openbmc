SUMMARY = "Tests to compare standard functions of different libc implementations"
DESCRIPTION = "libc-bench is a set of time- and memory-efficiency tests to compare \
implementations of various C/POSIX standard library functions."
HOMEPAGE = "http://www.etalabs.net/libc-bench.html"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://Makefile;md5=e12f113da27dfe9cfb6c2c537da8d8df"

SRC_URI = "http://www.etalabs.net/releases/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "f763de90f95fe68e4e03e5b6f49698ac"
SRC_URI[sha256sum] = "6825260aa5f15f4fbc7957ec578e9c859cbbe210e025ec74c4a0d05677523794"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/libc-bench ${D}${bindir}
}
