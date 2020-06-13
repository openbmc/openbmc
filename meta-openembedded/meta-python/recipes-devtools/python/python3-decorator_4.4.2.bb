SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=be2fd2007972bf96c08af3293d728b22"

SRC_URI[md5sum] = "d83c624cce93e6bdfab144821b526e1d"
SRC_URI[sha256sum] = "e3a62f0520172440ca0dcc823749319382e377f37f140a0b99ef45fecb84bfe7"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-stringold \
    "
