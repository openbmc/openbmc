require python-smmap.inc

inherit setuptools

RDEPENDS_${PN} += "python-codecs python-mmap python-lang"
