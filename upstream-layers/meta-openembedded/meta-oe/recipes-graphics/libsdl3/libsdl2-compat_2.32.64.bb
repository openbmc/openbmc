SUMMARY = "Simple DirectMedia Layer (SDL) sdl2-compat"
DESCRIPTION = "This code is a compatibility layer; it provides a binary and source compatible \
API for programs written against SDL2, but it uses SDL3 behind the scenes. If you are \
writing new code, please target SDL3 directly and do not use this layer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca6eaff208365238403badb97aa6b3d2"

SRC_URI = "http://www.libsdl.org/release/sdl2-compat-${PV}.tar.gz"
SRC_URI[sha256sum] = "b2fa522e7bb08113534904b3dc2d2f7ff7b0c259224d86c12374d5b4a4027930"
S = "${UNPACKDIR}/sdl2-compat-${PV}"

DEPENDS += "libsdl3"

PROVIDES = "virtual/libsdl2"

inherit cmake pkgconfig upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "-DSDL2COMPAT_X11=ON,-DSDL2COMPAT_X11=OFF,virtual/libx11"

do_install:append() {
	mv ${D}${libdir}/pkgconfig/sdl2-compat.pc ${D}${libdir}/pkgconfig/sdl2.pc
}

FILES:${PN} += "${datadir}/licenses"

RCONFLICTS:${PN} = "libsdl2"
RPROVIDES:${PN} = "libsdl2"

BBCLASSEXTEND = "nativesdk native"
