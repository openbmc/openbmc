SUMMARY = "Connman config to setup wired interface on qemu machines"
DESCRIPTION = "This is the ConnMan configuration to set up a Wired \
network interface for a qemu machine."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit systemd

SRC_URI_append_qemuall = " file://wired.config \
                           file://wired-setup \
                           file://wired-connection.service \
"
PR = "r2"

S = "${WORKDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} = "${localstatedir}/* ${datadir}/*"

do_install() {
    #Configure Wired network interface in case of qemu* machines
    if test -e ${WORKDIR}/wired.config &&
       test -e ${WORKDIR}/wired-setup &&
       test -e ${WORKDIR}/wired-connection.service; then
        install -d ${D}${localstatedir}/lib/connman
        install -m 0644 ${WORKDIR}/wired.config ${D}${localstatedir}/lib/connman
        install -d ${D}${datadir}/connman
        install -m 0755 ${WORKDIR}/wired-setup ${D}${datadir}/connman
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/wired-connection.service ${D}${systemd_system_unitdir}
        sed -i -e 's|@SCRIPTDIR@|${datadir}/connman|g' ${D}${systemd_system_unitdir}/wired-connection.service
    fi
}

SYSTEMD_SERVICE_${PN}_qemuall = "wired-connection.service"
