SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=69f84fd117b2398674e12b8380df27c8"

SRC_URI[sha256sum] = "65f266143752f734b0a7cc83c46f4618af75b8c5911b00ccb61d0ac9b6da0360"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-stringold \
    "

BBCLASSEXTEND = "native nativesdk"
