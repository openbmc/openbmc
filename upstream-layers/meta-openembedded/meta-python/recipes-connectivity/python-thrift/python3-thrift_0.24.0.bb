SUMMARY = "Python bindings for the Apache Thrift RPC system"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

SRC_URI[sha256sum] = "9ef601c49e988475ff0e741d8e1b45feec23b48514e524341efc274191f1789c"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

# Use different filename to prevent conflicts with thrift itself
PYPI_ARCHIVE_NAME_PREFIX = "python-"

RDEPENDS:${PN} += "\
    python3-logging \
    python3-scons \
    python3-six \
    python3-stringold \
    python3-threading \
"

BBCLASSEXTEND = "native"
