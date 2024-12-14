LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/KhronosGroup/VK-GL-CTS.git;protocol=https;name=vk-gl-cts;nobranch=1 \
           file://0001-cmake-Define-WAYLAND_SCANNER-and-WAYLAND_PROTOCOLS_D.patch \
           file://0001-use-library-sonames-for-linking.patch \
           file://generate-srcuri.py \
           "

SRC_URI:append:libc-musl = "file://fix-musl.patch"
SRC_URI:append:toolchain-clang = "file://fix-clang-private-operator.patch"

SRCREV_FORMAT = "vk-gl-cts"

S = "${WORKDIR}/git"

inherit pkgconfig cmake cmake-qemu features_check python3native

UPSTREAM_CHECK_GITTAGREGEX = "${BPN}-(?P<pver>\d+(\.\d+)+)"

ANY_OF_DISTRO_FEATURES += "opengl vulkan"

DEPENDS += "python3-lxml-native libpng zlib virtual/libgles2 qemu-native"
DEPENDS:append:libc-musl = " libexecinfo"

EXTRA_OECMAKE += "-DAMBER_DISABLE_WERROR=ON \
                  -DUPDATE_DEPS_DIR=${S}/external/validation/ \
                  -DWAYLAND_SCANNER=${STAGING_BINDIR_NATIVE}/wayland-scanner \
                  -DWAYLAND_PROTOCOLS_DIR=${STAGING_DATADIR}/wayland-protocols"

# The best thing for the user to do is to not specify any of the following
# PACKAGECONFIGs (i.e. leave it blank) which tells the project to do its own
# probing and build what it thinks is appropriate.
# However, if you want, you can specify one of the following PACKAGECONFIGs
# to override this behaviour.
PACKAGECONFIG ??= ""
PACKAGECONFIG[surfaceless] = "-DDEQP_TARGET=surfaceless,,,,,wayland x11_egl x11_glx x11_egl_glx"
PACKAGECONFIG[wayland] = "-DDEQP_TARGET=wayland,,wayland-native wayland wayland-protocols,,,surfaceless x11_egl x11_glx x11_egl_glx"
PACKAGECONFIG[x11_egl] = "-DDEQP_TARGET=x11_egl,,virtual/libx11 virtual/egl,,,surfaceless wayland x11_glx x11_egl_glx"
PACKAGECONFIG[x11_glx] = "-DDEQP_TARGET=x11_glx,,virtual/libx11,,,surfaceless wayland x11_egl x11_egl_glx"
PACKAGECONFIG[x11_egl_glx] = "-DDEQP_TARGET=x11_glx,,virtual/libx11 virtual/egl,,,surfaceless wayland x11_egl x11_glx"

python __anonymous() {
    # if the user doesn't specify any PACKAGECONFIG then the cts build system
    # is going to probe the sysroot to try to figure out what to build
    # in this case we try to guess whether the user is building for wayland
    # or x11 and add the required dependencies automatically
    distrofeatures = (d.getVar("DISTRO_FEATURES") or "")
    if not bb.utils.contains_any("PACKAGECONFIG", ["surfaceless", "wayland", "x11_egl", "x11_glx", "x11_egl_glx"], True, False, d):
        if "wayland" in distrofeatures:
            d.appendVar("DEPENDS", " wayland-native ${MLPREFIX}wayland ${MLPREFIX}wayland-protocols")
        if "x11" in distrofeatures:
            d.appendVar("DEPENDS", " virtual/${MLPREFIX}libx11 virtual/${MLPREFIX}egl ")
}

CTSDIR = "/usr/lib/${BPN}"

FILES:${PN} += "${CTSDIR}"

# does not work with libc++-19 due
# error: implicit instantiation of undefined template 'std::char_traits<unsigned int>'
TOOLCHAIN = "gcc"

# Prototype task to refresh the generated SRC_URI entries by parsing
# the files in the source tree and writing a BPN-sources.inc file.
do_refresh_srcuri() {
    ${UNPACKDIR}/generate-srcuri.py ${PV} ${S}/external/ \
        > ${THISDIR}/${BPN}-sources.inc
    # Don't convert ${S}/external/vulkan-validationlayers/src/scripts/known_good.json as we
    # currently build without validation
}

addtask refresh_srcuri after do_patch
do_refresh_srcuri[network] = "1"
do_refresh_srcuri[nostamp] = "1"
