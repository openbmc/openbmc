HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-flash"
SUMMARY = "Phosphor OEM IPMI In-band Firmware Update over BLOB"
DESCRIPTION = "This package handles a series of OEM IPMI commands that implement the firmware update handler over the BLOB protocol."
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-ipmi-blobs"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-ipmi-flash"
SRCREV = "7b91cbc101f36f1038d595f99a7423ee4de6575b"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/blob-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

BLOBIPMI_PROVIDER_LIBRARY += "libfirmwareblob.so"
