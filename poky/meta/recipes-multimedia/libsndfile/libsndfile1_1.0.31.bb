SUMMARY = "Audio format Conversion library"
DESCRIPTION = "Library for reading and writing files containing sampled \
sound (such as MS Windows WAV and the Apple/SGI AIFF format) through \
one standard library interface."
HOMEPAGE = "https://libsndfile.github.io/libsndfile/"
AUTHOR = "Erik de Castro Lopo"
DEPENDS = "flac libogg libvorbis"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1-only"

SRC_URI = "https://github.com/libsndfile/libsndfile/releases/download/${PV}/libsndfile-${PV}.tar.bz2 \
           file://noopus.patch \
           file://0001-flac-Fix-improper-buffer-reusing-732.patch \
          "
UPSTREAM_CHECK_URI = "https://github.com/libsndfile/libsndfile/releases/"

SRC_URI[md5sum] = "3f3b2a86a032f064ef922a2c8c191f7b"
SRC_URI[sha256sum] = "a8cfb1c09ea6e90eff4ca87322d4168cdbe5035cb48717b40bf77e751cc02163"

LIC_FILES_CHKSUM = "file://COPYING;md5=e77fe93202736b47c07035910f47974a"

CVE_PRODUCT = "libsndfile"

S = "${WORKDIR}/libsndfile-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[regtest] = "--enable-sqlite,--disable-sqlite,sqlite3"

inherit autotools lib_package pkgconfig multilib_header

do_install:append() {
    oe_multilib_header sndfile.h
}
