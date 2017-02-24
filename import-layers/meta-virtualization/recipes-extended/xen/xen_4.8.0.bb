require xen.inc

SRC_URI = " \
    https://downloads.xenproject.org/release/xen/${PV}/xen-${PV}.tar.gz \
    "

SRC_URI[md5sum] = "d738f7c741110342621cb8a4d10b0191"
SRC_URI[sha256sum] = "1e15c713ab7ba3bfda8b4a285ed973529364fd1100e6dd5a61f29583dc667b04"

S = "${WORKDIR}/xen-${PV}"
