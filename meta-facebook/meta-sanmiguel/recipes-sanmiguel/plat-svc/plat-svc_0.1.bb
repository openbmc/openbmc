LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit systemd
inherit obmc-phosphor-utils

S = "${UNPACKDIR}/sources"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

# udev rules
SRC_URI:append = " \
    file://99-cp2112-bind.rules \
    file://99-cp2112-cx9-gpio.rules \
    "

# scripts
SRC_URI:append = " \
    file://platform-early-sys-init \
    file://standby-power-enable \
    file://nic-sensor-read \
    "

# services
SRC_URI:append = " \
    file://platform-sys-init.service \
    file://nic-sensor-read.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    platform-sys-init.service \
    nic-sensor-read.service \
    "

FILES:${PN} += " \
    ${systemd_system_unitdir}/platform-sys-init.service \
    ${systemd_system_unitdir}/nic-sensor-read.service \
    "

do_install() {
    # install scripts
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/platform-early-sys-init ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/standby-power-enable ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/nic-sensor-read ${PLATSVC_LIBEXECDIR}

    # install udev rules
    UDEV_RULES_DIR="${D}${sysconfdir}/udev/rules.d"
    install -d ${UDEV_RULES_DIR}
    install -m 0644 ${UNPACKDIR}/99-cp2112-bind.rules ${UDEV_RULES_DIR}/99-cp2112-bind.rules
    install -m 0644 ${UNPACKDIR}/99-cp2112-cx9-gpio.rules ${UDEV_RULES_DIR}/99-cp2112-cx9-gpio.rules

    # install services
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/platform-sys-init.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/nic-sensor-read.service ${D}${systemd_system_unitdir}
}

