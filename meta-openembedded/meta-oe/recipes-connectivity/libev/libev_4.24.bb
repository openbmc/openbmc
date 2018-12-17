SUMMARY = "A full-featured and high-performance event loop that is loosely \
modelled after libevent."
HOMEPAGE = "http://software.schmorp.de/pkg/libev.html"
LICENSE = "BSD-2-Clause | GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6ad416afd040c90698edcdf1cbee347"

SRC_URI = "http://dist.schmorp.de/libev/Attic/${BP}.tar.gz"

SRC_URI[md5sum] = "94459a5a22db041dec6f98424d6efe54"
SRC_URI[sha256sum] = "973593d3479abdf657674a55afe5f78624b0e440614e2b8cb3a07f16d4d7f821"

inherit autotools

EXTRA_OECONF += "--with-pic"

do_install_append() {
    # Avoid conflicting with libevent. The provided compatibility layer is
    # still basic so drop it for now.
    rm ${D}${includedir}/event.h
}
