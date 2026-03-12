SUMMARY = "Library for Python 3.6+ to communicate with the Google Chromecast."
HOMEPAGE = "https://github.com/balloob/pychromecast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1dbd4e85f47b389bdadee9c694669f5"

SRC_URI += "file://0001-bump-required-version-to-0.46.1-for-python3-wheel.patch \
           file://0002-allow-newer-wheel.patch \
           "

SRC_URI[sha256sum] = "fe78841b0b04aa107d08aed216e91ab9cfb54b11d089d382be4e987e3631d4a6"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-zeroconf (>=0.131.0) \
    python3-protobuf (>=4.25.2) \
    python3-casttube (>=0.2.1) \
"
