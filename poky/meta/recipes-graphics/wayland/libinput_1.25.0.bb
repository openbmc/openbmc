SUMMARY = "Library to handle input devices in Wayland compositors"
DESCRIPTION = "libinput is a library to handle input devices in Wayland \
compositors and to provide a generic X.Org input driver. It provides \
device detection, device handling, input device event processing and \
abstraction so minimize the amount of custom input code compositors need to \
provide the common set of functionality that users expect."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libinput/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bab4ac7dc1c10bc0fb037dc76c46ef8a"

DEPENDS = "libevdev udev mtdev"

SRC_URI = "git://gitlab.freedesktop.org/libinput/libinput.git;protocol=https;branch=main \
           file://run-ptest \
           "
SRCREV = "3fd38d89276b679ac3565efd7c2150fd047902cb"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_REGEX = "libinput-(?P<pver>\d+\.\d+\.(?!9\d+)\d+)"

inherit meson pkgconfig lib_package ptest

# Patch out build directory, otherwise it leaks into ptest binary
do_configure:append() {
    sed -i -e "s,${WORKDIR},,g" config.h
    if [ -e "litest-config.h" ]; then
        sed -i -e "s,${WORKDIR},,g" litest-config.h
    fi
}

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[libwacom] = "-Dlibwacom=true,-Dlibwacom=false,libwacom"
PACKAGECONFIG[gui] = "-Ddebug-gui=true,-Ddebug-gui=false,cairo gtk+3 wayland-native"
PACKAGECONFIG[tests] = "-Dtests=true -Dinstall-tests=true,-Dtests=false -Dinstall-tests=false,libcheck"

UDEVDIR = "`pkg-config --variable=udevdir udev`"

EXTRA_OEMESON += "-Dudev-dir=${UDEVDIR} \
                  -Ddocumentation=false \
                  -Dzshcompletiondir=no"

# package name changed in 1.8.1 upgrade: make sure package upgrades work
RPROVIDES:${PN} = "libinput"
RREPLACES:${PN} = "libinput"
RCONFLICTS:${PN} = "libinput"

FILES:${PN}-ptest += "${libexecdir}/libinput/libinput-test-suite"
