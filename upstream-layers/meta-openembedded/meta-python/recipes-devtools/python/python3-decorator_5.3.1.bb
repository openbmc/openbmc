SUMMARY = "Python decorator utilities"
DESCRIPTION = "\
The aim of the decorator module it to simplify the usage of decorators \
for the average programmer, and to popularize decorators by showing \
various non-trivial examples. Of course, as all techniques, decorators \
can be abused and you should not try to solve every problem with a \
decorator, just because you can."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=fb3c386712bc6cfb52aae3692d1ebbd4"

SRC_URI[sha256sum] = "4cbcdd55a6efadb9dbea26b858f4fb3264567b52d69ca0d25b721b553f60ea82"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-stringold \
    "

BBCLASSEXTEND = "native nativesdk"
