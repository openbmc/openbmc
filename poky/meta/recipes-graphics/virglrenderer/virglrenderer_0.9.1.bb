SUMMARY = "VirGL virtual OpenGL renderer"
DESCRIPTION = "Virgil is a research project to investigate the possibility of \
creating a virtual 3D GPU for use inside qemu virtual machines, that allows \
the guest operating system to use the capabilities of the host GPU to \
accelerate 3D rendering."
HOMEPAGE = "https://virgil3d.github.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c81c08eeefd9418fca8f88309a76db10"

DEPENDS = "libdrm virtual/egl virtual/libgbm libepoxy"
SRCREV = "363915595e05fb252e70d6514be2f0c0b5ca312b"
SRC_URI = "git://anongit.freedesktop.org/git/virglrenderer;branch=branch-0.9.1 \
           file://0001-meson.build-use-python3-directly-for-python.patch \
           file://cve-2022-0135.patch \
           file://cve-2022-0175.patch \
           "

S = "${WORKDIR}/git"

inherit meson pkgconfig features_check

BBCLASSEXTEND = "native nativesdk"

REQUIRED_DISTRO_FEATURES = "opengl"
