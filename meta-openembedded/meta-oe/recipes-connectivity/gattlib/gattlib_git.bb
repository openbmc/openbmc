DESCRIPTION = "Bluetooth library with attribute support"
SECTION = "libs/network"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://CMakeLists.txt;beginline=1;endline=20;md5=8d5efeb9189b60866baff80ff791bf00"

DEPENDS = "bluez5 glib-2.0"
DEPENDS += "glib-2.0-native"

PV = "0.2+git${SRCPV}"

SRC_URI = "git://github.com/labapart/gattlib.git \
           file://dbus-avoid-strange-chars-from-the-build-dir.patch \
           file://0001-cmake-Use-GNUInstallDirs.patch \
           "

SRCBRANCH = "master"
SRCREV = "5c7ee43bd70ee09a7170ddd55b9fdbdef69e9080"

S = "${WORKDIR}/git"

PACKAGECONFIG[examples] = "-DGATTLIB_BUILD_EXAMPLES=ON,-DGATTLIB_BUILD_EXAMPLES=OFF"

# Set this to force use of DBus API if Bluez version is older than 5.42
PACKAGECONFIG[force-dbus] = "-DGATTLIB_FORCE_DBUS=TRUE,-DGATTLIB_FORCE_DBUS=FALSE"

EXTRA_OECMAKE += "-DGATTLIB_BUILD_DOCS=OFF"

inherit pkgconfig cmake

FILES_${PN} = "${libdir}/*"
FILES_${PN}-dev = "${includedir}/*"
