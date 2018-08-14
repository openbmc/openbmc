SUMMARY = "Convenience functions for dbus services in Python 2.x"
HOMEPAGE = "http://nphilipp.fedorapeople.org/python-slip/"
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

inherit setuptools

SRC_URI[md5sum] = "a6d8ee96245fc21785d4c1c062c85f2f"
SRC_URI[sha256sum] = "bc897f1ab6f5b4989f85548799aaeb84fde67bbba18e40341bfe1d7885c2fd9a"

do_compile_prepend() {
    sed -e 's/@VERSION@/${PV}/g' setup.py.in > setup.py
}
