SUMMARY = "Simple DirectMedia Layer (SDL) sdl2-compat"
DESCRIPTION = "This code is a compatibility layer; it provides a binary and source compatible \
API for programs written against SDL2, but it uses SDL3 behind the scenes. If you are \
writing new code, please target SDL3 directly and do not use this layer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=98241180d857fe975e4b60d44d6c01a5"

SRC_URI = "http://www.libsdl.org/release/sdl2-compat-${PV}.tar.gz"
SRC_URI[sha256sum] = "eb02c4c47d90e7a2585a65c712cf4a08ff4c37c0a1efc17af49d8ebde3292c23"
S = "${WORKDIR}/sdl2-compat-${PV}"

DEPENDS += "libsdl3"

inherit cmake pkgconfig upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

FILES:${PN} += "${datadir}/licenses"
