SUMMARY = "Inventory config for openpower-vpd-parser"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit openpower-fru-vpd
inherit native

SRC_URI += "file://inventory"

PROVIDES += "virtual/openpower-fru-inventory"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
        # This recipe is supposed to create a systemd environment file
        # with values for FRU types and paths. This example recipe
        # uses a pre-defined file ($PN/inventory).

        DEST=${D}${inventory_datadir_native}
        install -d ${DEST}
        install inventory ${DEST}
}
