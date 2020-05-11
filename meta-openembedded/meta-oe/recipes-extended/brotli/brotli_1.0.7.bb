SUMMARY = "Lossless compression library and tool"
DESCRIPTIOM = "Brotli is a generic-purpose lossless compression algorithm \
that it is similar in speed to deflate but offers more dense compression."
HOMEPAGE = "https://github.com/google/brotli"
BUGTRACKER = "https://github.com/google/brotli/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=941ee9cd1609382f946352712a319b4b"

SRC_URI = "git://github.com/google/brotli.git"
# tag 1.0.7
SRCREV= "d6d98957ca8ccb1ef45922e978bb10efca0ea541"
S = "${WORKDIR}/git"

inherit cmake lib_package

do_install_append () {
	for lib in $(ls ${D}${libdir}/*-static.a); do
		mv -v "${lib}" "$(echo ${lib} | sed s/-static//)"
	done
}

BBCLASSEXTEND = "native"
