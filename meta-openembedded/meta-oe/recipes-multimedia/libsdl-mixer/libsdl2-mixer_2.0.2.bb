SUMMARY = "Simple DirectMedia Layer mixer library V2"
SECTION = "libs"
DEPENDS = "virtual/libsdl2 flac libmikmod libvorbis"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=95e0c3cf63f71b950911e698a54b7fc5"

SRC_URI = "http://www.libsdl.org/projects/SDL_mixer/release/SDL2_mixer-${PV}.tar.gz"

SRC_URI[md5sum] = "aaa0551393993c14a13f72b339c0ed6c"
SRC_URI[sha256sum] = "4e615e27efca4f439df9af6aa2c6de84150d17cbfd12174b54868c12f19c83bb"

S = "${WORKDIR}/SDL2_mixer-${PV}"

inherit autotools-brokensep pkgconfig

EXTRA_AUTORECONF += "--include=acinclude"
EXTRA_OECONF = "--disable-music-mp3 --enable-music-ogg --enable-music-ogg-tremor LIBS=-L${STAGING_LIBDIR}"

PACKAGECONFIG[mad] = "--enable-music-mp3-mad-gpl,--disable-music-mp3-mad-gpl,libmad"

do_configure_prepend () {
    # Remove old libtool macros.
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f acinclude/$i
    done
}
