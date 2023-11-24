SUMMARY = "Python bindings for the Apache Thrift RPC system"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

SRC_URI[sha256sum] = "2b5b6488fcded21f9d312aa23c9ff6a0195d0f6ae26ddbd5ad9e3e25dfc14408"

inherit pypi setuptools3

# Use different filename to prevent conflicts with thrift itself
PYPI_SRC_URI:append = ";downloadfilename=${BP}.${PYPI_PACKAGE_EXT}"

RDEPENDS:${PN} += "\
    python3-logging \
    python3-scons \
    python3-six \
    python3-stringold \
    python3-threading \
"

BBCLASSEXTEND = "native"
