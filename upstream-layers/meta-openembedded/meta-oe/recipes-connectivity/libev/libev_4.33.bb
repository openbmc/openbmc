SUMMARY = "A full-featured and high-performance event loop that is loosely \
modelled after libevent."
HOMEPAGE = "http://software.schmorp.de/pkg/libev.html"
LICENSE = "BSD-2-Clause | GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6ad416afd040c90698edcdf1cbee347"

SRC_URI = "http://dist.schmorp.de/libev/Attic/${BP}.tar.gz"
SRC_URI[sha256sum] = "507eb7b8d1015fbec5b935f34ebed15bf346bed04a11ab82b8eee848c4205aea"

inherit autotools

EXTRA_OECONF += "--with-pic"

do_install:append() {
    # Avoid conflicting with libevent. The provided compatibility layer is
    # still basic so drop it for now.
    rm ${D}${includedir}/event.h
}

BBCLASSEXTEND = "native"
