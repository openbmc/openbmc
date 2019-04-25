SUMMARY = "VirGL virtual OpenGL renderer"
HOMEPAGE = "https://virgil3d.github.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c81c08eeefd9418fca8f88309a76db10"

DEPENDS = "libdrm mesa libepoxy"
SRCREV = "402c228861c9893f64cffbbcb4cb23044b8c721c"
SRC_URI = "git://anongit.freedesktop.org/virglrenderer \
           file://0001-vtest-add-missing-includes.patch \
           file://0001-Makefile.am-explicitly-link-with-libdrm.patch \
           "

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

BBCLASSEXTEND = "native nativesdk"

REQUIRED_DISTRO_FEATURES = "opengl"
REQUIRED_DISTRO_FEATURES_class-native = ""
REQUIRED_DISTRO_FEATURES_class-nativesdk = ""
