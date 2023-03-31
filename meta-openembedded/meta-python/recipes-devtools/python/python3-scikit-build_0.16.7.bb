SUMMARY = "Improved build system generator for Python C/C++/Fortran/Cython extensions"
LICENSE = "MIT & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c96d2b08b3cec6d3c67fb864d1fd8cc"

DEPENDS = "python3-setuptools-scm-native"

PYPI_PACKAGE = "scikit-build"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "a9b9cc7479b71e6c8d434596dfade025253aae23adb22a9a2d85850fd51cecfd"

RDEPENDS:${PN} = " \
	python3-distro \
	python3-packaging \
	python3-setuptools \
	python3-typing-extensions \
	python3-wheel \
	cmake \
"

BBCLASSEXTEND = "native nativesdk"
