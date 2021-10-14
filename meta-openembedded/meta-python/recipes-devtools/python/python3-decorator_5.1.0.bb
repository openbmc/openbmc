SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=be2fd2007972bf96c08af3293d728b22"

SRC_URI[sha256sum] = "e59913af105b9860aa2c8d3272d9de5a56a4e608db9a2f167a8480b323d529a7"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-stringold \
    "
