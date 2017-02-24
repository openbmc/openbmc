SUMMARY="Pylint is a Python source code analyzer"
HOMEPAGE= "http://www.pylint.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

SRC_URI[md5sum] = "66ba9c27e067568bdabcdd7c21303903"
SRC_URI[sha256sum] = "ef901a34b62ed7a734370ba5b162d890231ba8822abe88c6dda1268e2575f5f1"

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
                   python-unittest"

inherit pypi setuptools
