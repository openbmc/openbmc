SUMMARY = "Audio format Conversion library"
HOMEPAGE = "http://www.mega-nerd.com/libsndfile"
AUTHOR = "Erik de Castro Lopo"
DEPENDS = "flac libogg libvorbis sqlite3"
SECTION = "libs/multimedia"
LICENSE = "LGPLv2.1"

SRC_URI = "http://www.mega-nerd.com/libsndfile/files/libsndfile-${PV}.tar.gz \
           file://CVE-2017-6892.patch \
           file://CVE-2017-8361-8365.patch \
           file://CVE-2017-8362.patch \
           file://CVE-2017-8363.patch \
          "

SRC_URI[md5sum] = "646b5f98ce89ac60cdb060fcd398247c"
SRC_URI[sha256sum] = "1ff33929f042fa333aed1e8923aa628c3ee9e1eb85512686c55092d1e5a9dfa9"

LIC_FILES_CHKSUM = "file://COPYING;md5=e77fe93202736b47c07035910f47974a"

CVE_PRODUCT = "libsndfile"

S = "${WORKDIR}/libsndfile-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"

inherit autotools lib_package pkgconfig

do_configure_prepend_arm() {
	export ac_cv_sys_largefile_source=1
	export ac_cv_sys_file_offset_bits=64
}

