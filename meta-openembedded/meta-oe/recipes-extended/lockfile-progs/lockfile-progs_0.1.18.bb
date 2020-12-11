SUMMARY = "Command-line programs to safely lock and unlock files and mailboxes"
DESCRIPTION = "\
lockfile-progs provide a method to lock and unlock mailboxes and files \
safely (via liblockfile)."
HOMEPAGE = "http://packages.qa.debian.org/l/lockfile-progs.html"
SECTION = "Applications/System"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"
DEPENDS = "liblockfile"

SRC_URI = "${DEBIAN_MIRROR}/main/l/${BPN}/${BPN}_${PV}.tar.gz"
SRC_URI[md5sum] = "4eb83bdf88016db836b7cc09591fb0f3"
SRC_URI[sha256sum] = "a42995a4b97e6188efc90fcc1a761163c4b2cff5c81b936f85c84301ddb05ce6"

do_compile() {
    oe_runmake CFLAGS=' -g -pipe -Wall -Wp,-D_FORTIFY_SOURCE=2 -fexceptions -fstack-protector --param=ssp-buffer-size=4 -fasynchronous-unwind-tables'
}

do_install() {
    install -m 755 -d ${D}${bindir}
    install bin/* ${D}${bindir}
    install -m 755 -d ${D}${mandir}/man1
    install man/* ${D}${mandir}/man1
}
