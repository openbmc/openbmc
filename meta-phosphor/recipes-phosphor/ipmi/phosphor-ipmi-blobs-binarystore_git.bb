SUMMARY = "BMC Generic Binary Blob Store via OEM IPMI Blob Transport"
DESCRIPTION = "This package provides a read/write/serialize abstraction for storing binary data through IPMI blobs"
HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-blobs-binarystore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "phosphor-ipmi-blobs"
DEPENDS += "phosphor-logging"
DEPENDS += "nanopb-generator-native"
DEPENDS += "nanopb-runtime"
SRCREV = "719b65fd9eac8517029d89d300de11c2cc787bcc"
PACKAGECONFIG ??= ""
PACKAGECONFIG[blobtool] = "-Dblobtool=enabled,-Dblobtool=disabled"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-ipmi-blobs-binarystore;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'blobtool', 'binarystore-migration.service', '', d)}"

EXTRA_OEMESON:append = " -Dtests=disabled"

FILES:${PN}:append = " ${libdir}/ipmid-providers"
FILES:${PN}:append = " ${libdir}/blob-ipmid"

BLOBIPMI_PROVIDER_LIBRARY += "libbinarystore.so"
