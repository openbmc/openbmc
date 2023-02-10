FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

SRC_URI:append:buv-runbmc = " file://F0B_BMC_BMC.json"
SRC_URI:append:buv-runbmc = " file://blacklist.json"

FILES:${PN}:append:buv-runbmc = " \
    ${datadir}/entity-manager/F0B_BMC_BMC.json"

do_install:append:buv-runbmc() {
    rm -f ${D}/usr/share/entity-manager/configurations/*.json
    install -d ${D}${datadir}/entity-manager
    install -m 0644 -D ${WORKDIR}/blacklist.json\
        ${D}${datadir}/entity-manager/blacklist.json
    install -m 0644 -D ${WORKDIR}/F0B_BMC_BMC.json \
        ${D}${datadir}/entity-manager/configurations/F0B_BMC_BMC.json
}
