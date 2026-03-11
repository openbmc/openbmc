SUMMARY = "A rich help formatter for argparse"
HOMEPAGE = "https://github.com/hamdanal/rich-argparse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=06f5c372171ce02f7e6a18f5f57f0b69"

PYPI_PACKAGE = "rich_argparse"

SRC_URI[sha256sum] = "d7a493cde94043e41ea68fb43a74405fa178de981bf7b800f7a3bd02ac5c27be"

inherit pypi python_hatchling

RDEPENDS:${PN} += "\
	python3-rich \
"

BBCLASSEXTEND = "native nativesdk"
