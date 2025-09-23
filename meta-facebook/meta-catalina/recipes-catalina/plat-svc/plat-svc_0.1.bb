LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI:append = " \
    file://frontend-nic-temp-read \
    file://frontend-nic-temp-read.service \
    file://platform-early-sys-init \
    file://platform-sys-init.service \
    file://standby-power-enable \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    frontend-nic-temp-read.service \
    platform-sys-init.service \
    "

do_install() {
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}

    install -m 0755 ${UNPACKDIR}/frontend-nic-temp-read ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/platform-early-sys-init ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/standby-power-enable ${PLATSVC_LIBEXECDIR}

}

#===============================================================================
# Catalina
#===============================================================================
SRC_URI:append:catalina = " \
    file://iob-nic-temp-read \
    file://iob-nic-temp-read.service \
    file://osfp-eeprom-driver-bind \
    file://osfp-eeprom-driver-bind.service \
    "

SYSTEMD_SERVICE:${PN}:append:catalina = " \
    iob-nic-temp-read.service \
    osfp-eeprom-driver-bind.service \
    "

do_install:append:catalina() {
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/iob-nic-temp-read ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/osfp-eeprom-driver-bind ${PLATSVC_LIBEXECDIR}
}

#===============================================================================
# Clemente
#===============================================================================
SRC_URI:append:clemente = " \
    file://backend-nic-driver-bind \
    file://backend-nic-driver-bind.service \
    file://check-bootdrive-led \
    file://check-bootdrive-led.service \
    "

SYSTEMD_SERVICE:${PN}:append:clemente = " \
    backend-nic-driver-bind.service \
    check-bootdrive-led.service \
    "

do_install:append:clemente() {
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/backend-nic-driver-bind ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/check-bootdrive-led ${PLATSVC_LIBEXECDIR}
}