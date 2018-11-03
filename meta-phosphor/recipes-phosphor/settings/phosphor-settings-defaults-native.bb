SUMMARY = "Default settings"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-settings-manager

SRC_URI += "file://defaults.yaml"

PROVIDES += "virtual/phosphor-settings-defaults"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${settings_datadir}
        install -d ${DEST}
        install defaults.yaml ${DEST}
}
