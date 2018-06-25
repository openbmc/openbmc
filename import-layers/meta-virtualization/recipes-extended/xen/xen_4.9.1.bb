FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
require xen.inc

SRC_URI = " \
    https://downloads.xenproject.org/release/xen/${PV}/xen-${PV}.tar.gz \
    file://xsa246-4.9.patch \
    file://0001-p2m-Always-check-to-see-if-removing-a-p2m-entry-actu.patch \
    file://0002-p2m-Check-return-value-of-p2m_set_entry-when-decreas.patch \
    file://xsa248.patch \
    file://xsa249.patch \
    file://xsa250.patch \
    file://xsa251.patch \
    "

SRC_URI[md5sum] = "8b9d6104694b164d54334194135f7217"
SRC_URI[sha256sum] = "ecf88b01f44cd8f4ef208af3f999dceb69bdd2a316d88dd9a9535ea7b49ed356"

S = "${WORKDIR}/xen-${PV}"
