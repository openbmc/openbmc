PACKAGECONFIG:append:p10bmc = " openpower-pels"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:p10bmc = " file://com.ibm.Hardware.Chassis.Model.Rainier2U_dev_callouts.json"
SRC_URI:append:p10bmc = " file://com.ibm.Hardware.Chassis.Model.Rainier4U_dev_callouts.json"
SRC_URI:append:p10bmc = " file://com.ibm.Hardware.Chassis.Model.Everest_dev_callouts.json"
SRC_URI:append:p10bmc = " file://com.ibm.Hardware.Chassis.Model.Bonnell_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Rainier2U_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Rainier4U_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Everest_dev_callouts.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Bonnell_dev_callouts.json"

#Enable phal feature, if available.
PACKAGECONFIG:append = " ${@bb.utils.filter('MACHINE_FEATURES', 'phal', d)}"
PACKAGECONFIG[phal] = "-Dphal=enabled, -Dphal=disabled, pdata libekb pdbg ipl"

do_install:append:p10bmc() {
    install -d ${D}/${datadir}/phosphor-logging/pels
    install -m 0644 ${UNPACKDIR}/com.ibm.Hardware.Chassis.Model.Rainier2U_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Rainier2U_dev_callouts.json
    install -m 0644 ${UNPACKDIR}/com.ibm.Hardware.Chassis.Model.Rainier4U_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Rainier4U_dev_callouts.json
    install -m 0644 ${UNPACKDIR}/com.ibm.Hardware.Chassis.Model.Everest_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Everest_dev_callouts.json
    install -m 0644 ${UNPACKDIR}/com.ibm.Hardware.Chassis.Model.Bonnell_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Bonnell_dev_callouts.json
    ln -s ./com.ibm.Hardware.Chassis.Model.Rainier4U_dev_callouts.json ${D}/${datadir}/phosphor-logging/pels/com.ibm.Hardware.Chassis.Model.Rainier1S4U_dev_callouts.json
}
