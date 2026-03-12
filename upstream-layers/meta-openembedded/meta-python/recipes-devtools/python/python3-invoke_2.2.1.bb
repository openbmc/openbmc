SUMMARY = "Pythonic task execution"
HOMEPAGE = "https://www.pyinvoke.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8815068973f31b78c328dc067e297ab"

SRC_URI[sha256sum] = "515bf49b4a48932b79b024590348da22f39c4942dff991ad1fb8b8baea1be707"

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
