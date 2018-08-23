SUMMARY = "VPD layout for openpower-fru-vpd"
PR = "r1"

inherit native
inherit openpower-fru-vpd
inherit obmc-phosphor-license

SRC_URI += "file://layout.yaml"

PROVIDES += "virtual/openpower-fru-vpd-layout"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${vpdlayout_datadir}

        install -d ${DEST}
        install layout.yaml ${DEST}
}
