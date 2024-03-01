SUMMARY = "jsonref is a library for automatic dereferencing of JSON Reference objects for Python"
HOMEPAGE = "https://github.com/gazpachoking/jsonref"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ac1cccee5d43e11fc4eddcf445be64a"

SRC_URI[sha256sum] = "32fe8e1d85af0fdefbebce950af85590b22b60f9e95443176adbde4e1ecea552"

SRC_URI += "file://migrate-to-pdm-backend.patch"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

DEPENDS += " \
    python3-pdm-native \
    python3-pdm-backend-native \
"

RDEPENDS:${PN} += " \
    python3-core \
    python3-json \
    python3-netclient \
"
