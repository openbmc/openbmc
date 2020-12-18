LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f4ed2144f2ed8db87f4f530d9f68710"

inherit cmake systemd

DEPENDS = "boost sdbusplus libgpiod systemd phosphor-dbus-interfaces phosphor-logging gpioplus"
RDEPENDS_${PN} += "libsystemd bash"

S = "${WORKDIR}"

SRC_URI += " \
            file://LICENSE \
            file://scp-failover.cpp \
            file://CMakeLists.txt \
            file://ampere-scp-failover.service \
           "

# Run service after chassis power on
TMPL_HOST_MNT = "ampere-scp-failover.service"
INSTFMT_MNT = "ampere-scp-failover.service"
POWERON_TGT = "obmc-chassis-poweron@{0}.target"
FMT_MNT = "../${TMPL_HOST_MNT}:${POWERON_TGT}.requires/${INSTFMT_MNT}"

FILES_${PN} = "${bindir}/ampere-scp-failover"
SYSTEMD_SERVICE_${PN} += "${TMPL_HOST_MNT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_MNT', 'OBMC_CHASSIS_INSTANCES')}"
