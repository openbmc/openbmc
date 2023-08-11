SUMMARY = "An implementation of the Python 3 asyncio event loop on top of GLib"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

inherit setuptools3 pypi features_check

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

SRC_URI[md5sum] = "60153055e76ceaacdfbaeafb03d61dd9"
SRC_URI[sha256sum] = "fe3ceb2ba5f541330c07ca1bd7ae792468d625bad1acf5354a3a7a0b9fd87521"

RDEPENDS:${PN} += "python3-asyncio python3-pygobject"
