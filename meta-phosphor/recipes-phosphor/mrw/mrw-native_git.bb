SUMMARY = "Phosphor OpenBMC machine readable workbook"
DESCRIPTION = "Pulls down system specific data"
LICENSE = "${PHOSPHOR_MRW_LICENSE}"
LIC_FILES_CHKSUM = "${PHOSPHOR_MRW_LIC_FILES_CHKSUM}"
SRCREV = "${PHOSPHOR_MRW_REV}"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "${PHOSPHOR_MRW_URI}"

S = "${WORKDIR}/git"

inherit obmc-xmlpatch
inherit mrw-xml
inherit native

do_install() {
    install -d ${D}/${mrw_datadir}
    install -m 0644 ${MRW_XML} ${D}/${mrw_datadir}
}

PHOSPHOR_MRW_LICENSE ?= "Apache-2.0"
PHOSPHOR_MRW_LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PHOSPHOR_MRW_URI ?= "http://missing-mrw-uri"
