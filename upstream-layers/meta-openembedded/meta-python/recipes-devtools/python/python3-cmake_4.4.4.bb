SUMMARY = "CMake is an open-source, cross-platform family of tools designed to build, test and package software"
LICENSE = "Apache-2.0 AND BSD-3-Clause"
LIC_FILES_CHKSUM = " \
	file://LICENSE_BSD_3;md5=9134cb61aebbdd79dd826ccb9ae6afcd \
	file://LICENSE_Apache_20;md5=19cbd64715b51267a47bf3750cc6a8a5 \
"

DEPENDS = "ninja-native cmake-native python3-scikit-build-native python3-scikit-build-core-native"

PYPI_ARCHIVE_NAME_PREFIX = "pypi-"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "f337d18e33b116cabae9a7a8ff423fc0bc3f389da0acefeb724e1397128dcc34"

SRC_URI += " \
	file://CMakeLists.txt \
	file://run-cmake-from-path.patch \
"

addtask do_patchbuild after do_patch before do_configure

do_patchbuild () {
	rm -f ${S}/CMakeLists.txt
	cp ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append () {
	rm -rf ${D}${bindir}
}

RDEPENDS:${PN} = " \
	cmake \
	python3-scikit-build \
	python3-scikit-build-core \
"

BBCLASSEXTEND = "native nativesdk"
