SUMMARY = "YAML configuration for Vesnin"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://vesnin-ipmi-fru.yaml \
    "

S = "${WORKDIR}"

do_install() {
    install -m 0644 -D vesnin-ipmi-fru.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
}

FILES_${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    "

ALLOW_EMPTY_${PN} = "1"
