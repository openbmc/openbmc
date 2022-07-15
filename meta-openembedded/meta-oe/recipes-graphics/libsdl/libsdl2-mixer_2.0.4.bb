SUMMARY = "Simple DirectMedia Layer mixer library V2"
SECTION = "libs"
DEPENDS = "libsdl2 flac libmikmod libvorbis"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=516daf7a177ad4c8874bb9efa1a69c1f"

SRC_URI = "http://www.libsdl.org/projects/SDL_mixer/release/SDL2_mixer-${PV}.tar.gz \
           file://0001-configure.in-Undefine-AC_CONFIG_AUX_DIR.patch \
          "
SRC_URI[sha256sum] = "b4cf5a382c061cd75081cf246c2aa2f9df8db04bdda8dcdc6b6cca55bede2419"

S = "${WORKDIR}/SDL2_mixer-${PV}"

inherit autotools-brokensep pkgconfig

EXTRA_OECONF = "--disable-music-mp3 --enable-music-ogg --disable-music-ogg-shared LIBS=-L${STAGING_LIBDIR}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[mad] = "--enable-music-mp3-mad-gpl,--disable-music-mp3-mad-gpl,libmad"

do_configure:prepend () {
    # Remove old libtool macros.
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f acinclude/$i
    done
}
