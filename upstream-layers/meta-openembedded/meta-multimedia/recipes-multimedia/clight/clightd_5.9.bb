SUMMARY = "D-Bus service providing screen management and ambient light detection"
HOMEPAGE = "https://github.com/FedeDP/clightd"
SECTION = "utils"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"


SRCREV = "d4e88b1d351d61151c99a399abd526d9b7dda12d"
SRC_URI = "git://github.com/FedeDP/${BPN};protocol=https;branch=master;tag=${PV} \
"

inherit cmake pkgconfig systemd features_check

REQUIRED_DISTRO_FEATURES = "polkit systemd"

DEPENDS += "\
    dbus \
    libiio \
    libjpeg-turbo \
    libmodule \
    polkit \
    systemd \
    udev \
"

PACKAGECONFIG ??= ""

PACKAGECONFIG[dpms]       = "-DENABLE_DPMS=1,-DENABLE_DPMS=0,libx11 xext libdrm wayland"
PACKAGECONFIG[gamma]      = "-DENABLE_GAMMA=1,-DENABLE_GAMMA=0,libx11 xrandr libdrm wayland"
PACKAGECONFIG[pipewire]   = "-DENABLE_PIPEWIRE=1,-DENABLE_PIPEWIRE=0,pipewire"
PACKAGECONFIG[screen]     = "-DENABLE_SCREEN=1,-DENABLE_SCREEN=0,libx11"
PACKAGECONFIG[yoctolight] = "-DENABLE_YOCTOLIGHT=1,-DENABLE_YOCTOLIGHT=0,libusb1"

FILES:${PN} += " \
    ${libdir}/* \
    ${datadir}/* \
"

SYSTEMD_SERVICE:${PN} += "clightd.service"
