SUMMARY = "Improved build system generator for Python C/C++/Fortran/Cython extensions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c96d2b08b3cec6d3c67fb864d1fd8cc"

DEPENDS = "python3-hatch-vcs-native python3-hatch-fancy-pypi-readme-native"

PYPI_PACKAGE = "scikit_build"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "46e1b2d71343d14e4c07d7e60902e673c78defb9a2c282b70ad80fb8502ade2e"

RDEPENDS:${PN} = " \
	python3-distro \
	python3-packaging \
	python3-setuptools \
	python3-typing-extensions \
	python3-wheel \
	cmake \
"

BBCLASSEXTEND = "native nativesdk"
