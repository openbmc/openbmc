FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " file://daytonax-baseboard.json \
                   file://daytonax-chassis.json \
                 "

do_install:append() {
     rm -f ${D}/usr/share/entity-manager/configurations/*.json
     install -d ${D}/usr/share/entity-manager/configurations
     install -m 0444 ${WORKDIR}/daytonax-baseboard.json ${D}/usr/share/entity-manager/configurations
     install -m 0444 ${WORKDIR}/daytonax-chassis.json ${D}/usr/share/entity-manager/configurations
}
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
