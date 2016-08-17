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

SRC_URI = "https://fedorahosted.org/released/${SRCNAME}/${SRCNAME}-${PV}.tar.bz2"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

SRC_URI[md5sum] = "fb3299d75af1a67ca6679d96ce839da6"
SRC_URI[sha256sum] = "f47361ec52b608309b83c71905e692b6b363eaf3b8a7afdeff866cd94463ad5c"

do_compile_prepend() {
    sed -e 's/@VERSION@/${PV}/g' setup.py.in > setup.py
}
