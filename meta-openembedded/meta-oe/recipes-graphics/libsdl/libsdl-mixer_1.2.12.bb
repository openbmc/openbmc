SUMMARY = "Simple DirectMedia Layer mixer library"
SECTION = "libs"
DEPENDS = "libsdl flac libmikmod libvorbis"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING;md5=a37a47a0e579e461474cd03b9e05199d"

SRC_URI = "http://www.libsdl.org/projects/SDL_mixer/release/SDL_mixer-${PV}.tar.gz \
           file://configure.patch \
"

SRC_URI[sha256sum] = "1644308279a975799049e4826af2cfc787cad2abb11aa14562e402521f86992a"

S = "${WORKDIR}/SDL_mixer-${PV}"

inherit autotools-brokensep pkgconfig

EXTRA_AUTORECONF += "--include=acinclude"
EXTRA_OECONF = "--disable-music-mp3 --enable-music-ogg --enable-music-ogg-tremor LIBS=-L${STAGING_LIBDIR}"

PACKAGECONFIG[mad] = "--enable-music-mp3-mad-gpl,--disable-music-mp3-mad-gpl,libmad"

do_configure () {
    # Remove old libtool macros.
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f acinclude/$i
    done
    cp build-scripts/* . || true
    rm -rf build-scripts/
    export SYSROOT=$PKG_CONFIG_SYSROOT_DIR

    autotools_do_configure

    rm config.log
    for i in $(find -name "Makefile") ; do
        sed -i -e 's:-L/usr/lib:-L${STAGING_LIBDIR}:g' $i
    done
}
