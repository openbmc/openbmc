SUMMARY = "Python bindings for the Apache Thrift RPC system"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

SRC_URI[sha256sum] = "5f43448a92c36ed6a450048355d10e231a1787e4c28965f08fabac0eb978914c"

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
