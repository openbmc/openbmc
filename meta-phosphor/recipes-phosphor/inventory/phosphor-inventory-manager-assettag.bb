SUMMARY = "Recipe to create AssetTag property in inventory manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PROVIDES += "virtual/phosphor-inventory-manager-assettag"
PR = "r1"

SRC_URI = "file://assettag.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit phosphor-inventory-manager

do_install() {
        install -D assettag.yaml ${D}${base_datadir}/events.d/assettag.yaml
}

FILES:${PN} += "${base_datadir}/events.d/assettag.yaml"
