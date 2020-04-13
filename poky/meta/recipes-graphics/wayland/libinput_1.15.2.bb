SUMMARY = "Library to handle input devices in Wayland compositors"
DESCRIPTION = "libinput is a library to handle input devices in Wayland \
compositors and to provide a generic X.Org input driver. It provides \
device detection, device handling, input device event processing and \
abstraction so minimize the amount of custom input code compositors need to \
provide the common set of functionality that users expect."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libinput/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1f2ea9ebff3a2c6d458faf58492efb63"

DEPENDS = "libevdev udev mtdev"

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BP}.tar.xz \
           file://determinism.patch \
           "
SRC_URI[md5sum] = "eb6bd2907ad33d53954d70dfb881a643"
SRC_URI[sha256sum] = "971c3fbfb624f95c911adeb2803c372e4e3647d1b98f278f660051f834597747"

UPSTREAM_CHECK_REGEX = "libinput-(?P<pver>\d+\.\d+\.(?!9\d+)\d+)"

inherit meson pkgconfig lib_package

PACKAGECONFIG ??= ""
PACKAGECONFIG[libwacom] = "-Dlibwacom=true,-Dlibwacom=false,libwacom"
PACKAGECONFIG[gui] = "-Ddebug-gui=true,-Ddebug-gui=false,cairo gtk+3"

UDEVDIR = "`pkg-config --variable=udevdir udev`"

EXTRA_OEMESON += "-Dudev-dir=${UDEVDIR} \
                  -Ddocumentation=false \
                  -Dtests=false \
                  -Dzshcompletiondir=no"

# package name changed in 1.8.1 upgrade: make sure package upgrades work
RPROVIDES_${PN} = "libinput"
RREPLACES_${PN} = "libinput"
RCONFLICTS_${PN} = "libinput"

