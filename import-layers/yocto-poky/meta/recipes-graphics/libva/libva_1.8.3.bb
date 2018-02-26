SUMMARY = "Video Acceleration (VA) API for Linux"
DESCRIPTION = "Video Acceleration API (VA API) is a library (libVA) \
and API specification which enables and provides access to graphics \
hardware (GPU) acceleration for video processing on Linux and UNIX \
based operating systems. Accelerated processing includes video \
decoding, video encoding, subpicture blending and rendering. The \
specification was originally designed by Intel for its GMA (Graphics \
Media Accelerator) series of GPU hardware, the API is however not \
limited to GPUs or Intel specific hardware, as other hardware and \
manufacturers can also freely use this API for hardware accelerated \
video decoding."

HOMEPAGE = "https://01.org/linuxmedia/vaapi"
BUGTRACKER = "https://github.com/01org/libva/issues"

SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2e48940f94acb0af582e5ef03537800f"

SRC_URI = "git://github.com/01org/libva.git;protocol=http;branch=v1.8-branch "
SRCREV = "457470987cc9df5976ce8c72ffd4bfbd9baaf0f9"
UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>(\d+(\.\d+)+))$"

S = "${WORKDIR}/git"

DEPENDS = "libdrm virtual/mesa virtual/libgles1 virtual/libgles2 virtual/egl"

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

EXTRA_OECONF = "ac_cv_prog_WAYLAND_SCANNER=wayland-scanner"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11 libxext libxfixes"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland-native wayland"

PACKAGES =+ "${PN}-x11 ${PN}-tpi ${PN}-glx ${PN}-egl ${PN}-wayland"

RDEPENDS_${PN}-tpi =+ "${PN}"
RDEPENDS_${PN}-x11 =+ "${PN}"
RDEPENDS_${PN}-glx =+ "${PN}-x11"
RDEPENDS_${PN}-egl =+ "${PN}-x11"

FILES_${PN}-dbg += "${libdir}/dri/.debug"

FILES_${PN}-x11 =+ "${libdir}/libva-x11*${SOLIBS}"
FILES_${PN}-tpi =+ "${libdir}/libva-tpi*${SOLIBS}"
FILES_${PN}-glx =+ "${libdir}/libva-glx*${SOLIBS}"
FILES_${PN}-egl =+ "${libdir}/libva-egl*${SOLIBS}"
FILES_${PN}-wayland =+ "${libdir}/libva-wayland*${SOLIBS}"
