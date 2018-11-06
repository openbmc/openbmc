SUMMARY = "VPD layout for openpower-fru-vpd"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit openpower-fru-vpd

SRC_URI += "file://layout.yaml"

PROVIDES += "virtual/openpower-fru-vpd-layout"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${vpdlayout_datadir}

        install -d ${DEST}
        install layout.yaml ${DEST}
}
