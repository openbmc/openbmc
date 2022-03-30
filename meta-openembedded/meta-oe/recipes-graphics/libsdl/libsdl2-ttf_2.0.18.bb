SUMMARY = "Simple DirectMedia Layer truetype font library"
SECTION = "libs"
DEPENDS = "virtual/libsdl2 freetype virtual/egl"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e98cfd01ca78f683e9d035795810ce87"

SRC_URI = "http://www.libsdl.org/projects/SDL_ttf/release/SDL2_ttf-${PV}.tar.gz \
           file://automake_foreign.patch \
           "
SRC_URI[sha256sum] = "7234eb8883514e019e7747c703e4a774575b18d435c22a4a29d068cb768a2251"

S = "${WORKDIR}/SDL2_ttf-${PV}"

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
