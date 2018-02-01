SUMMARY="Pylint is a Python source code analyzer"
HOMEPAGE= "http://www.pylint.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

SRC_URI[md5sum] = "31da2185bf59142479e4fa16d8a9e347"
SRC_URI[sha256sum] = "a673984a8dd78e4a8b8cfdee5359a1309d833cf38405008f4a249994a8456719"

RDEPENDS_${PN} += "python-codecs \
                   python-contextlib \
                   python-core \
                   python-distutils \
                   python-io \
                   python-lang \
                   python-multiprocessing \
                   python-netserver \
                   python-numbers \
                   python-pickle \
                   python-re \
                   python-shell \
                   python-six \
                   python-stringold \
                   python-subprocess \
                   python-textutils \
                   python-unittest \
                   python-backports-functools-lru-cache \
                   python-setuptools \
                   python-astroid \
                   python-wrapt \
                   python-isort \
                   python-lazy-object-proxy \
                   "

inherit pypi setuptools

do_install_append(){
    rm ${D}${bindir}/pylint
    cat >> ${D}${bindir}/pylint <<EOF
#!/usr/bin/env python
from pylint import run_pylint
run_pylint()
EOF
    chmod 755 ${D}${bindir}/pylint
}
