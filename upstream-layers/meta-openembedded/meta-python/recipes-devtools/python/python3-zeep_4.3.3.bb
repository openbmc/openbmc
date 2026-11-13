SUMMARY = "A Python SOAP client"
DESCRIPTION = "A Python SOAP client"
HOMEPAGE = "https://github.com/mvantellingen/python-zeep"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=23356a26b06086844496d9e634f58ae5"

SRC_URI[sha256sum] = "99d5059f92f721020998695fd9c85289ccb03ec5a2398ad49c6dbe43f19cfb94"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += "\
    python3-attrs \
    python3-lxml \
    python3-platformdirs \
    python3-requests \
    python3-requests-file \
    python3-requests-toolbelt \
    python3-isodate \
"
