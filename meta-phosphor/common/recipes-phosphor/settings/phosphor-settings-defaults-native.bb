SUMMARY = "Default settings"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-settings-manager

SRC_URI += "file://defaults.yaml"

PROVIDES += "virtual/phosphor-settings-defaults"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${settings_datadir}
        install -d ${DEST}
        install defaults.yaml ${DEST}
}
