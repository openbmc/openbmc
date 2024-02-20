SUMMARY = "Improved build system generator for Python C/C++/Fortran/Cython extensions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c96d2b08b3cec6d3c67fb864d1fd8cc"

DEPENDS = "${PYTHON_PN}-hatch-vcs-native ${PYTHON_PN}-hatch-fancy-pypi-readme-native"

PYPI_PACKAGE = "scikit_build"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "b51a51a36b37c42650994b5047912f59b22e3210b23e321f287611f9ef6e5c9d"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-distro \
	${PYTHON_PN}-packaging \
	${PYTHON_PN}-setuptools \
	${PYTHON_PN}-typing-extensions \
	${PYTHON_PN}-wheel \
	cmake \
"

BBCLASSEXTEND = "native nativesdk"
