DESCRIPTION = "Program vbios"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SRC_URI += " file://program-edid.service \
             file://program-edid.sh \
             file://edid.bin \
           "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "program-edid.service"
SYSTEMD_ENVIRONMENT_FILE:${PN} +="obmc/edid/program_edid"
FILES:${PN} += "/usr/share/edid/edid.bin"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/program-edid.sh ${D}${bindir}/
    install -d ${D}${datadir}/edid
    install -m 0644 -D ${WORKDIR}/edid.bin \
        ${D}${datadir}/edid/edid.bin
}
