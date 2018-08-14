SUMMARY = "Multitouch viewer"
DESCRIPTION = "mtview is a small X application that shows a graphical view of your MT-enabled hardware. It uses mtdev."
HOMEPAGE = "http://bitmath.org/code/mtview/"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c61b8950dc1aab4d2aa7c2ae6b1cfb3"

inherit autotools pkgconfig distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://bitmath.org/git/mtview.git;protocol=http"
SRCREV = "ad437c38dc111cf3990a03abf14efe1b5d89604b"

DEPENDS += "mtdev utouch-frame utouch-evemu libx11"

PV = "1.1.7+git${SRCPV}"

S = "${WORKDIR}/git/"
