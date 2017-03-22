require python-gitdb.inc

DEPENDS = "python3-async python3-smmap"

inherit distutils3

RDEPENDS_${PN} += "python3-smmap python3-async python3-mmap python3-lang python3-io python3-shell python3-crypt python3-compression"
