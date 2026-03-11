SUMMARY = "Simple DirectMedia Layer mixer library V2"
SECTION = "libs"
DEPENDS = "virtual/libsdl2"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=fbb0010b2f7cf6e8a13bcac1ef4d2455"

SRC_URI = "http://www.libsdl.org/projects/SDL_mixer/release/SDL2_mixer-${PV}.tar.gz"
SRC_URI[sha256sum] = "cb760211b056bfe44f4a1e180cc7cb201137e4d1572f2002cc1be728efd22660"

S = "${UNPACKDIR}/SDL2_mixer-${PV}"

inherit cmake pkgconfig

do_configure:prepend() {
	# cmake checks for these binaries. Touch them to pass the tests and add RDEPENDS
	touch ${STAGING_BINDIR}/fluidsynth
	touch ${STAGING_BINDIR}/wavpack
	touch ${STAGING_BINDIR}/wvunpack
	touch ${STAGING_BINDIR}/wvgain
	touch ${STAGING_BINDIR}/wvtag
}

PACKAGECONFIG ?= "flac wave vorbis"
PACKAGECONFIG[opusfile] = "-DSDL2MIXER_OPUS=ON -DSDL2MIXER_OPUS_ENABLED=ON, -DSDL2MIXER_OPUS=OFF,opusfile"
PACKAGECONFIG[vorbis] = "-DSDL2MIXER_VORBIS=VORBISFILE -DSDL2MIXER_VORBIS_VORBISFILE=ON, -DSDL2MIXER_VORBIS=OFF,libvorbis"
PACKAGECONFIG[flac] = "-DSDL2MIXER_FLAC=ON, -DSDL2MIXER_FLAC=OFF,flac"
PACKAGECONFIG[xmp] = "-DSDL2MIXER_MOD=ON -DSDL2MIXER_MOD_XMP=ON, -DSDL2MIXER_MOD=OFF,libxmp,libxmp"
PACKAGECONFIG[fluidsynth] = "-DSDL2MIXER_MIDI=ON -DSDL2MIXER_MIDI_FLUIDSYNTH_ENABLED=ON, -DSDL2MIXER_MIDI=OFF,fluidsynth,fluidsynth-bin"
PACKAGECONFIG[wave] = "-DSDL2MIXER_WAVE=ON -DSDL2MIXER_WAVPACK=ON, -DSDL2MIXER_WAVE=OFF,wavpack,wavpack wavpack-bin"
PACKAGECONFIG[mpg123] = "-DSDL2MIXER_MP3=ON -DSDL2MIXER_MP3_MPG123=ON, -DSDL2MIXER_MP3=OFF,mpg123"

FILES:${PN} += "${datadir}/licenses"

