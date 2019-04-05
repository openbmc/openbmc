SUMMARY = "A full-featured and high-performance event loop that is loosely \
modelled after libevent."
HOMEPAGE = "http://software.schmorp.de/pkg/libev.html"
LICENSE = "BSD-2-Clause | GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6ad416afd040c90698edcdf1cbee347"

SRC_URI = "http://dist.schmorp.de/libev/Attic/${BP}.tar.gz"
SRC_URI[md5sum] = "911daf566534f745726015736a04f04a"
SRC_URI[sha256sum] = "78757e1c27778d2f3795251d9fe09715d51ce0422416da4abb34af3929c02589"

inherit autotools

EXTRA_OECONF += "--with-pic"

do_install_append() {
    # Avoid conflicting with libevent. The provided compatibility layer is
    # still basic so drop it for now.
    rm ${D}${includedir}/event.h
}
