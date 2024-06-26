SUMMARY = "Quanta-Q71l IPMI to DBus Inventory mapping."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-fru
inherit native

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
        DEST=${D}${config_datadir}

        install -d ${DEST}
        install config.yaml ${DEST}
}
