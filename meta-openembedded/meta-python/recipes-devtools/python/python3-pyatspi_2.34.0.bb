SUMMARY = "Python bindings for DBus AT-SPI2 accessibility"
DESCRIPTION = "AT-SPI2 is a protocol over DBus, toolkit widgets use to provide content to screen readers such as Orca"
SECTION = "devel/python"
HOMEPAGE = "https://www.freedesktop.org/wiki/Accessibility/AT-SPI2/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=db29218e6ba3794c56df7d4987dc7e4d \
                    file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "python3-dbus-native glib-2.0 dbus-glib libxml2 atk gtk+ python3-pygobject"

SRC_URI = "git://github.com/GNOME/pyatspi2.git;protocol=https;branch=mainline"
SRCREV = "cc99d68db66174f4499b9b325bc788393b972edd"
S = "${WORKDIR}/git" 

# Same restriction as gtk+
inherit features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

inherit pkgconfig autotools python3native

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}/pyatspi/*"
