SUMMARY = "YAML configuration for IBM System1"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://system1-ipmi-fru.yaml \
    file://system1-ipmi-fru-properties.yaml \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -m 0644 -D system1-ipmi-fru.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D system1-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    "

ALLOW_EMPTY:${PN} = "1"
