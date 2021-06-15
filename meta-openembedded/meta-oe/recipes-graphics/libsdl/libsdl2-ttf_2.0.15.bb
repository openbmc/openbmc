SUMMARY = "Simple DirectMedia Layer truetype font library"
SECTION = "libs"
DEPENDS = "virtual/libsdl2 freetype virtual/libgl"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=4bb27d550fdafcd8f8e4fb8cbb2775ef"

SRC_URI = " \
    http://www.libsdl.org/projects/SDL_ttf/release/SDL2_ttf-${PV}.tar.gz \
    file://automake_foreign.patch \
"
SRC_URI[md5sum] = "04fe06ff7623d7bdcb704e82f5f88391"
SRC_URI[sha256sum] = "a9eceb1ad88c1f1545cd7bd28e7cbc0b2c14191d40238f531a15b01b1b22cd33"

S = "${WORKDIR}/SDL2_ttf-${PV}"

inherit autotools pkgconfig features_check

# links to libGL.so
REQUIRED_DISTRO_FEATURES += "x11 opengl"

do_configure_prepend() {
    # Removing these files fixes a libtool version mismatch.
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"

    for i in ${MACROS}; do
        rm -f ${S}/acinclude/$i
    done
}
ASNEEDED = ""
