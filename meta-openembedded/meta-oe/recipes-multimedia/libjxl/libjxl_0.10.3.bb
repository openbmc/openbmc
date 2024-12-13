SUMMARY = "Reference implementation of JPEG XL (encoder and decoder)"
HOMEPAGE = "https://github.com/libjxl/libjxl/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a905a337cc228a1f68f0b5416f52a7f"

inherit cmake pkgconfig mime

DEPENDS = "highway brotli"

SRC_URI = "gitsm://github.com/libjxl/libjxl.git;protocol=https;nobranch=1 \
           file://0001-cmake-Do-not-use-mrelax-all-with-clang-on-RISCV64.patch \
           "

SRCREV = "4a3b22d2600f92d8706fb72d85d52bfee2acbd54"
S = "${WORKDIR}/git"

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

PACKAGECONFIG ?= "mime gdk-pixbuf-loader sizeless-vectors"
# libjxl/0.10.2/recipe-sysroot/usr/include/hwy/ops/rvv-inl.h:591:17: error: use
# of undeclared identifier '__riscv_vsetvlmax_e8mf8'
#  591 | HWY_RVV_FOREACH(HWY_RVV_LANES, Lanes, setvlmax_e, _ALL)
#      |                 ^
PACKAGECONFIG:remove:riscv64 = "sizeless-vectors"
PACKAGECONFIG:remove:riscv32 = "sizeless-vectors"
PACKAGECONFIG[gdk-pixbuf-loader] = "-DJPEGXL_ENABLE_PLUGIN_GDKPIXBUF=ON,-DJPEGXL_ENABLE_PLUGIN_GDKPIXBUF=OFF,gdk-pixbuf"
PACKAGECONFIG[gimp] = "-DJPEGXL_ENABLE_PLUGIN_GIMP210=ON,-DJPEGXL_ENABLE_PLUGIN_GIMP210=OFF,gimp"
PACKAGECONFIG[mime] = "-DJPEGXL_ENABLE_PLUGIN_MIME=ON,-DJPEGXL_ENABLE_PLUGIN_MIME=OFF"
PACKAGECONFIG[sizeless-vectors] = "-DJPEGXL_ENABLE_SIZELESS_VECTORS=ON,-DJPEGXL_ENABLE_SIZELESS_VECTORS=OFF"

FILES:${PN} += "${libdir}/gdk-pixbuf-2.0 ${datadir}"
