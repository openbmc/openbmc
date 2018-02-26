SUMMARY = "H.264/MPEG-4 AVC video encoder"
DESCRIPTION = "A free software library and application for encoding video streams into the H.264/MPEG-4 AVC format."
HOMEPAGE = "http://www.videolan.org/developers/x264.html"

LICENSE = "GPLv2"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "yasm-native"

SRC_URI = "git://github.com/mirror/x264;branch=stable \
           file://don-t-default-to-cortex-a9-with-neon.patch \
           file://Fix-X32-build-by-disabling-asm.patch \
           "
UPSTREAM_VERSION_UNKNOWN = "1"

SRCREV = "2b741f81e51f92d053d87a49f59ff1026553a0f6"

PV = "r2731+git${SRCPV}"

S = "${WORKDIR}/git"

inherit lib_package pkgconfig perlnative

X264_DISABLE_ASM = ""
X264_DISABLE_ASM_armv4 = "--disable-asm"
X264_DISABLE_ASM_armv5 = "--disable-asm"
X264_DISABLE_ASM_powerpc = "${@bb.utils.contains("TUNE_FEATURES", "spe", "--disable-asm", "", d)}"
X264_DISABLE_ASM_mipsarch = "${@bb.utils.contains("TUNE_FEATURES", "r6", "", "--disable-asm", d)}"

EXTRA_OECONF = '--prefix=${prefix} \
                --host=${HOST_SYS} \
                --libdir=${libdir} \
                --cross-prefix=${TARGET_PREFIX} \
                --sysroot=${STAGING_DIR_TARGET} \
                --enable-shared \
                --enable-static \
                --disable-lavf \
                --disable-swscale \
                --disable-opencl \
                --enable-pic \
                ${X264_DISABLE_ASM} \
               '

do_configure() {
    ./configure ${EXTRA_OECONF}
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

AS[unexport] = "1"
