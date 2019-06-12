# Provides the config file for the phosphor-fan-presence application.
# The default config file is empty.  To provide a real one,
# append this recipe in a layer, add:
# FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
# and provide a config file.

SUMMARY = "Config file for phosphor-fan-presence"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"
PROVIDES += "virtual/phosphor-fan-presence-config"

SRC_URI = "file://config.yaml"

do_install() {
        install -D config.yaml ${D}${presence_datadir}/config.yaml
}

FILES_${PN} += "${presence_datadir}/config.yaml"
