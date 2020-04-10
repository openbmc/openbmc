SUMMARY = "Linux libcamera framework"
SECTION = "libs"

LICENSE = "GPL-2.0 & LGPL-2.1"

LIC_FILES_CHKSUM = "\
    file://licenses/gnu-gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://licenses/gnu-lgpl-2.1.txt;md5=4b54a1fd55a448865a0b32d41598759d \
"

SRC_URI = " \
        git://linuxtv.org/libcamera.git;protocol=git \
        file://0001-meson-import-python3-to-use-also-from-sysroot.patch \
"

SRCREV = "53eab996809e826bc914e4c34c78fe74d86f8dc4"

PV = "201910+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS = "python3-pyyaml-native udev"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'qt', 'qtbase qtbase-native', '', d)}"

RDEPENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland qt', 'qtwayland', '', d)}"

inherit meson pkgconfig python3native

FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig"
FILES_${PN} += " ${libdir}/libcamera.so"

