SUMMARY = "VirGL virtual OpenGL renderer"
HOMEPAGE = "https://virgil3d.github.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c81c08eeefd9418fca8f88309a76db10"

DEPENDS = "libdrm mesa libepoxy"
SRCREV = "48cc96c9aebb9d0164830a157efc8916f08f00c0"
SRC_URI = "git://anongit.freedesktop.org/virglrenderer \
           file://0001-gallium-Expand-libc-check-to-be-platform-OS-check.patch \
           "

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

BBCLASSEXTEND = "native nativesdk"

REQUIRED_DISTRO_FEATURES = "opengl"
REQUIRED_DISTRO_FEATURES_class-native = ""
REQUIRED_DISTRO_FEATURES_class-nativesdk = ""
