SUMMARY = "OpenGL frontend to Freetype 2"
HOMEPAGE = "https://sourceforge.net/projects/ftgl/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=527a83e92c7bf363025380eec05df6e4"

inherit autotools pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

DEPENDS += " \
    freetype \
    freeglut \
"
SRCREV = "b787042cc1d1e4861337d5f9a6727e4c6900a4f2"
PV .= "+git"

SRC_URI = "git://github.com/HamzaM3/ftgl;protocol=https;branch=master \
           file://0001-Fix-type-mismatch-with-latest-FreeType.patch"

S = "${WORKDIR}/git"
