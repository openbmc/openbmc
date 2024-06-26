SUMMARY = "Copy the static inventory yaml for inventory manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-inventory-manager

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI = " file://static-inventory.yaml"

do_install() {
    install -D static-inventory.yaml ${D}${base_datadir}/events.d/static-inventory.yaml
}

FILES:${PN} += "${base_datadir}/events.d/static-inventory.yaml"
