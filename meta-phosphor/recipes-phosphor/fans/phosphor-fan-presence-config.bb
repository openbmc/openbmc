# Provides the config file for the phosphor-fan-presence application.
# The default config file is empty.  To provide a real one,
# append this recipe in a layer, add:
# FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
# and provide a config file.

SUMMARY = "Config file for phosphor-fan-presence"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"
PROVIDES += "virtual/phosphor-fan-presence-config"

SRC_URI = "file://config.yaml"

do_install() {
        install -D config.yaml ${D}${presence_datadir}/config.yaml
}

FILES_${PN} += "${presence_datadir}/config.yaml"
