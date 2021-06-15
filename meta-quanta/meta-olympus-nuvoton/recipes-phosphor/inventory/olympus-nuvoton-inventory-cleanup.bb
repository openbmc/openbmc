SUMMARY = "Copy the inventory cleanup yaml for inventory manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-inventory-manager

S = "${WORKDIR}"

SRC_URI_olympus-nuvoton = "file://inventory-cleanup.yaml"

do_install_olympus-nuvoton() {
        install -D inventory-cleanup.yaml ${D}${base_datadir}/events.d/inventory-cleanup.yaml
}

FILES_${PN}_olympus-nuvoton += "${base_datadir}/events.d/inventory-cleanup.yaml"
