SUMMARY = "YAML configuration for IBM Genesis3"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI:genesis3 = " \
    file://genesis3-ipmi-fru.yaml \
    file://genesis3-ipmi-fru-properties.yaml \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install:genesis3() {
    install -m 0644 -D genesis3-ipmi-fru.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D genesis3-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    "

ALLOW_EMPTY:${PN} = "1"
