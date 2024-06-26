SUMMARY = "Debug collector error watch config file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PROVIDES += "virtual/phosphor-debug-errors"
PR = "r1"

SRC_URI += "file://errors_watch.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit native

do_install:append() {
    DEST=${D}${datadir}/dump
    install -d ${DEST}
    install errors_watch.yaml ${DEST}/
}
