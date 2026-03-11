SUMMARY = "Pythonic task execution"
HOMEPAGE = "https://www.pyinvoke.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8815068973f31b78c328dc067e297ab"

SRC_URI[sha256sum] = "ee6cbb101af1a859c7fe84f2a264c059020b0cb7fe3535f9424300ab568f6bd5"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
        python3-fcntl \
        python3-json \
        python3-logging \
        python3-pprint \
        python3-terminal \
        python3-unittest \
        python3-unixadmin \
"
