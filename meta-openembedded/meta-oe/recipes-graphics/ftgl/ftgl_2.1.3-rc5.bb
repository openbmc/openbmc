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

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
    file://0001-use-pkg-config-to-find-freetype2.patch \
    file://0002-Makefile.am-remove-useless-and-breaking-code.patch \
    file://0001-Explicit-typecast-to-avoid-implicit-double-to-float-.patch \
"
SRC_URI[md5sum] = "c7879018cde844059495b3029b0b6503"
SRC_URI[sha256sum] = "521ff7bd62c459ff5372e269c223e2a6107a6a99a36afdc2ae634a973af70c59"

S = "${WORKDIR}/ftgl-2.1.3~rc5"

# undefined reference to symbol 'sin@@GLIBC_2.4'
CFLAGS += "-lm"
