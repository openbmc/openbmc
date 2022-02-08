SUMMARY = "USB CEC Adaptor communication Library"
HOMEPAGE = "http://libcec.pulse-eight.com/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b3a719e97f49e4841e90573f9b1a98ac"

DEPENDS = "p8platform udev ncurses swig-native python3"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11 libxrandr', '', d)}"
DEPENDS_append_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' userland', d)}"

PV = "5.0.0"

SRCREV = "43bc27fe7be491149e6f57d14110e02abdac2f24"
SRC_URI = "git://github.com/Pulse-Eight/libcec.git;branch=release;protocol=https \
           file://0001-CheckPlatformSupport.cmake-Do-not-hardcode-lib-path.patch \
           file://0001-Enhance-reproducibility.patch \
          "

S = "${WORKDIR}/git"

inherit cmake pkgconfig

# Put client tools into a separate package
PACKAGE_BEFORE_PN += "${PN}-tools"
FILES_${PN}-tools = "${bindir}"
RDEPENDS_${PN}-tools = "python3-${BPN} python3-core"

# Create the wrapper for python3
PACKAGES += "python3-${BPN}"
FILES_python3-${BPN} = "${libdir}/python3* ${bindir}/py*"
RDEPENDS_${PN} = "python3-core"

# cec-client and xbmc need the .so present to work :(
FILES_${PN} += "${libdir}/*.so"
INSANE_SKIP_${PN} = "dev-so"

# Adapter shows up as a CDC-ACM device
RRECOMMENDS_${PN} = "kernel-module-cdc-acm"
