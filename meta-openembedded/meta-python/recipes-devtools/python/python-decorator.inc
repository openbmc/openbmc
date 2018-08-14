SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=893f896413826096ce5270c700a498fd"

SRC_URI[md5sum] = "249e7299b9b4bced0c382343f84eb1c0"
SRC_URI[sha256sum] = "c39efa13fbdeb4506c476c9b3babf6a718da943dab7811c206005a4a956c080c"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-stringold \
    "
