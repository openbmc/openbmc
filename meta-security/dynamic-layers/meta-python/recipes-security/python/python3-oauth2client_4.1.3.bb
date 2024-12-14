SUMMARY = "Add version info to file paths."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=038e1390e94fe637991fa5569daa62bc"

PYPI_PACKAGE = "oauth2client"
SRC_URI[sha256sum] = "d486741e451287f69568a4d26d70d9acd73a2bbfa275746c535b4209891cccc6"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} = "python3-six python3-rsa python3-httplib2 python3-pyasn1 python3-pyasn1-modules"
