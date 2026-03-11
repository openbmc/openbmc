SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1e3768cfe2662fa77c49c9c2d3804d87"

SRC_URI[sha256sum] = "b3cd8f3c5d8c7738257f1018880444f7b7d9b66232c64649f562d7ba86ad4bc1"

inherit pypi ptest-python-pytest python_setuptools_build_meta

# let OE do the strip operation
export UJSON_BUILD_NO_STRIP = "1"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-numbers \
"

RDEPENDS:${PN}-ptest += " \
    python3-json \
    python3-misc \
    python3-pytz \
"

BBCLASSEXTEND = "native nativesdk"
