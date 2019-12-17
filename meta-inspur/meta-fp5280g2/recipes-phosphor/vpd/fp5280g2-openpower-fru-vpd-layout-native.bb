SUMMARY = "Inspur FP5280G2 VPD layout for openpower-fru-vpd"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

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
