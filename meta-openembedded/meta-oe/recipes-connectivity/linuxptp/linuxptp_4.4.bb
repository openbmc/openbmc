SUMMARY = "linuxptp package for linux"
DESCRIPTION = "Precision Time Protocol (PTP) according to IEEE standard 1588 \
for Linux"
HOMEPAGE = "http://linuxptp.sourceforge.net/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://downloads.nwtime.org/linuxptp/linuxptp-${PV}.tgz \
           file://systemd/phc2sys@.service.in \
           file://systemd/ptp4l@.service.in \
           "

SRC_URI[sha256sum] = "61757bc0a58d789b8fcbdddf56c88a0230597184a70dcb2ac05b4c6b619f7d5c"

inherit systemd

EXTRA_OEMAKE = "CC='${CC}' EXTRA_CFLAGS='${CFLAGS}' mandir='${mandir}' \
    sbindir='${sbindir}'"

export KBUILD_OUTPUT="${RECIPE_SYSROOT}"

LINUXPTP_SYSTEMD_SERVICES = "phc2sys@.service ptp4l@.service"

do_install() {
    oe_runmake install DESTDIR=${D}

    # Install example configs from source tree
    install -d ${D}${docdir}/${PN}
    cp -R --no-dereference --preserve=mode,links ${S}/configs \
        ${D}${docdir}/${PN}

    # Install default configuration files
    install -d ${D}/${sysconfdir}/linuxptp/
    install -m 644 ${S}/configs/default.cfg \
        ${D}${sysconfdir}/linuxptp/ptp4l.conf

    # Install systemd services
    install -d ${D}/${systemd_unitdir}/system/
    for service in ${LINUXPTP_SYSTEMD_SERVICES}; do
        sed -i -e 's,@SBINDIR@,${sbindir},g' \
            ${UNPACKDIR}/systemd/$service.in
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            ${UNPACKDIR}/systemd/$service.in
        install -m 644 ${UNPACKDIR}/systemd/$service.in \
            ${D}/${systemd_unitdir}/system/$service
    done
}

SYSTEMD_SERVICE:${PN} = "${LINUXPTP_SYSTEMD_SERVICES}"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

PACKAGES =+ "${PN}-configs"

FILES:${PN}-configs += "${docdir}"
