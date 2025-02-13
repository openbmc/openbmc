SUMMARY = "Filters to enhance web typography, including support for Django & Jinja templates"
HOMEPAGE = "https://github.com/mintchaos/typogrify"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b8558ebcc682078c1a44d0227573006d"

inherit pypi python_hatchling

PYPI_PACKAGE = "typogrify"
SRC_URI[sha256sum] = "f0aa004e98032a6e6be4c9da65e7eb7150e36ca3bf508adbcda82b4d003e61ee"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-smartypants"

