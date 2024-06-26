# Provides the config file for the phosphor-fan-presence application.
# The default config file is empty.  To provide a real one,
# append this recipe in a layer, add:
# FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
# and provide a config file.
SUMMARY = "Config file for phosphor-fan-presence"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PROVIDES += "virtual/phosphor-fan-presence-config"
PR = "r1"

SRC_URI = "file://config.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit phosphor-fan

do_install() {
        install -D config.yaml ${D}${presence_datadir}/config.yaml
}

FILES:${PN} += "${presence_datadir}/config.yaml"
