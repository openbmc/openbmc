SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=893f896413826096ce5270c700a498fd"

SRC_URI[md5sum] = "a0f7f4fe00ae2dde93494d90c192cf8c"
SRC_URI[sha256sum] = "7cb64d38cb8002971710c8899fbdfb859a23a364b7c99dab19d1f719c2ba16b5"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-stringold \
    "
