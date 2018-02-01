SUMMARY = "Simple DirectMedia Layer mixer library V2"
SECTION = "libs"
DEPENDS = "virtual/libsdl2 flac libmikmod libvorbis"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=29d8bc7c38aa44b1cf3a633a46589917"

SRC_URI = "http://www.libsdl.org/projects/SDL_mixer/release/SDL2_mixer-${PV}.tar.gz"

SRC_URI[md5sum] = "c6c4f556d4415871f526248f5c9a627d"
SRC_URI[sha256sum] = "5a24f62a610249d744cbd8d28ee399d8905db7222bf3bdbc8a8b4a76e597695f"

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
