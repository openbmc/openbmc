SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=41e24e0e235a97e60cdb5c4137fa2cd0"

SRC_URI[md5sum] = "434b57fdc3230c500716c5aff8896100"
SRC_URI[sha256sum] = "9c6e98edcb33499881b86ede07d9968c81ab7c769e28e9af24075f0a5379f070"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-stringold \
    "
