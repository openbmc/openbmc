SUMMARY = "Library for Python 3.6+ to communicate with the Google Chromecast."
HOMEPAGE = "https://github.com/balloob/pychromecast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1dbd4e85f47b389bdadee9c694669f5"

SRC_URI += "\
            file://0001-Update-setuptools-requirement-from-78.0-65.6-to-65.6.patch \
            file://0001-Update-setuptools-requirement-from-79.0-65.6-to-65.6.patch \
            file://0001-Update-setuptools-requirement-from-80.0-65.6-to-65.6.patch \
"

SRC_URI[sha256sum] = "7abbae80a2c9e05b93b1a7b8b4d771bbc764d88fd5e56a566f46ac1bd3f93848"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-zeroconf (>=0.131.0) \
    python3-protobuf (>=4.25.2) \
    python3-casttube (>=0.2.1) \
"
