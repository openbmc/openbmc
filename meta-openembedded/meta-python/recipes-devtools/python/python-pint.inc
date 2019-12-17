DESCRIPTION = "Interval arithmetic for Python"
HOMEPAGE = "https://github.com/AlexandreDecan/python-intervals"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

PYPI_PACKAGE := "Pint"

inherit pypi

SRC_URI[md5sum] = "d0681cb7cfaca9fc68ce7edab0d08d88"
SRC_URI[sha256sum] = "32d8a9a9d63f4f81194c0014b3b742679dce81a26d45127d9810a68a561fe4e2"

BBCLASSEXTEND = "native"
