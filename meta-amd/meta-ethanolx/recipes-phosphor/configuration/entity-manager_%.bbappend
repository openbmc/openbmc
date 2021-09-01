FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " file://ethanolx-baseboard.json \
                   file://ethanolx-chassis.json"

do_install:append() {
     rm -f ${D}/usr/share/entity-manager/configurations/*.json
     install -d ${D}/usr/share/entity-manager/configurations
     install -m 0444 ${WORKDIR}/ethanolx-baseboard.json ${D}/usr/share/entity-manager/configurations
     install -m 0444 ${WORKDIR}/ethanolx-chassis.json ${D}/usr/share/entity-manager/configurations
}
