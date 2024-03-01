DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI[sha256sum] = "3b2eede94d64538cb894eec50302a881e056ed7e057f0e24fb45b28a19d6b2e8"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-dnspython \
	python3-six \
	python3-greenlet \
"
