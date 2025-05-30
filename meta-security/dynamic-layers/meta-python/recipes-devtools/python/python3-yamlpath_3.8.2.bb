DESCRIPTION="YAML Path and Command-Line Tools"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5abda174c5040dd12ed2b225e3a096f0"

SRC_URI[sha256sum] = "4f30cc214b5085d4b0e7756e06c3af3ae589ecde9650d2ada7e1d345ec4fda4f"

PYPI_PACKAGE = "yamlpath"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"
RDEPENDS:${PN} += "\
   python3-datetime \
   python3-ruamel-yaml \
   python3-dateutil \
"
