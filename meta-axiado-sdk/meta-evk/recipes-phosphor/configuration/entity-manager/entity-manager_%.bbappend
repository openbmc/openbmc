FILESEXTRAPATHS:append := ":${THISDIR}/files"

SRC_URI += "file://DCSCM.json"

do_install:append() {
     rm -f ${D}/usr/share/entity-manager/configurations/*.json
     install -m 0444 ${UNPACKDIR}/DCSCM.json ${D}/usr/share/entity-manager/configurations
}
