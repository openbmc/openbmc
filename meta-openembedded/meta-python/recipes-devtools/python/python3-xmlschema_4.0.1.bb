SUMMARY = "The xmlschema library is an implementation of XML Schema for Python."
HOMEPAGE = "https://github.com/sissaschool/xmlschema"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=26aa26eda991a3a2b61c11b62d3fda65"

SRC_URI[sha256sum] = "9a8598daa2e2859c499f6f08242f7547804ae0c2d037971c44bcb9aae154ff22"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-elementpath \
    python3-modules \
"

BBCLASSEXTEND = "native nativesdk"
