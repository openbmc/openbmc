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
SRC_URI[sha256sum] = "27e845b1b7dc0a91a85f1a1f18892ed205adb38caf767741eb258008d8264de0"
S = "${UNPACKDIR}/sdl2-compat-${PV}"

DEPENDS += "libsdl3"

PROVIDES = "virtual/libsdl2"

inherit cmake pkgconfig upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

do_install:append() {
	mv ${D}${libdir}/pkgconfig/sdl2-compat.pc ${D}${libdir}/pkgconfig/sdl2.pc
}

FILES:${PN} += "${datadir}/licenses"

RCONFLICTS:${PN} = "libsdl2"
RPROVIDES:${PN} = "libsdl2"

BBCLASSEXTEND = "nativesdk native"
