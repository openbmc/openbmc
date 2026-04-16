SUMMARY = "Advanced TFTP server and client"
SECTION = "net"
HOMEPAGE = "http://packages.debian.org/atftp"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=570a9b3749dd0463a1778803b12a6dce"

SRCREV = "7238b7b6753157d0c4ad739df3d87a958f78d70b"

SRC_URI = "git://git.code.sf.net/p/atftp/code;branch=master;protocol=https \
           file://atftpd.init \
           file://atftpd.service \
"


inherit autotools update-rc.d systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[pcre] = "--enable-libpcre,--disable-libpcre,libpcre"
PACKAGECONFIG[readline] = "--enable-libreadline,--disable-libreadline,readline"

INITSCRIPT_PACKAGES = "${PN}d"
INITSCRIPT_NAME:${PN}d = "atftpd"
INITSCRIPT_PARAMS:${PN}d = "defaults 80"


EXTRA_OEMAKE = "CFLAGS='${CFLAGS} -std=gnu89'"

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/atftpd.init ${D}${sysconfdir}/init.d/atftpd

    install -d ${D}/srv/tftp

    rm ${D}${sbindir}/in.tftpd

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/atftpd.service ${D}${systemd_unitdir}/system
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
