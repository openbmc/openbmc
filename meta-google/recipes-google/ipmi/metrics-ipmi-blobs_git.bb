HOMEPAGE = "http://github.com/openbmc/google-misc"
SUMMARY = "gBMC Health Metrics Blob"
DESCRIPTION = "BMC health metrics IPMI blob handler."
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"

inherit meson pkgconfig

DEPENDS += "phosphor-ipmi-blobs"
DEPENDS += "phosphor-logging"
DEPENDS += "protobuf-native"
DEPENDS += "protobuf"

SRC_URI = "git://github.com/openbmc/google-misc"
SRCREV = "3f43b7eebe17c96c13643018c927f38c0a071868"
S = "${WORKDIR}/git/metrics-ipmi-blobs"

FILES_${PN} += "${libdir}/ipmid-providers/libmetricsblob.so*"
FILES_${PN} += "${libdir}/blob-ipmid/libmetricsblob.so*"
INSANE_SKIP_${PN} += "dev-so"

BLOBIPMI_PROVIDER_LIBRARY += "libmetricsblob.so"

do_install_append() {
    install -d ${D}/${libdir}/blob-ipmid
    ln -s ../ipmid-providers/libmetricsblob.so ${D}/${libdir}/blob-ipmid/libmetricsblob.so.0
}
