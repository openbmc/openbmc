SUMMARY = "FFmpeg derived postprocessing library"
HOMEPAGE = "http://git.videolan.org/?p=libpostproc.git;a=summary"
SECTION = "libs"
DEPENDS = "libav"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# because it depends on libav which has commercial flag
LICENSE_FLAGS = "commercial"

PV = "52.3.0+git${SRCPV}"

SRCREV = "811db3b957dfde24aef2d0f82e297e5bf552d873"
SRC_URI = "git://github.com/lu-zero/postproc;protocol=https"

S = "${WORKDIR}/git"

inherit autotools lib_package pkgconfig

FULL_OPTIMIZATION_armv7a = "-fexpensive-optimizations -fomit-frame-pointer -O4 -ffast-math"
BUILD_OPTIMIZATION = "${FULL_OPTIMIZATION}"

EXTRA_FFCONF_armv7a = "--cpu=cortex-a8"
EXTRA_FFCONF ?= ""

EXTRA_OECONF = " \
    --enable-shared \
    --enable-pthreads \
    --enable-gpl \
    --enable-postproc \
    \
    --cross-prefix=${TARGET_PREFIX} \
    --prefix=${prefix} \
    \
    --arch=${TARGET_ARCH} \
    --target-os="linux" \
    --enable-cross-compile \
    --extra-cflags="${TARGET_CFLAGS} ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}" \
    --extra-ldflags="${TARGET_LDFLAGS}" \
    --sysroot="${STAGING_DIR_TARGET}" \
    --shlibdir="${libdir}" \
    ${EXTRA_FFCONF} \
"

do_configure() {
    ${S}/configure ${EXTRA_OECONF}
}
