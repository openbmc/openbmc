SUMMARY = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=affe5a7d8b988c3db245c01075b29e17"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "importlib_metadata"

SRC_URI[sha256sum] = "ab830580bc0ef3db61ce8fae716389e5462b67e033018bab6d8f80ef17172f99"

S = "${UNPACKDIR}/importlib_metadata-${PV}"

DEPENDS += "python3-setuptools-scm-native python3-coherent-licensed-native"
RDEPENDS:${PN} += "python3-zipp"
RDEPENDS:${PN}:append:class-target = " python3-misc"
RDEPENDS:${PN}:append:class-nativesdk = " python3-misc"

BBCLASSEXTEND = "native nativesdk"
