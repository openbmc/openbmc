SUMMARY = "Hotswap Controller Setting Changing"
DESCRIPTION = "Hotswap Controller Setting Changing Daemon"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "bash"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_append =  " file://gsj-hotswap-change-setting.sh \
                    file://gsj-hotswap-change-setting.service \
                  "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/gsj-hotswap-change-setting.sh ${D}${bindir}/

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/gsj-hotswap-change-setting.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "gsj-hotswap-change-setting.service"
