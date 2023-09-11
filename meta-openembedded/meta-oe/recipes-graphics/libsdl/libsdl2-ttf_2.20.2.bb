SUMMARY = "Simple DirectMedia Layer truetype font library"
SECTION = "libs"
DEPENDS = "libsdl2 freetype virtual/egl"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a41cbf59bdea749fe34c1af6d3615f68"

SRC_URI = " \
    git://github.com/libsdl-org/SDL_ttf.git;branch=release-2.20.x;protocol=https \
    git://github.com/libsdl-org/freetype.git;branch=VER-2-12-1-SDL;destsuffix=git/external/freetype;name=freetype;protocol=https \
    git://github.com/libsdl-org/harfbuzz.git;branch=2.9.1-SDL;destsuffix=git/external/harfbuzz;name=harfbuzz;protocol=https \
    file://0001-freetype-Fix-function-signatures-to-match-without-ca.patch;patchdir=external/harfbuzz \
    file://automake_foreign.patch \
"
SRCREV = "89d1692fd8fe91a679bb943d377bfbd709b52c23"
SRCREV_freetype = "6fc77cee03e078e97afcee0c0e06a2d3274b9a29"
SRCREV_harfbuzz = "43931e3e596c04044861770b831c8f9452e2d3b0"

SRCREV_FORMAT .= "_freetype_harfbuzz"

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
