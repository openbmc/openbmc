SUMMARY = "VirGL virtual OpenGL renderer"
DESCRIPTION = "Virgil is a research project to investigate the possibility of \
creating a virtual 3D GPU for use inside qemu virtual machines, that allows \
the guest operating system to use the capabilities of the host GPU to \
accelerate 3D rendering."
HOMEPAGE = "https://virgil3d.github.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c81c08eeefd9418fca8f88309a76db10"

DEPENDS = "libdrm libepoxy virtual/egl virtual/libgbm"
SRCREV = "690680e5f0f952e22424fca1538c1b24457a0868"
SRC_URI = "git://gitlab.freedesktop.org/virgl/virglrenderer.git;branch=main;protocol=https \
           file://0001-meson.build-use-python3-directly-for-python.patch \
           file://0001-vrend-Fix-int-conversion-fatal-build-error-with-GCC-.patch \
           "

S = "${WORKDIR}/git"

inherit meson pkgconfig features_check

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'vulkan', 'venus', '', d)}"

PACKAGECONFIG[venus] = "-Dvenus=true,-Dvenus=false,vulkan-loader vulkan-headers"
PACKAGECONFIG[va] = "-Dvideo=true,-Dvideo=false,libva"
PACKAGECONFIG[render-server] = "-Drender-server=true,-Drender-server=false"
PACKAGECONFIG[drm-msm-experimental] = "-Ddrm-msm-experimental=true,-Ddrm-msm-experimental=false"
PACKAGECONFIG[minigbm_allocation] = "-Dminigbm_allocation=true,-Dminigbm_allocation=false"
PACKAGECONFIG[venus-validate] = "-Dvenus-validate=true,-Dvenus-validate=false"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,libcheck"

BBCLASSEXTEND = "native nativesdk"

REQUIRED_DISTRO_FEATURES = "opengl"
