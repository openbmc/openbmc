SUMMARY = "Python bindings for DBus AT-SPI2 accessibility"
DESCRIPTION = "AT-SPI2 is a protocol over DBus, toolkit widgets use to provide content to screen readers such as Orca"
SECTION = "devel/python"
HOMEPAGE = "https://www.freedesktop.org/wiki/Accessibility/AT-SPI2/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9f288ba982d60518f375b5898283886 \
                    file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "python3-dbus-native glib-2.0 dbus-glib libxml2 atk gtk+ python3-pygobject"

SRC_URI = "git://github.com/GNOME/pyatspi2.git;protocol=https;branch=gnome-3-32"
SRCREV = "dc4565208fca00da06b972915a080ad3c63f640d"
S = "${WORKDIR}/git" 

# Same restriction as gtk+
inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

inherit pkgconfig autotools python3native

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}/pyatspi/*"
