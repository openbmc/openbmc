SUMMARY = "An implementation of XMP"
DESCRIPTION = "An implementation of XMP based on Adobe XMP SDK\
and released under a BSD-style license like Adobe's."
HOMEPAGE = "https://libopenraw.freedesktop.org/exempi/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=f816aac8b22d16134477b8e156f0a0f4"

SRC_URI = "https://libopenraw.freedesktop.org/download/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "900fb9957be2095c78e5111b99c49378adac58161a358f52f93c55126f34eb8f"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/libopenraw/exempi/-/tags"
UPSTREAM_CHECK_REGEX = "tags/(?P<pver>\d+(\.\d+)+)"

inherit autotools pkgconfig

DEPENDS = "expat zlib"

EXTRA_OECONF = "--disable-samples --disable-unittest"

PACKAGES =+ "${PN}-bin"
FILES:${PN}-bin = "${bindir}/exempi"
FILES:${PN} = "${libdir}/lib*${SOLIBS}"
FILES:${PN}-dev += "${libdir}/lib*${SOLIBSDEV} ${libdir}/pkgconfig ${includedir}"

BBCLASSEXTEND = "native nativesdk"
