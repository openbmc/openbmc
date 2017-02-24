require python-smmap.inc

inherit setuptools3

RDEPENDS_${PN} += "python3-codecs python3-mmap python3-lang"
