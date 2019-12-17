SUMMARY = "Power sequencer data definition"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = "file://ucd90160.yaml"

FILES_${PN} += "${datadir}/power-sequencer/ucd90160.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${datadir}/power-sequencer

    install -D ucd90160.yaml ${DEST}/ucd90160.yaml
}
