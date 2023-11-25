SUMMARY = "Audio format Conversion library"
DESCRIPTION = "Library for reading and writing files containing sampled \
sound (such as MS Windows WAV and the Apple/SGI AIFF format) through \
one standard library interface."
HOMEPAGE = "https://libsndfile.github.io/libsndfile/"
DEPENDS = "flac libogg libvorbis"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.1-only"

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/libsndfile-${PV}.tar.xz \
           file://noopus.patch \
           file://cve-2022-33065.patch \
          "
GITHUB_BASE_URI = "https://github.com/libsndfile/libsndfile/releases/"

SRC_URI[sha256sum] = "3799ca9924d3125038880367bf1468e53a1b7e3686a934f098b7e1d286cdb80e"

LIC_FILES_CHKSUM = "file://COPYING;md5=e77fe93202736b47c07035910f47974a"

CVE_PRODUCT = "libsndfile"

S = "${WORKDIR}/libsndfile-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[regtest] = "--enable-sqlite,--disable-sqlite,sqlite3"

inherit autotools lib_package pkgconfig multilib_header github-releases

do_install:append() {
    oe_multilib_header sndfile.h
}
