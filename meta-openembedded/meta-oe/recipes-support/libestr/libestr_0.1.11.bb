SUMMARY = "some essentials for string handling (and a bit more)"
HOMEPAGE = "http://libestr.adiscon.com/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=9d6c993486c18262afba4ca5bcb894d0"

SRC_URI = "http://libestr.adiscon.com/files/download/${BP}.tar.gz"

SRC_URI[md5sum] = "1f25a2332750d4bfacfb314235fedff0"
SRC_URI[sha256sum] = "46632b2785ff4a231dcf241eeb0dcb5fc0c7d4da8ee49cf5687722cdbe8b2024"

UPSTREAM_CHECK_URI = "http://libestr.adiscon.com/download/"

inherit autotools
