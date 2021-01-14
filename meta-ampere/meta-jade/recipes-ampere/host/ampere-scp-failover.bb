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
SYSTEMD_SERVICE_${PN} += "ampere-scp-failover.service"
