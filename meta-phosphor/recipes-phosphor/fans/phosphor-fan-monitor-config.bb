SUMMARY = "Phosphor fan monitor definition default data"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI = "file://monitor.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit phosphor-fan

do_install() {
    DEST=${D}${monitor_datadir}
    install -D monitor.yaml ${D}${monitor_datadir}/monitor.yaml
}

FILES:${PN} += "${monitor_datadir}/monitor.yaml"
