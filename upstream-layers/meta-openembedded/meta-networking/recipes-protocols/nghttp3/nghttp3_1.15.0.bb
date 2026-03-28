SUMMARY = "HTTP/3 library written in C"
HOMEPAGE = "https://nghttp2.org/nghttp3"
BUGTRACKER = "https://github.com/ngtcp2/nghttp3/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2005b8c7595329cc8ab211085467600a"

SRC_URI = "git://github.com/ngtcp2/nghttp3;protocol=https;branch=main;tag=v${PV};name=nghttp3 \
           git://github.com/ngtcp2/munit;protocol=https;branch=main;name=munit;subdir=${S}/tests/munit \
           git://github.com/ngtcp2/sfparse;protocol=https;branch=main;name=sfparse;subdir=${S}/lib/sfparse \
"

SRCREV_nghttp3 = "d326f4c1eb3f6a780d77793b30e16756c498f913"
SRCREV_munit = "11e8e3466b2d6a8bdfd4b05a3d1ee7805c5d3442"
SRCREV_sfparse = "ff7f230e7df2844afef7dc49631cda03a30455f3"

SRCREV_FORMAT = "nghttp3"

inherit cmake

PACKAGECONFIG ?= "shared"

PACKAGECONFIG[static] = "-DENABLE_STATIC=ON, -DENABLE_STATIC=OFF"
PACKAGECONFIG[shared] = "-DENABLE_SHARED=ON, -DENABLE_SHARED=OFF"
PACKAGECONFIG[build-lib-only] = "-DENABLE_LIB_ONLY=ON, -DENABLE_LIB_ONLY=OFF"
