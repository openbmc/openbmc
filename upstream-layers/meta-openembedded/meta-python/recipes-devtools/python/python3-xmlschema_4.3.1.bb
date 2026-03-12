SUMMARY = "The xmlschema library is an implementation of XML Schema for Python."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=385fddea479acdec12ab77a938f68cd9"

SRC_URI[sha256sum] = "853effdfaf127849d4724368c17bd669e7f1486e15a0376404ad7954ec31a338"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-elementpath \
    python3-modules \
"

BBCLASSEXTEND = "native nativesdk"
