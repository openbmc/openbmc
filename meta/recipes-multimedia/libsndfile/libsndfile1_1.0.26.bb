SUMMARY = "Audio format Conversion library"
HOMEPAGE = "http://www.mega-nerd.com/libsndfile"
AUTHOR = "Erik de Castro Lopo"
DEPENDS = "flac libogg libvorbis sqlite3"
SECTION = "libs/multimedia"
LICENSE = "LGPLv2.1"

SRC_URI = "http://www.mega-nerd.com/libsndfile/files/libsndfile-${PV}.tar.gz"

SRC_URI[md5sum] = "ec810a0c60c08772a8a5552704b63393"
SRC_URI[sha256sum] = "cd6520ec763d1a45573885ecb1f8e4e42505ac12180268482a44b28484a25092"

LIC_FILES_CHKSUM = "file://COPYING;md5=e77fe93202736b47c07035910f47974a"

S = "${WORKDIR}/libsndfile-${PV}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"

inherit autotools lib_package pkgconfig

do_configure_prepend_arm() {
	export ac_cv_sys_largefile_source=1
	export ac_cv_sys_file_offset_bits=64
}

