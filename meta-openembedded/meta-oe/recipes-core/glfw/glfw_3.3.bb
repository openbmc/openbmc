SUMMARY  = "A multi-platform library for OpenGL, OpenGL ES, Vulkan, window and input"
HOMEPAGE = "https://www.glfw.org/"
DESCRIPTION = "GLFW is an Open Source, multi-platform library for OpenGL, \
OpenGL ES and Vulkan application development. It provides a simple, \
platform-independent API for creating windows, contexts and surfaces, reading \
input, handling events, etc."
LICENSE  = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=98d93d1ddc537f9b9ea6def64e046b5f"
SECTION = "lib"

inherit pkgconfig cmake features_check

PV .= "+git${SRCPV}"
SRCREV = "781fbbadb0bccc749058177b1385c82da9ace880"
SRC_URI = "git://github.com/glfw/glfw.git"

S = "${WORKDIR}/git"

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON -DGLFW_BUILD_DOCS=OFF"

CFLAGS += "-fPIC"

DEPENDS = "libpng libglu zlib libxrandr libxinerama libxi libxcursor"
REQUIRED_DISTRO_FEATURES = "x11 opengl"

COMPATIBLE_HOST_libc-musl = "null"
