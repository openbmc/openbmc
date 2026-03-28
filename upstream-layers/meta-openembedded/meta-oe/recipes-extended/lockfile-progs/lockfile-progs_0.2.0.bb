SUMMARY = "Command-line programs to safely lock and unlock files and mailboxes"
DESCRIPTION = "\
lockfile-progs provide a method to lock and unlock mailboxes and files \
safely (via liblockfile)."
HOMEPAGE = "http://packages.qa.debian.org/l/lockfile-progs.html"
SECTION = "Applications/System"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"
DEPENDS = "liblockfile"

SRC_URI = "${DEBIAN_MIRROR}/main/l/${BPN}/${BPN}_${PV}.tar.xz"
SRC_URI[sha256sum] = "2988fb5a8b407e52e2aa6282ce45497f465c921d591138c100f4898215844c90"

do_compile() {
    ${BUILD_CC} ${BUILD_CPPFLAGS} ${BUILD_CFLAGS} -c rewrite-time.c
    ${BUILD_CC} -o rewrite-time ${BUILD_LDFLAGS} rewrite-time.o
    oe_runmake CFLAGS=' -g -pipe -Wall -Wp,-D_FORTIFY_SOURCE=2 -fexceptions -fstack-protector --param=ssp-buffer-size=4 -fasynchronous-unwind-tables ${DEBUG_PREFIX_MAP}'
}

do_install() {
    install -m 755 -d ${D}${bindir}
    install bin/* ${D}${bindir}
    install -m 755 -d ${D}${mandir}/man1
    install man/* ${D}${mandir}/man1
}
