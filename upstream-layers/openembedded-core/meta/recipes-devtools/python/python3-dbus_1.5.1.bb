SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT AND (AFL-2.1 OR GPL-2.0-or-later)"
LIC_FILES_CHKSUM = "file://COPYING;md5=97f58951300aa52a9f9e3a62bd5c846c \
                    file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663 \
                    file://subprojects/dbus-gmain/LICENSES/GPL-2.0-or-later.txt;md5=3d26203303a722dedc6bf909d95ba815 \
                    file://subprojects/dbus-gmain/LICENSES/AFL-2.1.txt;md5=f3ad2f482ec639b440413665cfb9e714 \
                    "
DEPENDS = "dbus glib-2.0"

PYPI_PACKAGE = "dbus-python"

SRC_URI[sha256sum] = "57423cf87d09b7d084f420b55b737fe654c33339b06df77945620d97795c6ac4"

inherit pypi setuptools3-base meson pkgconfig

EXTRA_OEMESON += "-Dtests=disabled"

RDEPENDS:${PN} = "python3-io python3-logging python3-stringold python3-threading python3-xml"

BBCLASSEXTEND = "native nativesdk"
