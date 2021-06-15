DESCRIPTION = "A small network printer daemon for embedded situations that passes the job directly to the printer"
HOMEPAGE = "http://p910nd.sourceforge.net/"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

PR = "r2"

SRC_URI = "${SOURCEFORGE_MIRROR}/p910nd/p910nd-${PV}.tar.bz2 \
           file://fix-var-lock.patch"

SRC_URI[md5sum] = "69461a6c54dca0b13ecad5b83864b43e"
SRC_URI[sha256sum] = "4ac980a3ae24babae6f70f0a692625ece03a4a92c357fbb10d2e368386c3c26f"

do_compile () {
    ${CC} ${LDFLAGS} -o p910nd p910nd.c
}

do_install () {
    install -D -m 0755 ${S}/p910nd ${D}${sbindir}/p910nd
    install -D -m 0644 ${S}/p910nd.conf ${D}${sysconfdir}/p910nd.conf
}
