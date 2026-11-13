SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD-3-Clause AND TCL"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1e3768cfe2662fa77c49c9c2d3804d87"

SRC_URI[sha256sum] = "aef7ea4cd9e2e9f8a35c4538a9780d58c80d76c5da66a8dad269b4cc6d930c29"

CVE_PRODUCT = "ultrajson:ultrajson ultrajson_project:ultrajson"

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
