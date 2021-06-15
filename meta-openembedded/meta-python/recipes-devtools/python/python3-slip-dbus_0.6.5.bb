SUMMARY = "Convenience functions for dbus services in Python 2.x"
HOMEPAGE = "https://github.com/nphilipp/python-slip"
DESCRIPTION = "\
The Simple Library for Python 2.x packages contain miscellaneous code for \
convenience, extension and workaround purposes. \
\
This package provides slip.dbus.service.Object, which is a dbus.service.Object \
derivative that ends itself after a certain time without being used and/or if \
there are no clients anymore on the message bus, as well as convenience \
functions and decorators for integrating a dbus service with PolicyKit."

SECTION = "devel/python"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"
SRCNAME = "python-slip"

SRC_URI = "https://github.com/nphilipp/${SRCNAME}/releases/download/${SRCNAME}-${PV}/${SRCNAME}-${PV}.tar.bz2"
S = "${WORKDIR}/${SRCNAME}-${PV}"

SRC_URI[md5sum] = "28ae5f93853466c44ec96706ba2a1eb4"
SRC_URI[sha256sum] = "c726c086f0dd93a0ac7a0176f383a12af91b6657b78a301e3f5b25d9f8d4d10b"

do_compile_prepend() {
    sed -e 's/@VERSION@/${PV}/g' ${S}/setup.py.in > ${S}/setup.py
}

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-six \
"
# http://errors.yoctoproject.org/Errors/Details/184713/
# python-native/python: can't open file 'setup.py': [Errno 2] No such file or directory
CLEANBROKEN = "1"

inherit setuptools3
