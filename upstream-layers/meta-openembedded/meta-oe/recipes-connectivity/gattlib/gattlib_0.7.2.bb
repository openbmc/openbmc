DESCRIPTION = "Bluetooth library with attribute support"
SECTION = "libs/network"

LICENSE = "GPL-2.0-or-later | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://CMakeLists.txt;beginline=1;endline=6;md5=f44a9a14d37330e7cd454e694e714ab8"

DEPENDS = "bluez5 glib-2.0 glib-2.0-native python3-packaging-native"

SRC_URI = "git://github.com/labapart/gattlib.git;branch=master;protocol=https;tag=${PV} \
           file://dbus-avoid-strange-chars-from-the-build-dir.patch \
           file://0001-Add-missing-include.patch \
           "

SRCBRANCH = "master"
SRCREV = "f99558d9b8e3dbba2a952a0b292d3497aec8ee69"


CVE_STATUS[CVE-2019-6498] = "fixed-version: patch is already included in sources"

PACKAGECONFIG[examples] = "-DGATTLIB_BUILD_EXAMPLES=ON,-DGATTLIB_BUILD_EXAMPLES=OFF"

# Set this to force use of DBus API if Bluez version is older than 5.42
PACKAGECONFIG[force-dbus] = "-DGATTLIB_FORCE_DBUS=TRUE,-DGATTLIB_FORCE_DBUS=FALSE"

EXTRA_OECMAKE += "-DGATTLIB_PYTHON_INTERFACE=OFF"
EXTRA_OECMAKE += "-DGATTLIB_BUILD_DOCS=OFF"

inherit pkgconfig cmake python3native

do_compile:append() {
    for f in org-bluez-gattdescriptor1.c org-bluez-battery1.c org-bluez-adaptater1.c \
        org-bluez-device1.c org-bluez-gattservice1.c org-bluez-gattcharacteristic1.c; do
        sed -i -e 's|${B}/dbus/||g' ${B}/dbus/$f
    done
}

FILES:${PN} = "${libdir}/*"
FILES:${PN}-dev = "${includedir}/* ${libdir}/pkgconfig"
