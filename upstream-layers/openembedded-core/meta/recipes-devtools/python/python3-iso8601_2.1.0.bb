SUMMARY = "Simple module to parse ISO 8601 dates"
HOMEPAGE = "http://pyiso8601.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aab31f2ef7ba214a5a341eaa47a7f367"

SRC_URI[sha256sum] = "6b1d3829ee8921c4301998c909f7829fa9ed3cbdac0d3b16af2d743aed1ba8df"

inherit pypi python_poetry_core

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
