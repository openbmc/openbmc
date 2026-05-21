SUMMARY = "A rich help formatter for argparse"
HOMEPAGE = "https://github.com/hamdanal/rich-argparse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=06f5c372171ce02f7e6a18f5f57f0b69"

PYPI_PACKAGE = "rich_argparse"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "679df3d832fa94ad6e4bdb07ded088cd7ea2dddc58ae9b2b46346a40b06cbc0c"

inherit pypi python_hatchling

RDEPENDS:${PN} += "\
	python3-rich \
"

BBCLASSEXTEND = "native nativesdk"
