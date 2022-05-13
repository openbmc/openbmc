SUMMARY = "pldmsensors"
DESCRIPTION = "PLDM Sensor Services Configured from D-Bus"

SRC_URI = "git://github.com/Nuvoton-Israel/pldmsensors.git;protocol=https;branch=main;"

SRCREV = "c0bac1546c0f4c5c38eaf0ab6b0cc473a6823ec2"

PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SYSTEMD_SERVICE_${PN} = " xyz.openbmc_project.pldmsensor.service"

DEPENDS = "boost nlohmann-json sdbusplus i2c-tools libgpiod libpldm-intel "
inherit cmake systemd

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DYOCTO=1"

SRC_URI += "file://pldm.json "

do_install:append() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/pldm.json ${D}${sysconfdir}/default
}
