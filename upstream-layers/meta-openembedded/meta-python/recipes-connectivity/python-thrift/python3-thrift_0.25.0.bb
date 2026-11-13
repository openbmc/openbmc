SUMMARY = "Python bindings for the Apache Thrift RPC system"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

SRC_URI[sha256sum] = "e85615ae9245f5622a5e36a9d2d867e1e7b17a8ca467b257dece5dc1642ccd73"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

# Use different filename to prevent conflicts with thrift itself
PYPI_ARCHIVE_NAME_PREFIX = "python-"

RDEPENDS:${PN} += "\
    python3-logging \
    python3-multiprocessing \
    python3-scons \
    python3-stringold \
    python3-threading \
"

BBCLASSEXTEND = "native"
