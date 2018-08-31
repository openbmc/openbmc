# Provides the config file for the phosphor-fan-presence application.
# The default config file is empty.  To provide a real one,
# append this recipe in a layer, add:
# FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
# and provide a config file.

SUMMARY = "Config file for phosphor-fan-presence"
PR = "r1"

inherit native
inherit phosphor-fan
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-fan-presence-config"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
        install -D config.yaml ${D}${presence_datadir}/config.yaml
}
