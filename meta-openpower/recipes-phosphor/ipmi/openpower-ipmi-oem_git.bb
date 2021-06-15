SUMMARY = "Phosphor IPMI plugin for OpenPOWER OEM Commands"
DESCRIPTION = "Phosphor IPMI plugin for OpenPOWER OEM Commands"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink
inherit python3native

require ${BPN}.inc

DEPENDS += "phosphor-ipmi-host"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "${PYTHON_PN}-mako-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "virtual/phosphor-ipmi-inventory-sel"

TARGET_CFLAGS += "-fpic"

HOSTIPMI_PROVIDER_LIBRARY += "liboemhandler.so"

EXTRA_OECONF = " \
        INVSENSOR_YAML_GEN=${STAGING_DIR_NATIVE}${datadir}/phosphor-ipmi-host/sensor/invsensor.yaml \
        "

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"
