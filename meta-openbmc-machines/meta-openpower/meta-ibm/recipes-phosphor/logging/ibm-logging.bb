SUMMARY = "IBM enhanced error logging"
DESCRIPTION = "Adds additional error logging functionality for IBM systems"
PR = "r1"
HOMEPAGE = "https://github.com/openbmc/ibm-logging"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRC_URI += "git://github.com/openbmc/ibm-logging"
SRCREV = "259e7277e6af53d2c4862cd48c14131c0b22bb81"

inherit autotools
inherit pkgconfig
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd

DEPENDS += " \
         ibm-dbus-interfaces \
         phosphor-logging \
         nlohmann-json \
         autoconf-archive-native \
         sdbusplus \
         "

RDEPENDS_${PN} += " \
         phosphor-logging \
         phosphor-dbus-interfaces \
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
