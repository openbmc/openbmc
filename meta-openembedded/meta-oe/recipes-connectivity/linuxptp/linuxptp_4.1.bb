SUMMARY = "linuxptp package for linux"
DESCRIPTION = "Precision Time Protocol (PTP) according to IEEE standard 1588 \
for Linux"
HOMEPAGE = "http://linuxptp.sourceforge.net/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

LINUXPTP_SRC_URI = "http://sourceforge.net/projects/linuxptp"

SRC_URI = "${LINUXPTP_SRC_URI}/files/v4.1/linuxptp-${PV}.tgz \
           file://0001-include-string.h-for-strncpy.patch \
           file://0002-linuxptp-Use-CC-in-incdefs.sh.patch \
           file://systemd/phc2sys@.service.in \
           file://systemd/ptp4l@.service.in \
           "

SRC_URI[md5sum] = "1db8699fc155965341759be5e5aad938"
SRC_URI[sha256sum] = "e1743d44f8208897e30895da3579e670ff919b914feb4b5a949f3e421ddde535"

inherit systemd

UPSTREAM_CHECK_URI = "${LINUXPTP_SRC_URI}/files/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"

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
            ${WORKDIR}/systemd/$service.in
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            ${WORKDIR}/systemd/$service.in
        install -m 644 ${WORKDIR}/systemd/$service.in \
            ${D}/${systemd_unitdir}/system/$service
    done
}

SYSTEMD_SERVICE:${PN} = "${LINUXPTP_SYSTEMD_SERVICES}"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

PACKAGES =+ "${PN}-configs"

FILES:${PN}-configs += "${docdir}"
