SUMMARY = "Audio format Conversion library"
DESCRIPTION = "Library for reading and writing files containing sampled \
sound (such as MS Windows WAV and the Apple/SGI AIFF format) through \
one standard library interface."
HOMEPAGE = "http://www.mega-nerd.com/libsndfile"
AUTHOR = "Erik de Castro Lopo"
DEPENDS = "flac libogg libvorbis"
SECTION = "libs/multimedia"
LICENSE = "LGPLv2.1"

SRC_URI = "http://www.mega-nerd.com/libsndfile/files/libsndfile-${PV}.tar.gz \
           file://CVE-2017-6892.patch \
           file://CVE-2017-8361-8365.patch \
           file://CVE-2017-8362.patch \
           file://CVE-2017-8363.patch \
           file://CVE-2017-14634.patch \
           file://CVE-2018-13139.patch \
           file://0001-a-ulaw-fix-multiple-buffer-overflows-432.patch \
           file://CVE-2018-19432.patch \
           file://CVE-2017-12562.patch \
           file://CVE-2018-19758.patch \
           file://CVE-2019-3832.patch \
          "

SRC_URI[md5sum] = "646b5f98ce89ac60cdb060fcd398247c"
SRC_URI[sha256sum] = "1ff33929f042fa333aed1e8923aa628c3ee9e1eb85512686c55092d1e5a9dfa9"

LIC_FILES_CHKSUM = "file://COPYING;md5=e77fe93202736b47c07035910f47974a"

CVE_PRODUCT = "libsndfile"

S = "${WORKDIR}/libsndfile-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[regtest] = "--enable-sqlite,--disable-sqlite,sqlite3"

inherit autotools lib_package pkgconfig multilib_header

do_install_append() {
    oe_multilib_header sndfile.h
}

# This can't be replicated and is just a memory leak.
# https://github.com/erikd/libsndfile/issues/398
CVE_CHECK_WHITELIST += "CVE-2018-13419"
