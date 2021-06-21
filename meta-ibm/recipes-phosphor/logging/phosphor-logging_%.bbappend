PACKAGECONFIG_append_witherspoon-tacoma = " openpower-pels"
PACKAGECONFIG_append_p10bmc = " openpower-pels"

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append_p10bmc = " file://ibm,rainier-2u_dev_callouts.json"
SRC_URI_append_p10bmc = " file://ibm,rainier-4u_dev_callouts.json"
FILES_${PN}_append_p10bmc = " ${datadir}/phosphor-logging/pels/ibm,rainier-2u_dev_callouts.json"
FILES_${PN}_append_p10bmc = " ${datadir}/phosphor-logging/pels/ibm,rainier-4u_dev_callouts.json"

#Enable phal feature, if available.
PACKAGECONFIG_append = " ${@bb.utils.filter('OBMC_MACHINE_FEATURES', 'phal', d)}"
PACKAGECONFIG[phal] = "-Dphal=enabled, -Dphal=disabled, pdata libekb pdbg"

do_install_append_p10bmc() {
    install -d ${D}/${datadir}/phosphor-logging/pels
    install -m 0644 ${WORKDIR}/ibm,rainier-2u_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,rainier-2u_dev_callouts.json
    install -m 0644 ${WORKDIR}/ibm,rainier-4u_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,rainier-4u_dev_callouts.json
    ln -s ./ibm,rainier-4u_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/ibm,rainier-1s4u_dev_callouts.json
}
