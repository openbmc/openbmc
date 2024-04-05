SUMMARY = " C++ binding for Wayland using the most modern C++ technology"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3aae28cc66d61975114c2b14df215407"

SRC_URI = "git://github.com/NilsBrause/waylandpp.git;protocol=https;branch=master \
           file://0001-include-missing-cstdint.patch"

DEPENDS = "pugixml"
DEPENDS:append:class-target = " waylandpp-native wayland virtual/egl virtual/libgles2"

S = "${WORKDIR}/git"
SRCREV = "4321ed5c7b4bffa41b8a2a13dc7f3ece1191f4f3"

inherit cmake pkgconfig features_check

REQUIRED_DISTRO_FEATURES:class-target = "opengl"

EXTRA_OECMAKE:class-native = " \
	-DBUILD_SCANNER=ON \
	-DBUILD_LIBRARIES=OFF \
	-DBUILD_DOCUMENTATION=OFF \
	-DCMAKE_BUILD_TYPE=Release \
	-DCMAKE_VERBOSE_MAKEFILE=TRUE \
"

EXTRA_OECMAKE:class-target = " \
	-DBUILD_SCANNER=ON \
	-DBUILD_LIBRARIES=ON \
	-DBUILD_DOCUMENTATION=OFF \
	-DBUILD_EXAMPLES=OFF \
	-DOPENGL_LIBRARY="-lEGL -lGLESv2" \
	-DOPENGL_opengl_LIBRARY=-lEGL \
	-DOPENGL_glx_LIBRARY=-lEGL \
	-DWAYLAND_SCANNERPP="${STAGING_BINDIR_NATIVE}/wayland-scanner++" \
	-DCMAKE_BUILD_TYPE=Release \
	-DCMAKE_VERBOSE_MAKEFILE=TRUE \
	-DCMAKE_EXE_LINKER_FLAGS="-Wl,--enable-new-dtags" \
"

do_install:append:class-target() {
    sed -i -e 's|${S}||g' ${D}${libdir}/cmake/waylandpp/waylandpp-targets.cmake
    sed -i -e 's|${STAGING_DIR_HOST}||g' ${D}${libdir}/cmake/waylandpp/waylandpp-targets.cmake
}

BBCLASSEXTEND += "native nativesdk"
