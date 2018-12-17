SUMMARY = "C and C++ cross-platform library for decoding tracked music files (modules)"
DESCRIPTION = "libopenmpt is a cross-platform C++ and C library to decode tracked \
music files (modules) into a raw PCM audio stream. It also comes with openmpt123, a \
cross-platform command-line or terminal based module file player, and libopenmpt_modplug, \
a wrapper around libopenmpt that provides an interface that is ABI compatile with \
libmodplug. libopenmpt is based on the player code of the OpenMPT project."
HOMEPAGE = "https://lib.openmpt.org/libopenmpt/"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e804150573f149befef6c07e173f20c3"

DEPENDS = "virtual/libiconv"

SRC_URI = "https://lib.openmpt.org/files/libopenmpt/src/libopenmpt-${PV}+release.autotools.tar.gz \
           file://run-ptest \
          "
SRC_URI[md5sum] = "66bbc6fbb5f27a554cb145d805e9ef9d"
SRC_URI[sha256sum] = "14a137b8d1a20e1b6a5e67cbc9467ab7e5e4e67d5aa38a247afc825685c53939"

S = "${WORKDIR}/libopenmpt-${PV}+release.autotools"

inherit autotools pkgconfig ptest

PACKAGECONFIG ??= " \
    libopenmpt-modplug openmpt123 \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
    flac mpg123 ogg sndfile vorbis vorbisfile zlib \
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)} \
"

# libopenmpt_modplug is a library that wraps libopenmpt calls into
# functions that are ABI compatible with libmodplug. This allows for
# using modplug headers and linking against libopenmpt_modplug
# instead of against the original libmodplug library.
# NOTE: The wrapper is compatible to the ABI from libmodplug version
# 0.8.8 and newer.
PACKAGECONFIG[libopenmpt-modplug] = "--enable-libopenmpt_modplug,--disable-libopenmpt_modplug"
PACKAGECONFIG[openmpt123]         = "--enable-openmpt123,--disable-openmpt123"
PACKAGECONFIG[tests]              = "--enable-tests,--disable-tests"

# These packageconfigs affect openmpt123
PACKAGECONFIG[flac]               = "--with-flac,--without-flac,flac"
PACKAGECONFIG[mpg123]             = "--with-mpg123,--without-mpg123,mpg123"
PACKAGECONFIG[ogg]                = "--with-ogg,--without-ogg,libogg"
PACKAGECONFIG[portaudio]          = "--with-portaudio,--without-portaudio,portaudio-v19"
PACKAGECONFIG[pulseaudio]         = "--with-pulseaudio,--without-pulseaudio,pulseaudio"
PACKAGECONFIG[sdl]                = "--with-sdl,--without-sdl,virtual/libsdl"
PACKAGECONFIG[sdl2]               = "--with-sdl2,--without-sdl2,virtual/libsdl2"
PACKAGECONFIG[sndfile]            = "--with-sndfile,--without-sndfile,libsndfile1"
PACKAGECONFIG[vorbis]             = "--with-vorbis,--without-vorbis,libvorbis"
PACKAGECONFIG[vorbisfile]         = "--with-vorbisfile,--without-vorbisfile,libvorbis"
PACKAGECONFIG[zlib]               = "--with-zlib,--without-zlib,zlib"

# --disable-libmodplug is necessary, since otherwise we'd
# have a collision with the libmodplug package, because of the
# libmodplug.so file. (libmodplug.so from libopenmpt isintended
# to be used as a drop-in replacement, and according to the
# documentation, is not complete.)
EXTRA_OECONF += " \
    --disable-doxygen-doc \
    --disable-examples \
    --disable-libmodplug \
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

python __anonymous() {
    packageconfig = (d.getVar("PACKAGECONFIG") or "").split()
    if ("sdl" in packageconfig) and ("sdl2" in packageconfig):
        bb.error("sdl and sdl2 packageconfigs cannot be both enabled")
}

PACKAGES =+ "${PN}-modplug ${PN}-openmpt123 ${PN}-openmpt123-doc"
FILES_${PN}-modplug = "${libdir}/libopenmpt_modplug.so.*"
FILES_${PN}-openmpt123 = "${bindir}/openmpt123"
FILES_${PN}-openmpt123-doc = "${mandir}/man1/openmpt123*"

# Since version 0.3, libopenmpt uses SemVer 2.0.0 versioning.
# The SemVer versioning scheme is incompatible with Debian/Ubuntu
# package version names.
DEBIAN_NOAUTONAME_${PN} = "1"
DEBIAN_NOAUTONAME_${PN}-modplug = "1"

RDEPENDS_${PN}_libc-glibc = " \
    glibc-gconv-cp1252 \
    glibc-gconv-ibm437 \
    glibc-gconv-iso8859-1 \
    glibc-gconv-iso8859-15 \
"
