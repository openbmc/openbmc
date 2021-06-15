SUMMARY = "Phosphor fan monitor definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"

SRC_URI = "file://monitor.yaml"

do_install() {
    DEST=${D}${monitor_datadir}
    install -D monitor.yaml ${D}${monitor_datadir}/monitor.yaml
}

FILES_${PN} += "${monitor_datadir}/monitor.yaml"
