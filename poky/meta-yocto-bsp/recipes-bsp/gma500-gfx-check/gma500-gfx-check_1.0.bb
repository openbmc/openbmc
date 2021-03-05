SUMMARY = "Intel gma500_gfx fix for certain hardware"
DESCRIPTION = "Avoid inserting gma500_gfx module for certain hardware devices."
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://gma500-gfx-check.conf \
	file://gma500-gfx-check.sh "

do_install(){
    install -d ${D}${sysconfdir}/modprobe.d/
    install -m 755 ${WORKDIR}/gma500-gfx-check.sh ${D}${sysconfdir}/modprobe.d/gma500-gfx-check.sh
    install -m 644 ${WORKDIR}/gma500-gfx-check.conf ${D}${sysconfdir}/modprobe.d/gma500-gfx-check.conf
}

FILES_${PN}="${sysconfdir}/modprobe.d/gma500-gfx-check.conf \
             ${sysconfdir}/modprobe.d/gma500-gfx-check.sh"

COMPATIBLE_MACHINE = "genericx86"
