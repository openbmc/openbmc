SUMMARY = "Fast property caching"
HOMEPAGE = "https://github.com/aio-libs/propcache"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "f48107a8c637e80362555f37ecf49abe20370e557cc4ab374f04ec4423c97c3d"

inherit pypi python_setuptools_build_meta ptest-python-pytest cython

SRC_URI += " \
    file://0001-build-wheel-in-place.patch \
    file://0001-Update-Cython-to-version-3.2.3-184.patch \
"

DEPENDS += " \
	python3-expandvars-native \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest-codspeed \
	python3-pytest-xdist \
	python3-rich \
	python3-statistics \
"

BBCLASSEXTEND += "native nativesdk"
