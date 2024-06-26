SUMMARY = "Apply default configuration from baseboard FRU"
DESCRIPTION = "Provide baseboard FRU EEPROM handlers to apply platform configuration on system boot"

inherit systemd

SYSTEMD_SERVICE:${PN} = " \
        baseboard-fru-handler.service \
    "

RDEPENDS:${PN} = "bash u-boot-fw-utils"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
SRC_URI = " \
        file://baseboard-fru-handler.sh \
        file://baseboard-fru-handler.service \
    "

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

do_install() {
    install -m 0755 ${S}/baseboard-fru-handler.sh -D -t ${D}${bindir}
    install -m 0644 ${S}/baseboard-fru-handler.service -D -t ${D}${systemd_system_unitdir}
}
