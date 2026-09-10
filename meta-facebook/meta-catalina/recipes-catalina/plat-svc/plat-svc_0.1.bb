LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI:append = " \
    file://platform-early-sys-init \
    file://platform-sys-init.service \
    file://standby-power-enable \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    platform-sys-init.service \
    "

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install() {
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}

    install -m 0755 ${UNPACKDIR}/platform-early-sys-init ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/standby-power-enable ${PLATSVC_LIBEXECDIR}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/platform-sys-init.service ${D}${systemd_system_unitdir}
}

#===============================================================================
# Catalina
#===============================================================================
SRC_URI:append:catalina = " \
    file://frontend-nic-temp-read \
    file://frontend-nic-temp-read.service \
    file://iob-nic-temp-read \
    file://iob-nic-temp-read.service \
    file://osfp-eeprom-driver-bind \
    file://osfp-eeprom-driver-bind.service \
    "

SYSTEMD_SERVICE:${PN}:append:catalina = " \
    frontend-nic-temp-read.service \
    iob-nic-temp-read.service \
    osfp-eeprom-driver-bind.service \
    "

do_install:append:catalina() {
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/frontend-nic-temp-read ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/iob-nic-temp-read ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/osfp-eeprom-driver-bind ${PLATSVC_LIBEXECDIR}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/frontend-nic-temp-read.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/iob-nic-temp-read.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/osfp-eeprom-driver-bind.service ${D}${systemd_system_unitdir}
}

#===============================================================================
# Clemente
#===============================================================================
SRC_URI:append:clemente = " \
    file://check-bootdrive-led \
    file://check-bootdrive-led.service \
    file://frontend-nic-init \
    file://frontend-nic-init.service \
    file://init-sma \
    file://init-sma.service \
    file://cx-ready.target \
    "

SYSTEMD_SERVICE:${PN}:append:clemente = " \
    check-bootdrive-led.service \
    frontend-nic-init.service \
    init-sma.service \
    cx-ready.target \
    "

do_install:append:clemente() {
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/check-bootdrive-led ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/frontend-nic-init ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/init-sma ${PLATSVC_LIBEXECDIR}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/check-bootdrive-led.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/frontend-nic-init.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/init-sma.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/cx-ready.target ${D}${systemd_system_unitdir}
}
