SUMMARY = "Various benchmarning tests for X"
HOMEPAGE = "http://www.o-hand.com"
SECTION = "devel"
LICENSE = "Zlib"
DEPENDS = "pango libxext libxft virtual/libx11 gtk+"

SRCREV = "e5939ff608b95cdd4d0ab0e1935781ab9a276ac0"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/${BPN}"
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://test-pango-gdk.c;endline=24;md5=1ee74ec851ecda57eb7ac6cc180f7655"

S = "${WORKDIR}/git/tests"

inherit autotools pkgconfig distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"
