SUMMARY = "A full-featured and high-performance event loop that is loosely \
modelled after libevent."
HOMEPAGE = "http://software.schmorp.de/pkg/libev.html"
LICENSE = "BSD-2-Clause | GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6ad416afd040c90698edcdf1cbee347"

SRC_URI = "http://dist.schmorp.de/libev/Attic/${BP}.tar.gz"
SRC_URI[md5sum] = "d38925fbc030153fe674c4e0e864a69a"
SRC_URI[sha256sum] = "2d5526fc8da4f072dd5c73e18fbb1666f5ef8ed78b73bba12e195cfdd810344e"

inherit autotools

EXTRA_OECONF += "--with-pic"

do_install_append() {
    # Avoid conflicting with libevent. The provided compatibility layer is
    # still basic so drop it for now.
    rm ${D}${includedir}/event.h
}
