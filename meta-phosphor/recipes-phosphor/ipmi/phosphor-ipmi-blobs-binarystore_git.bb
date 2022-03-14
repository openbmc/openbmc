HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-blobs-binarystore"
SUMMARY = "BMC Generic Binary Blob Store via OEM IPMI Blob Transport"
DESCRIPTION = "This package provides a read/write/serialize abstraction for storing binary data through IPMI blobs"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-ipmi-blobs"
DEPENDS += "phosphor-logging"
DEPENDS += "protobuf-native"
DEPENDS += "protobuf"

PACKAGECONFIG ??= ""
PACKAGECONFIG[blobtool] = "-Dblobtool=enabled,-Dblobtool=disabled"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-ipmi-blobs-binarystore;branch=master;protocol=https"
SRCREV = "f3aa37a7d0ae9f360037292c11b865d85f175d83"

FILES:${PN}:append = " ${libdir}/ipmid-providers"
FILES:${PN}:append = " ${libdir}/blob-ipmid"

BLOBIPMI_PROVIDER_LIBRARY += "libbinarystore.so"

EXTRA_OEMESON:append = " -Dtests=disabled"

