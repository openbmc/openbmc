DESCRIPTION = "Program vbios"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SRC_URI += " file://program-vbios.service \
             file://program-vbios.sh \
             file://program-vbios \
             file://vbios.bin \
             file://dontload.conf \
           "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "program-vbios.service"
SYSTEMD_ENVIRONMENT_FILE:${PN} +="obmc/vbios/program_vbios"
FILES:${PN} += "/usr/share/vbios/vbios.bin"
FILES:${PN} += "${sysconfdir}/modprobe.d/dontload.conf"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/program-vbios.sh ${D}${bindir}/
    install -m 0755 ${WORKDIR}/program-vbios ${D}${bindir}/
    install -d ${D}${datadir}/vbios
    install -m 0644 -D ${WORKDIR}/vbios.bin \
        ${D}${datadir}/vbios/vbios.bin
    install -d ${D}${sysconfdir}/modprobe.d/
    install -m 644 ${WORKDIR}/dontload.conf ${D}${sysconfdir}/modprobe.d/dontload.conf
}
