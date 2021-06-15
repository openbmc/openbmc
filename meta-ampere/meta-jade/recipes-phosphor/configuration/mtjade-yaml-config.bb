SUMMARY = "YAML configuration for Mt.Jade"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
inherit allarch

SRC_URI = " \
    file://mtjade-ipmi-fru.yaml \
    file://mtjade-ipmi-fru-properties.yaml \
    "

S = "${WORKDIR}"

do_install() {
    cat mtjade-ipmi-fru.yaml > fru-read.yaml
    install -m 0644 -D mtjade-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D fru-read.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
}

FILES_${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    "

ALLOW_EMPTY_${PN} = "1"
