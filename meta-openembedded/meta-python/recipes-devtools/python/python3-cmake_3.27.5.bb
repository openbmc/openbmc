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
SRC_URI[sha256sum] = "101d4e56154658c974e9425acd31f0fedde3ce68b47263fd14f789b028f8a13a"

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
