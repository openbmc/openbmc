SUMMARY = "Debug collector error watch config file"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit native

SRC_URI += "file://errors_watch.yaml"

PROVIDES += "virtual/phosphor-debug-errors"

S = "${WORKDIR}"

do_install_append() {
    DEST=${D}${datadir}/dump
    install -d ${DEST}
    install errors_watch.yaml ${DEST}/
}

