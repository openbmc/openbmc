SUMMARY = "Program EDID data when use DP connector"
DESCRIPTION = "Add program EDID data support for DDC to SMB internal \
loopback mode."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd
DEPENDS = "systemd python3-edid-json-tool-native"
RDEPENDS:${PN} += "bash"

SRC_URI = "file://program-edid.service \
           file://program-edid.sh \
           file://edid.json \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "program-edid.service"
SYSTEMD_ENVIRONMENT_FILE:${PN} +="obmc/edid/program_edid"
FILES:${PN} += "/usr/share/edid/edid.bin"

do_compile() {
    json2edid ${UNPACKDIR}/edid.json ${UNPACKDIR}/edid.bin
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/program-edid.sh ${D}${bindir}/
    install -d ${D}${datadir}/edid
    install -m 0644 -D ${UNPACKDIR}/edid.bin \
        ${D}${datadir}/edid/edid.bin
}
