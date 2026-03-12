SUMMARY = "A rich help formatter for argparse"
HOMEPAGE = "https://github.com/hamdanal/rich-argparse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=06f5c372171ce02f7e6a18f5f57f0b69"

PYPI_PACKAGE = "rich_argparse"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "64fd2e948fc96e8a1a06e0e72c111c2ce7f3af74126d75c0f5f63926e7289cd1"

inherit pypi python_hatchling

RDEPENDS:${PN} += "\
	python3-rich \
"

BBCLASSEXTEND = "native nativesdk"
