DESCRIPTION = "Font Awesome icons packaged for setuptools (easy_install) / pip."
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Font-Awesome"
SECTION = "devel/python"
LICENSE = "OFL-1.0 & MIT & CC-BY-4.0"
LIC_FILES_CHKSUM = "file://xstatic/pkg/font_awesome/data/LICENSE.txt;md5=57f9201afe70f877988912a7b233de47"

PYPI_PACKAGE = "xstatic_font_awesome"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "9f3cb2f038fad7d352722375d3f25af346da9ee093ed9dc2c8c46bd911ab1971"

DEPENDS += " \
    python3-xstatic \
    python3-pip \
"

inherit pypi setuptools3
