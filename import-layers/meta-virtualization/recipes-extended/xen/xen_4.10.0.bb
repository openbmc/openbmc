FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
require xen.inc

SRC_URI = " \
    https://downloads.xenproject.org/release/xen/${PV}/xen-${PV}.tar.gz \
    file://xsa253.patch \
    "

SRC_URI[md5sum] = "ab9d320d02cb40f6b40506aed1a38d58"
SRC_URI[sha256sum] = "0262a7023f8b12bcacfb0b25e69b2a63291f944f7683d54d8f33d4b2ca556844"

S = "${WORKDIR}/xen-${PV}"
