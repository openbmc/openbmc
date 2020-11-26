LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f4ed2144f2ed8db87f4f530d9f68710"
inherit cmake systemd

DEPENDS = "boost sdbusplus libgpiod systemd phosphor-dbus-interfaces phosphor-logging"
RDEPENDS_${PN} += "libsystemd bash"

S = "${WORKDIR}"

SRC_URI += " \
            file://LICENSE \
            file://include/bootprogress.hpp \
            file://src/bootprogress.cpp \
            file://CMakeLists.txt \
            file://service_files/xyz.openbmc_project.bootprogress.service \
           "

# Needed to install into the obmc-chassis-poweron target
TMPL_HOST_MNT = "xyz.openbmc_project.bootprogress.service"
INSTFMT_MNT = "xyz.openbmc_project.bootprogress.service"
POWERON_TGT = "obmc-chassis-poweron@{0}.target"
FMT_MNT = "../${TMPL_HOST_MNT}:${POWERON_TGT}.requires/${INSTFMT_MNT}"

FILES_${PN} = "${bindir}/ampere-boot-progress"
SYSTEMD_SERVICE_${PN} += "${TMPL_HOST_MNT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_MNT', 'OBMC_CHASSIS_INSTANCES')}"
