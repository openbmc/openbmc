SUMMARY = "Reference implementation of JPEG XL (encoder and decoder)"
HOMEPAGE = "https://github.com/libjxl/libjxl/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a905a337cc228a1f68f0b5416f52a7f"

inherit cmake pkgconfig mime

DEPENDS = "highway brotli"

SRC_URI = "gitsm://github.com/libjxl/libjxl.git;protocol=https;nobranch=1;tag=v${PV} \
           "

SRCREV = "a7a9c787341cf703dede03c2009fa460cae5e5df"

EXTRA_OECMAKE = " \
	-DCMAKE_BUILD_TYPE=Release \
	-DJPEGXL_ENABLE_PLUGINS=ON \
	-DBUILD_TESTING=OFF \
	-DJPEGXL_WARNINGS_AS_ERRORS=OFF \
	-DJPEGXL_ENABLE_SJPEG=OFF \
	-DJPEGXL_ENABLE_BENCHMARK=OFF \
	-DJPEGXL_ENABLE_EXAMPLES=OFF \
	-DJPEGXL_ENABLE_MANPAGES=OFF \
	-DJPEGXL_ENABLE_SKCMS=ON \
	-DJPEGXL_FORCE_SYSTEM_BROTLI=ON \
	-DJPEGXL_FORCE_SYSTEM_HWY=ON \
	-DJPEGXL_ENABLE_JNI=OFF \
	-DJPEGXL_ENABLE_TCMALLOC=OFF \
	-DJPEGXL_ENABLE_TOOLS=OFF \
"

# Whether to build the SVE targets is decided by a compiler capability probe
# using __attribute__((target("+sve"))), which succeeds on any aarch64 compiler
# no matter what the tune supports. On a tune without SVE that compiles code
# which can never run, and gcc crashes on the SVE2_128 target:
#   enc_transforms-inl.h:799:1: internal compiler error:
#   in simplify_gen_subreg_concatn, at lower-subreg.cc:744
# Tie the targets to the tune instead.
EXTRA_OECMAKE:append:aarch64 = " ${@bb.utils.contains('TUNE_FEATURES', 'sve', '', '-DJPEGXL_ENABLE_HWY_SVE=OFF -DJPEGXL_ENABLE_HWY_SVE_256=OFF', d)}"
EXTRA_OECMAKE:append:aarch64 = " ${@bb.utils.contains('TUNE_FEATURES', 'sve2', '', '-DJPEGXL_ENABLE_HWY_SVE2=OFF -DJPEGXL_ENABLE_HWY_SVE2_128=OFF', d)}"

PACKAGECONFIG ?= "mime gdk-pixbuf-loader"
PACKAGECONFIG[gdk-pixbuf-loader] = "-DJPEGXL_ENABLE_PLUGIN_GDKPIXBUF=ON,-DJPEGXL_ENABLE_PLUGIN_GDKPIXBUF=OFF,gdk-pixbuf"
PACKAGECONFIG[gimp] = "-DJPEGXL_ENABLE_PLUGIN_GIMP210=ON,-DJPEGXL_ENABLE_PLUGIN_GIMP210=OFF,gimp"
PACKAGECONFIG[mime] = "-DJPEGXL_ENABLE_PLUGIN_MIME=ON,-DJPEGXL_ENABLE_PLUGIN_MIME=OFF"

# lcms/src/cmsps2.c
# error: out of range pc-relative fixup value
CFLAGS:append:toolchain-clang:arm = " -fno-integrated-as"
# lib/jxl/convolve_separable5.cc
# error: out of range pc-relative fixup value
CXXFLAGS:append:toolchain-clang:arm = " -fno-integrated-as"

FILES:${PN} += "${libdir}/gdk-pixbuf-2.0 ${datadir}"

BBCLASSEXTEND = "native"

CVE_STATUS[CVE-2025-12474] = "fixed-version: fixed in v0.11.2"
CVE_STATUS[CVE-2026-1837] = "fixed-version: fixed in v0.11.2"
