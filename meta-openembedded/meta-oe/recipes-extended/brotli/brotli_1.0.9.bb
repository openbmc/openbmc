SUMMARY = "Lossless compression library and tool"
DESCRIPTION = "Brotli is a generic-purpose lossless compression algorithm \
that it is similar in speed to deflate but offers more dense compression."
HOMEPAGE = "https://github.com/google/brotli"
BUGTRACKER = "https://github.com/google/brotli/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=941ee9cd1609382f946352712a319b4b"

SRC_URI = "git://github.com/google/brotli.git;branch=master;protocol=https \
           file://838.patch "
# tag 1.0.9
SRCREV= "e61745a6b7add50d380cfd7d3883dd6c62fc2c71"
S = "${WORKDIR}/git"

inherit cmake lib_package

do_install:append () {
	for lib in $(ls ${D}${libdir}/*-static.a); do
		basename=$(basename ${lib})
		mv -v "${lib}" "${D}${libdir}/$(echo ${basename} | sed s/-static//)"
	done
}

BBCLASSEXTEND = "native"
