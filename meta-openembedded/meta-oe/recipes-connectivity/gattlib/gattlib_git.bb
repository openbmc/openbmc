DESCRIPTION = "Bluetooth library with attribute support"
SECTION = "libs/network"

LICENSE = "GPL-2.0-or-later | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://CMakeLists.txt;beginline=1;endline=6;md5=71fdd2be76b4e95fe28324a70d4981c5"

DEPENDS = "bluez5 glib-2.0"
DEPENDS += "glib-2.0-native"

PV = "0.2+git${SRCPV}"

SRC_URI = "git://github.com/labapart/gattlib.git;branch=master;protocol=https \
           file://dbus-avoid-strange-chars-from-the-build-dir.patch \
           "

SRCBRANCH = "master"
SRCREV = "fa54ae42ccb3d8f911e00b02ed1e581537e47f79"

S = "${WORKDIR}/git"

PACKAGECONFIG[examples] = "-DGATTLIB_BUILD_EXAMPLES=ON,-DGATTLIB_BUILD_EXAMPLES=OFF"

# Set this to force use of DBus API if Bluez version is older than 5.42
PACKAGECONFIG[force-dbus] = "-DGATTLIB_FORCE_DBUS=TRUE,-DGATTLIB_FORCE_DBUS=FALSE"

EXTRA_OECMAKE += "-DGATTLIB_PYTHON_INTERFACE=OFF"
EXTRA_OECMAKE += "-DGATTLIB_BUILD_DOCS=OFF"

inherit pkgconfig cmake

FILES:${PN} = "${libdir}/*"
FILES:${PN}-dev = "${includedir}/* ${libdir}/pkgconfig"
