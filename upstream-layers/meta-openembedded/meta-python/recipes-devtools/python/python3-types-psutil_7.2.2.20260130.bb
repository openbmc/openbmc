SUMMARY = "Typing stubs for psutil"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ec038232ab86edd7354b091c54e190e2"

PYPI_PACKAGE = "types_psutil"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "15b0ab69c52841cf9ce3c383e8480c620a4d13d6a8e22b16978ebddac5590950"

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "\
	python3-psutil \
"
