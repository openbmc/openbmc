SUMMARY = "YAML configuration for Vesnin"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${YADROBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

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
