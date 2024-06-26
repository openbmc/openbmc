SUMMARY = "GBS IPMI Entity association mapping."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += "file://entity_association_map.json"

FILES:${PN} = " \
    ${datadir}/ipmi-entity-association/entity_association_map.json \
    "

do_install() {
    install -d ${D}${datadir}/ipmi-entity-association
    install -m 0644 -D ${UNPACKDIR}/entity_association_map.json \
        ${D}${datadir}/ipmi-entity-association/entity_association_map.json
}
