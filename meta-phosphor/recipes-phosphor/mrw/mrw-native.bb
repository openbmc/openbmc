SUMMARY = "Phosphor OpenBMC machine readable workbook"
DESCRIPTION = "Pulls down system specific data"
PR = "r1"

S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit obmc-xmlpatch
inherit native
inherit mrw-xml

PHOSPHOR_MRW_URI ?= "http://missing-mrw-uri"
SRC_URI += "${PHOSPHOR_MRW_URI}"
SRCREV = "${PHOSPHOR_MRW_REV}"

do_install() {
    install -d ${D}/${mrw_datadir}
    install -m 0644 ${MRW_XML} ${D}/${mrw_datadir}
}

