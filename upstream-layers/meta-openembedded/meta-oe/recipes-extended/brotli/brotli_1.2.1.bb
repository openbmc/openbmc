SUMMARY = "Lossless compression library and tool"
DESCRIPTION = "Brotli is a generic-purpose lossless compression algorithm \
that it is similar in speed to deflate but offers more dense compression."
HOMEPAGE = "https://github.com/google/brotli"
BUGTRACKER = "https://github.com/google/brotli/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=941ee9cd1609382f946352712a319b4b"

SRC_URI = "git://github.com/google/brotli.git;branch=v${@oe.utils.trim_version('${PV}')};protocol=https;tag=v${PV}"
SRCREV = "db24a98aed38a16de78cc05ca15cdd8dc2a0d92d"


inherit cmake lib_package

do_install:append () {
	for lib in $(ls ${D}${libdir}/*-static.a); do
		basename=$(basename ${lib})
		mv -v "${lib}" "${D}${libdir}/$(echo ${basename} | sed s/-static//)"
	done
}

BBCLASSEXTEND = "native nativesdk"
