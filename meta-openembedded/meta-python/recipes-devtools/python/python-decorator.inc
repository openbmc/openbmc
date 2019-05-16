SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=be2fd2007972bf96c08af3293d728b22"

SRC_URI[md5sum] = "a565a98581c0d110bc247323e89df9b1"
SRC_URI[sha256sum] = "86156361c50488b84a3f148056ea716ca587df2f0de1d34750d35c21312725de"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-stringold \
    "
