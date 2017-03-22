SUMMARY = "Audio format Conversion library"
HOMEPAGE = "http://www.mega-nerd.com/libsndfile"
AUTHOR = "Erik de Castro Lopo"
DEPENDS = "flac libogg libvorbis sqlite3"
SECTION = "libs/multimedia"
LICENSE = "LGPLv2.1"

SRC_URI = "http://www.mega-nerd.com/libsndfile/files/libsndfile-${PV}.tar.gz"

SRC_URI[md5sum] = "fd1d97c6077f03b5d984d7956ffedb7a"
SRC_URI[sha256sum] = "a391952f27f4a92ceb2b4c06493ac107896ed6c76be9a613a4731f076d30fac0"

LIC_FILES_CHKSUM = "file://COPYING;md5=e77fe93202736b47c07035910f47974a"

S = "${WORKDIR}/libsndfile-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"

inherit autotools lib_package pkgconfig

do_configure_prepend_arm() {
	export ac_cv_sys_largefile_source=1
	export ac_cv_sys_file_offset_bits=64
}

