SUMMARY = "Power sequencer data definition"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI:append:ibm-ac-server = " file://ucd90160.yaml"

FILES:${PN}:append:ibm-ac-server = " ${datadir}/power-sequencer/ucd90160.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install:append:ibm-ac-server() {
    DEST=${D}${datadir}/power-sequencer

    install -D ucd90160.yaml ${DEST}/ucd90160.yaml
}

