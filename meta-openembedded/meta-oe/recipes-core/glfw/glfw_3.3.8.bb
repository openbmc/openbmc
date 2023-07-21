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
SRCREV = "7482de6071d21db77a7236155da44c172a7f6c9e"
SRC_URI = "git://github.com/glfw/glfw.git;branch=3.3-stable;protocol=https"

S = "${WORKDIR}/git"

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON -DGLFW_BUILD_DOCS=OFF"

CFLAGS += "-fPIC"

DEPENDS = "libpng zlib"
REQUIRED_DISTRO_FEATURES = "opengl x11"
ANY_OF_DISTRO_FEATURES = "wayland x11"

# upstream considers x11 and wayland backends mutually exclusive and will
# prioritize wayland if it is enabled, but wayland has dependencies that cannot
# be satisfied by this layer so it is disabled by default

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[wayland] = "-DGLFW_USE_WAYLAND=ON,,wayland wayland-native wayland-protocols extra-cmake-modules libxkbcommon"
PACKAGECONFIG[x11] = ",,libxrandr libxinerama libxi libxcursor libglu"

COMPATIBLE_HOST:libc-musl = "null"
