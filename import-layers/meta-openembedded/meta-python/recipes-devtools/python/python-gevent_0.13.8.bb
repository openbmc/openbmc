SUMMARY = "A coroutine-based Python networking library"
DESCRIPTION = "gevent is a coroutine-based Python networking library that uses greenlet to provide \
a high-level synchronous API on top of the libevent event loop."
HOMEPAGE = "http://www.gevent.org"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2dbb33d00e1fd31c7041460a81ac0bd2 \
                    file://LICENSE.pyevent;md5=718070c63de243053e2c616268b00fdd"
DEPENDS += "python-greenlet libevent"
RDEPENDS_${PN} += "python-greenlet python-mime python-pprint python-re"

SRC_URI[md5sum] = "ca9dcaa7880762d8ebbc266b11252960"
SRC_URI[sha256sum] = "54b8d26300ce408c0916a3e63ef6cd3e6aca76230833558deb7de15196ed955e"

inherit pypi setuptools

