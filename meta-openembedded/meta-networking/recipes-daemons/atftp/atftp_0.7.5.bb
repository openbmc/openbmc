SUMMARY = "Advanced TFTP server and client"
SECTION = "net"
HOMEPAGE = "http://packages.debian.org/atftp"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f65159f73e603f89d6867d43191900e5"

SRCREV = "00921e75728e3681b051c2e48c59e36c6cfa2e97"

SRC_URI = "git://git.code.sf.net/p/atftp/code;branch=master \
           file://atftpd.init \
           file://atftpd.service \
"

S = "${WORKDIR}/git"

inherit autotools update-rc.d systemd

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[pcre] = "--enable-libpcre,--disable-libpcre,libpcre"
PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"
PACKAGECONFIG[readline] = "--enable-libreadline,--disable-libreadline,readline"

INITSCRIPT_PACKAGES = "${PN}d"
INITSCRIPT_NAME:${PN}d = "atftpd"
INITSCRIPT_PARAMS:${PN}d = "defaults 80"


EXTRA_OEMAKE = "CFLAGS='${CFLAGS} -std=gnu89'"

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/atftpd.init ${D}${sysconfdir}/init.d/atftpd

    install -d ${D}/srv/tftp

    rm ${D}${sbindir}/in.tftpd

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/atftpd.service ${D}${systemd_unitdir}/system
}

PACKAGES =+ "${PN}d"

FILES:${PN} = "${bindir}/*"

FILES:${PN}d = "${sbindir}/* \
    ${sysconfdir} \
    /srv/tftp \
    ${systemd_unitdir}/system/atftpd.service \
"

SYSTEMD_PACKAGES = "${PN}d"
SYSTEMD_SERVICE:${PN}d = "atftpd.service"
RPROVIDES:${PN}d += "${PN}d-systemd"
RREPLACES:${PN}d += "${PN}d-systemd"
RCONFLICTS:${PN}d += "${PN}d-systemd"
