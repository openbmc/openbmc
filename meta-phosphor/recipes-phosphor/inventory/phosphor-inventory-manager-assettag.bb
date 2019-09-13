SUMMARY = "Recipe to create AssetTag property in inventory manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-inventory-manager

PROVIDES += "virtual/phosphor-inventory-manager-assettag"
S = "${WORKDIR}"

SRC_URI = "file://assettag.yaml"

do_install() {
        install -D assettag.yaml ${D}${base_datadir}/events.d/assettag.yaml
}

FILES_${PN} += "${base_datadir}/events.d/assettag.yaml"
