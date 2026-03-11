SUMMARY = "YAML/JSON validation library"
DESCRIPTION = "pykwalify is a schema validator for YAML and JSON"
HOMEPAGE = "https://pypi.org/project/pykwalify/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4eaf57328ad29ea2e0ed31a57f0914dc"

SRC_URI[sha256sum] = "796b2ad3ed4cb99b88308b533fb2f559c30fa6efb4fa9fda11347f483d245884"

PYPI_PACKAGE = "pykwalify"

inherit setuptools3 pypi

RDEPENDS:${PN} = "\
    python3-dateutil \
    python3-docopt \
    python3-logging \
    python3-ruamel-yaml \
"

BBCLASSEXTEND = "native nativesdk"
