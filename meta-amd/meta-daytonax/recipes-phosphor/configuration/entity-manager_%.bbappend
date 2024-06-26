FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " file://daytonax-baseboard.json \
                   file://daytonax-chassis.json \
                 "

do_install:append() {
     rm -f ${D}${datadir}/entity-manager/configurations/*.json
     install -d ${D}${datadir}/entity-manager/configurations
     install -m 0444 ${UNPACKDIR}/daytonax-baseboard.json ${D}${datadir}/entity-manager/configurations
     install -m 0444 ${UNPACKDIR}/daytonax-chassis.json ${D}${datadir}/entity-manager/configurations
}
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
