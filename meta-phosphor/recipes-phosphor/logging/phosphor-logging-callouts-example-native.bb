SUMMARY = "Generated callout information for phosphor-logging"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PROVIDES += "virtual/phosphor-logging-callouts"
PR = "r1"

SRC_URI += "file://callouts.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit phosphor-logging
inherit native

do_install() {
        DEST=${D}${callouts_datadir}
        install -d ${DEST}
        install callouts.yaml ${DEST}
}
