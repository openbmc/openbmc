SUMMARY = "CMake is an open-source, cross-platform family of tools designed to build, test and package software"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = " \
	file://LICENSE_BSD_3;md5=9134cb61aebbdd79dd826ccb9ae6afcd \
	file://LICENSE_Apache_20;md5=19cbd64715b51267a47bf3750cc6a8a5 \
"

DEPENDS = "ninja-native cmake-native python3-scikit-build-native"

PYPI_PACKAGE = "cmake"
PYPI_ARCHIVE_NAME_PREFIX = "pypi-"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "4e0eb3c03dcf2d459f78d96cc85f7482476aeb1ae5ada65150b1db35c0f70cc7"

SRC_URI += " \
	file://CMakeLists.txt \
	file://run-cmake-from-path.patch \
"

addtask do_patchbuild after do_patch before do_configure

do_patchbuild () {
	rm -f ${S}/CMakeLists.txt
	cp ${WORKDIR}/CMakeLists.txt ${S}/
}

do_install:append () {
	rm -rf ${D}${bindir}
}

RDEPENDS:${PN} = " \
	cmake \
	python3-scikit-build \
"

BBCLASSEXTEND = "native nativesdk"
