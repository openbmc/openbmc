SUMMARY = "Phosphor OpenBMC machine readable workbook"
DESCRIPTION = "Pulls down system specific data"
PR = "r1"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit obmc-xmlpatch
inherit native
inherit mrw-xml

PHOSPHOR_MRW_LICENSE ?= "Apache-2.0"
PHOSPHOR_MRW_LIC_FILES_CHKSUM ?= "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"
PHOSPHOR_MRW_URI ?= "http://missing-mrw-uri"

LICENSE = "${PHOSPHOR_MRW_LICENSE}"
LIC_FILES_CHKSUM = "${PHOSPHOR_MRW_LIC_FILES_CHKSUM}"
SRC_URI += "${PHOSPHOR_MRW_URI}"
SRCREV = "${PHOSPHOR_MRW_REV}"

do_install() {
    install -d ${D}/${mrw_datadir}
    install -m 0644 ${MRW_XML} ${D}/${mrw_datadir}
}

