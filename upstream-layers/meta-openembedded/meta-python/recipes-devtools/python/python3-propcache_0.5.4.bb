SUMMARY = "Fast property caching"
HOMEPAGE = "https://github.com/aio-libs/propcache"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "ff6b113f50bc066a698db5d944d2c6dc7507168dd3341e255a8892fd0715a558"

inherit pypi python_setuptools_build_meta ptest-python-pytest cython

# Build the C extensions in the source tree instead of a temporary copy
PEP517_BUILD_OPTS += "-C build-inplace=true"

DEPENDS += " \
	python3-expandvars-native \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest-codspeed \
	python3-pytest-xdist \
	python3-rich \
	python3-statistics \
"

do_install_ptest:append() {
	# tests the in-tree build backend, which is not installed
	rm -f ${D}${PTEST_PATH}/tests/test_pep517_backend.py
}

BBCLASSEXTEND += "native nativesdk"
