PACKAGECONFIG:append:witherspoon-tacoma = " openpower-pels"
PACKAGECONFIG:append:p10bmc = " openpower-pels"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:p10bmc = " file://ibm,rainier-2u_dev_callouts.json"
SRC_URI:append:p10bmc = " file://ibm,rainier-4u_dev_callouts.json"
SRC_URI:append:p10bmc = " file://ibm,everest_dev_callouts.json"
SRC_URI:append:p10bmc = " file://ibm,bonnell_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/ibm,rainier-2u_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/ibm,rainier-4u_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/ibm,everest_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/ibm,bonnell_dev_callouts.json"

#Enable phal feature, if available.
PACKAGECONFIG:append = " ${@bb.utils.filter('MACHINE_FEATURES', 'phal', d)}"
PACKAGECONFIG[phal] = "-Dphal=enabled, -Dphal=disabled, pdata libekb pdbg ipl"

do_install:append:p10bmc() {
    install -d ${D}/${datadir}/phosphor-logging/pels
    install -m 0644 ${WORKDIR}/ibm,rainier-2u_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,rainier-2u_dev_callouts.json
    install -m 0644 ${WORKDIR}/ibm,rainier-4u_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,rainier-4u_dev_callouts.json
    install -m 0644 ${WORKDIR}/ibm,everest_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,everest_dev_callouts.json
    install -m 0644 ${WORKDIR}/ibm,bonnell_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,bonnell_dev_callouts.json
    ln -s ./ibm,rainier-4u_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,rainier-1s4u_dev_callouts.json
}
