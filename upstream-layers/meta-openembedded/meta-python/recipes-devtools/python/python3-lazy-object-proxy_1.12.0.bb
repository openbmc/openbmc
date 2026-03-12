SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0cd92fd68aacee0503822c05a3ee6a2"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "1f5a462d92fd0cfb82f1fab28b51bfb209fabbe6aabf7f0d51472c0c124c0c61"

PYPI_PACKAGE = "lazy_object_proxy"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

do_install:append() {
	# contain moddate, makes is non-reproducible
	rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/lazy_object_proxy/__pycache__/*.cpython-*.pyc
}
