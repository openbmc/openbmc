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
DEPENDS += "ipmi-blob-tool"
DEPENDS += "pciutils"

PACKAGECONFIG ?= "cleanup-delete"
PACKAGECONFIG[cleanup-delete] = "--enable-cleanup-delete, --disable-cleanup-delete"

EXTRA_OECONF = "--disable-tests --disable-build-host-tool"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-ipmi-flash"
SRCREV = "380832ccf6d9986cd2e993a6c463da97eadf7997"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/blob-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

BLOBIPMI_PROVIDER_LIBRARY += "libfirmwareblob.so"
BLOBIPMI_PROVIDER_LIBRARY += "${@bb.utils.contains('PACKAGECONFIG', 'cleanup-delete', 'libfirmwarecleanupblob.so', '', d)}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"
