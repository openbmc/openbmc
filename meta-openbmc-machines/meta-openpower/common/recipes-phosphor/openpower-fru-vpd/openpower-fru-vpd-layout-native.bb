SUMMARY = "VPD layout for phosphor-openpower-fru"
PR = "r1"

inherit native
inherit phosphor-openpower-fru
inherit obmc-phosphor-license

SRC_URI += "file://layout.yaml"

PROVIDES += "virtual/phosphor-openpower-fru-vpd-layout"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${vpdlayout_datadir}

        install -d ${DEST}
        install layout.yaml ${DEST}
}
