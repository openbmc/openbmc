
SUMMARY = "PCH BMC time service"
DESCRIPTION = "This service will read date/time from PCH device periodically, and set the BMC system time accordingly"

SRC_URI = "\
    file://CMakeLists.txt \
    file://pch-time-sync.cpp \
    "
PV = "0.1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}"

SYSTEMD_SERVICE:${PN} = "pch-time-sync.service"

inherit cmake
inherit obmc-phosphor-systemd

DEPENDS += " \
            sdbusplus \
            phosphor-logging \
            boost \
            i2c-tools \
           "
