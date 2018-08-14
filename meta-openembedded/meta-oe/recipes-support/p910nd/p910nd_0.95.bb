DESCRIPTION = "A small network printer daemon for embedded situations that passes the job directly to the printer"
HOMEPAGE = "http://p910nd.sourceforge.net/"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

PR = "r2"

SRC_URI = "${SOURCEFORGE_MIRROR}/p910nd/p910nd-${PV}.tar.bz2 \
           file://fix-var-lock.patch"

SRC_URI[md5sum] = "c7ac6afdf7730ac8387a8e87198d4491"
SRC_URI[sha256sum] = "7d78642c86dc247fbdef1ff85c56629dcdc6b2a457c786420299e284fffcb029"

do_compile () {
    ${CC} ${LDFLAGS} -o p910nd p910nd.c
}

do_install () {
    install -D -m 0755 ${S}/p910nd ${D}${sbindir}/p910nd
    install -D -m 0644 ${S}/p910nd.conf ${D}${sysconfdir}/p910nd.conf
}
