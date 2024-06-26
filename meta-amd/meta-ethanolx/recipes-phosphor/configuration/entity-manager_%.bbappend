FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " file://ethanolx-baseboard.json \
                   file://ethanolx-chassis.json"

do_install:append() {
     rm -f ${D}${datadir}/entity-manager/configurations/*.json
     install -d ${D}${datadir}/entity-manager/configurations
     install -m 0444 ${UNPACKDIR}/ethanolx-baseboard.json ${D}${datadir}/entity-manager/configurations
     install -m 0444 ${UNPACKDIR}/ethanolx-chassis.json ${D}${datadir}/entity-manager/configurations
}
