SUMMARY = "The xmlschema library is an implementation of XML Schema for Python."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=385fddea479acdec12ab77a938f68cd9"

SRC_URI[sha256sum] = "f3b33a21a2b2cdc404ddc3fb599d8bc1dee90b155d547370672e6e684746b8d6"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-elementpath \
    python3-modules \
"

BBCLASSEXTEND = "native nativesdk"
