SUMMARY = "C and C++ cross-platform library for decoding tracked music files (modules)"
DESCRIPTION = "libopenmpt is a cross-platform C++ and C library to decode tracked \
music files (modules) into a raw PCM audio stream. It also comes with openmpt123, a \
cross-platform command-line or terminal based module file player. libopenmpt is based \
on the player code of the OpenMPT project."
HOMEPAGE = "https://lib.openmpt.org/libopenmpt/"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=829270a66f67187a1d08640df673249a"

DEPENDS = "virtual/libiconv"

SRC_URI = "https://lib.openmpt.org/files/libopenmpt/src/libopenmpt-${PV}+release.autotools.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "093713c1c1024f4f10c4779a66ceb2af51fb7c908a9e99feb892d04019220ba1"

S = "${WORKDIR}/libopenmpt-${PV}+release.autotools"

inherit autotools pkgconfig ptest

PACKAGECONFIG ??= " \
    openmpt123 \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
    flac mpg123 ogg sndfile vorbis vorbisfile zlib \
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)} \
"

PACKAGECONFIG[openmpt123]         = "--enable-openmpt123,--disable-openmpt123"
PACKAGECONFIG[tests]              = "--enable-tests,--disable-tests"

# These packageconfigs affect openmpt123
PACKAGECONFIG[flac]               = "--with-flac,--without-flac,flac"
PACKAGECONFIG[mpg123]             = "--with-mpg123,--without-mpg123,mpg123"
PACKAGECONFIG[ogg]                = "--with-ogg,--without-ogg,libogg"
PACKAGECONFIG[portaudio]          = "--with-portaudio,--without-portaudio,portaudio-v19"
PACKAGECONFIG[pulseaudio]         = "--with-pulseaudio,--without-pulseaudio,pulseaudio"
PACKAGECONFIG[sdl2]               = "--with-sdl2,--without-sdl2,libsdl2"
PACKAGECONFIG[sndfile]            = "--with-sndfile,--without-sndfile,libsndfile1"
PACKAGECONFIG[vorbis]             = "--with-vorbis,--without-vorbis,libvorbis"
PACKAGECONFIG[vorbisfile]         = "--with-vorbisfile,--without-vorbisfile,libvorbis"
PACKAGECONFIG[zlib]               = "--with-zlib,--without-zlib,zlib"

EXTRA_OECONF += " \
    --disable-doxygen-doc \
    --disable-examples \
    --without-portaudiocpp \
"

do_compile_ptest() {
    oe_runmake ${PARALLEL_MAKE} libopenmpttest
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    install -m 0755 ${B}/libopenmpttest ${D}${PTEST_PATH}
    install -m 0644 ${S}/test/test.mptm ${D}${PTEST_PATH}/test
    install -m 0644 ${S}/test/test.s3m ${D}${PTEST_PATH}/test
    install -m 0644 ${S}/test/test.xm ${D}${PTEST_PATH}/test

    install -d ${D}${PTEST_PATH}/libopenmpt
    install -m 0644 ${S}/libopenmpt/libopenmpt_version.mk ${D}${PTEST_PATH}/libopenmpt
}

PACKAGES =+ "${PN}-openmpt123 ${PN}-openmpt123-doc"
FILES:${PN}-openmpt123 = "${bindir}/openmpt123"
FILES:${PN}-openmpt123-doc = "${mandir}/man1/openmpt123*"

# Since version 0.3, libopenmpt uses SemVer 2.0.0 versioning.
# The SemVer versioning scheme is incompatible with Debian/Ubuntu
# package version names.
DEBIAN_NOAUTONAME:${PN} = "1"

RDEPENDS:${PN}:libc-glibc = " \
    glibc-gconv \
    glibc-gconv-cp1252 \
    glibc-gconv-ibm437 \
    glibc-gconv-iso8859-1 \
    glibc-gconv-iso8859-15 \
"
