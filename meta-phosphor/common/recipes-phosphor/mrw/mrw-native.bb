SUMMARY = "Phosphor OpenBMC machine readable workbook"
DESCRIPTION = "Pulls down system specific data and provides tools to process it"
PR = "r1"
S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit native

DEPENDS += "perl"
PHOSPHOR_MRW_URI ?= ""

SRC_URI += "file://Targets.pm"
SRC_URI += "${PHOSPHOR_MRW_URI}"
SRCREV = "${PHOSPHOR_MRW_REV}"

do_install() {
    if [ -n "${PHOSPHOR_MRW_URI}" ]
    then
        install -d ${bindir}
        install -m 0755 ../Targets.pm ${bindir}

        install -d ${libdir}/mrw
        install -m 0644 ${MACHINE}.xml ${libdir}/mrw
    fi
}

BBCLASSEXTEND += "nativesdk"
