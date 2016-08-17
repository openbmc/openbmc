require xen.inc

SRC_URI = " \
    http://bits.xensource.com/oss-xen/release/${PV}/xen-${PV}.tar.gz \
    "

SRC_URI[md5sum] = "df2d854c3c90ffeefaf71e7f868fb326"
SRC_URI[sha256sum] = "44cc2fccba1e147ef4c8da0584ce0f24189c8743de0e3e9a9226da88ddb5f589"

S = "${WORKDIR}/xen-${PV}"
