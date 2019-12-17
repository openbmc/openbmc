SUMMARY = "IBM enhanced error logging"
DESCRIPTION = "Adds additional error logging functionality for IBM systems"
PR = "r1"
PV = "1.0+git${SRCPV}"
HOMEPAGE = "https://github.com/openbmc/ibm-logging"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRC_URI += "git://github.com/openbmc/ibm-logging"
SRCREV = "4c0e8945f072f9139d0efb04e8f955663b972a84"

inherit autotools
inherit pkgconfig
inherit pythonnative
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd
inherit phosphor-dbus-yaml

DEPENDS += " \
         ibm-dbus-interfaces \
         phosphor-logging \
         nlohmann-json \
         autoconf-archive-native \
         sdbusplus \
         "

S = "${WORKDIR}/git"

SRC_URI += "file://policyTable.json"

PACKAGECONFIG ??= ""
PACKAGECONFIG[policy-interface] = "--enable-policy-interface, --disable-policy-interface,,"

SERVICE = "com.ibm.Logging.service"
DBUS_SERVICE_${PN} += "${SERVICE}"

#The link is so that this service will restart if phosphor-logging restarts.
#The BindsTo in the service will not do the restart, it will only do the
#original start and a stop.
LOG_FMT = "../${SERVICE}:xyz.openbmc_project.Logging.service.wants/${SERVICE}"
SYSTEMD_LINK_${PN} += "${LOG_FMT}"

do_install_append(){

    install -d ${D}${datadir}/ibm-logging

    ${S}/condense_policy.py \
        -p ${WORKDIR}/policyTable.json \
        -c ${D}/${datadir}/ibm-logging/policy.json
}

#An optional task to generate a report on all of the errors
#created by OpenBMC, and compare these errors to what is
#in the error policy table
do_report(){

    ${S}/create_error_reports.py \
        -p ${D}/${datadir}/ibm-logging/policy.json \
        -y ${STAGING_DIR_NATIVE}${yaml_dir} \
        -e ${WORKDIR}/build/all_errors.json \
        -x ${WORKDIR}/build/policy_crosscheck.txt

}

addtask report

#Collect all of the error YAML files into our recipe-sysroot-native dir.
do_report[depends] = " \
                     ibm-logging:do_install \
                     phosphor-logging-error-logs-native:do_populate_sysroot \
                     phosphor-dbus-interfaces-native:do_populate_sysroot \
                     openpower-dbus-interfaces-native:do_populate_sysroot \
                     openpower-occ-control-native:do_populate_sysroot  \
                     openpower-debug-collector-native:do_populate_sysroot \
                     "
