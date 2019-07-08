SUMMARY = "USB CEC Adaptor communication Library"
HOMEPAGE = "http://libcec.pulse-eight.com/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=e61fd86f9c947b430126181da2c6c715"

DEPENDS = "p8platform udev ncurses swig-native python3"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11 libxrandr', '', d)}"
DEPENDS_append_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' userland', d)}"

PV = "4.0.2+gitr${SRCPV}"

SRCREV = "0a97062dd4b196ceeb003ec41841c7a7edc36dd1"
SRC_URI = "git://github.com/Pulse-Eight/libcec.git \
           file://0001-Explicitly-use-python3-in-pyCecClient.patch"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

# Put client tools into a separate package
PACKAGE_BEFORE_PN += "${PN}-tools"
FILES_${PN}-tools = "${bindir}"
RDEPENDS_${PN}-tools = "python3-${BPN}"

# Create the wrapper for python3
PACKAGES += "python3-${BPN}"
FILES_python3-${BPN} = "${libdir}/python3* ${bindir}/py*"
RDEPENDS_${PN} = "python3-core"

# cec-client and xbmc need the .so present to work :(
FILES_${PN} += "${libdir}/*.so"
INSANE_SKIP_${PN} = "dev-so"

# Adapter shows up as a CDC-ACM device
RRECOMMENDS_${PN} = "kernel-module-cdc-acm"
