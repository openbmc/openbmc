LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f4ed2144f2ed8db87f4f530d9f68710"
inherit cmake systemd

DEPENDS = "boost sdbusplus systemd"

S = "${WORKDIR}"

SRC_URI += " \
            file://LICENSE \
            file://ampere-power-cap.cpp \
            file://CMakeLists.txt \
            file://xyz.openbmc_project.AmpPowerCap.service \
           "

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.AmpPowerCap.service"
