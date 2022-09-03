SUMMARY = "Simple DirectMedia Layer truetype font library"
SECTION = "libs"
DEPENDS = "libsdl2 freetype virtual/egl"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=771dca8728b18d39b130e19b36514371"

SRC_URI = " \
    git://github.com/libsdl-org/SDL_ttf.git;branch=release-2.20.x;protocol=https \
    git://github.com/libsdl-org/freetype.git;branch=VER-2-12-1-SDL;destsuffix=git/external/freetype;name=freetype;protocol=https \
    git://github.com/libsdl-org/harfbuzz.git;branch=2.9.1-SDL;destsuffix=git/external/harfbuzz;name=harfbuzz;protocol=https \
    file://automake_foreign.patch \
"
SRCREV = "0a652b598625d16ea7016665095cb1e9bce9ef4f"
SRCREV_freetype = "6fc77cee03e078e97afcee0c0e06a2d3274b9a29"
SRCREV_harfbuzz = "6022fe2f68d028ee178284f297b3902ffdf65b03"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check

# links to libGL.so
REQUIRED_DISTRO_FEATURES += "opengl"

do_configure:prepend() {
    # Removing these files fixes a libtool version mismatch.
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"

    for i in ${MACROS}; do
        rm -f ${S}/acinclude/$i
    done
}
ASNEEDED = ""
