SUMMARY  = "A multi-platform library for OpenGL, OpenGL ES, Vulkan, window and input"
HOMEPAGE = "https://www.glfw.org/"
DESCRIPTION = "GLFW is an Open Source, multi-platform library for OpenGL, \
OpenGL ES and Vulkan application development. It provides a simple, \
platform-independent API for creating windows, contexts and surfaces, reading \
input, handling events, etc."
LICENSE  = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=98d93d1ddc537f9b9ea6def64e046b5f"
SECTION = "lib"

inherit pkgconfig cmake

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/glfw/glfw.git"
SRCREV = "d25248343e248337284dfbe5ecd1eddbd37ae66d"

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

DEPENDS = "libpng libglu zlib libxrandr libxinerama libxi libxcursor"
REQUIRED_DISTRO_FEATURES = "x11"

COMPATIBLE_HOST_libc-musl = "null"
