LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f4ed2144f2ed8db87f4f530d9f68710"
inherit cmake systemd

DEPENDS = "boost sdbusplus libgpiod systemd phosphor-dbus-interfaces phosphor-logging"
RDEPENDS_${PN} += "libsystemd bash"

S = "${WORKDIR}"

SRC_URI += " \
            file://LICENSE \
            file://src/ampere-power-logger.cpp \
            file://CMakeLists.txt \
            file://service_files/xyz.openbmc_project.power_logger.service \
           "

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.power_logger.service"