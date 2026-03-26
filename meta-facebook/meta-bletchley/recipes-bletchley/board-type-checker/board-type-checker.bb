SUMMARY = "Bletchley board type checker"
DESCRIPTION = "Bletchley board type checker to probe i2c device with correct driver in user-space"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += " bash i2c-tools"

SRC_URI = " file://board-type-checker-fpb \
            file://board-type-checker-fpb.service \
          "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " board-type-checker-fpb.service"

FILES:${PN} += "${systemd_system_unitdir}/board-type-checker-fpb.service"

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/board-type-checker-fpb ${D}${libexecdir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/board-type-checker-fpb.service ${D}${systemd_system_unitdir}
}
