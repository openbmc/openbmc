LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f4ed2144f2ed8db87f4f530d9f68710"
inherit cmake systemd

DEPENDS = "boost sdbusplus libgpiod systemd phosphor-dbus-interfaces phosphor-logging"
RDEPENDS_${PN} += "libsystemd bash"

S = "${WORKDIR}"

SRC_URI += " \
            file://LICENSE \
            file://include/* \
            file://src/tempevent_log.cpp \
            file://CMakeLists.txt \
           "

EXTRA_OECMAKE = "-DYOCTO=1"
