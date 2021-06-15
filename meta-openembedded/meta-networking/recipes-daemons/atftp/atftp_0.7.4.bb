SUMMARY = "Advanced TFTP server and client"
SECTION = "net"
HOMEPAGE = "http://packages.debian.org/atftp"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94d55d512a9ba36caa9b7df079bae19f"

SRCREV = "e56e8845f1070e89a4a6e509396b681688d03793"

SRC_URI = "git://git.code.sf.net/p/atftp/code \
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
INITSCRIPT_NAME_${PN}d = "atftpd"
INITSCRIPT_PARAMS_${PN}d = "defaults 80"


EXTRA_OEMAKE = "CFLAGS='${CFLAGS} -std=gnu89'"

do_install_append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/atftpd.init ${D}${sysconfdir}/init.d/atftpd

    install -d ${D}/srv/tftp

    rm ${D}${sbindir}/in.tftpd

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/atftpd.service ${D}${systemd_unitdir}/system
}

PACKAGES =+ "${PN}d"

FILES_${PN} = "${bindir}/*"

FILES_${PN}d = "${sbindir}/* \
    ${sysconfdir} \
    /srv/tftp \
    ${systemd_unitdir}/system/atftpd.service \
"

SYSTEMD_PACKAGES = "${PN}d"
SYSTEMD_SERVICE_${PN}d = "atftpd.service"
RPROVIDES_${PN}d += "${PN}d-systemd"
RREPLACES_${PN}d += "${PN}d-systemd"
RCONFLICTS_${PN}d += "${PN}d-systemd"
