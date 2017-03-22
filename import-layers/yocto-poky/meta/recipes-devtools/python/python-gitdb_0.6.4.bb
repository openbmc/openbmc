require python-gitdb.inc

DEPENDS = "python-async python-smmap"

inherit distutils

RDEPENDS_${PN} += "python-smmap python-async python-mmap python-lang python-zlib python-io python-shell"
