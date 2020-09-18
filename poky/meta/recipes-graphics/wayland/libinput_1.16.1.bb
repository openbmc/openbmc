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

DEPENDS = "libevdev udev mtdev libcheck"

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BP}.tar.xz \
           file://run-ptest \
           file://determinism.patch \
           "
SRC_URI[sha256sum] = "7ba7d1aeedd15168bb21d17e9e628aa1c27957963a423a3fea3938a501758539"

UPSTREAM_CHECK_REGEX = "libinput-(?P<pver>\d+\.\d+\.(?!9\d+)\d+)"

inherit meson pkgconfig lib_package ptest

# Patch out build directory, otherwise it leaks into ptest binary
do_configure_append() {
    sed -i -e "s,${WORKDIR},,g" config.h
    if [ -e "litest-config.h" ]; then
        sed -i -e "s,${WORKDIR},,g" litest-config.h
    fi
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[libwacom] = "-Dlibwacom=true,-Dlibwacom=false,libwacom"
PACKAGECONFIG[gui] = "-Ddebug-gui=true,-Ddebug-gui=false,cairo gtk+3"

UDEVDIR = "`pkg-config --variable=udevdir udev`"

EXTRA_OEMESON += "-Dudev-dir=${UDEVDIR} \
                  -Ddocumentation=false \
                  ${@bb.utils.contains('PTEST_ENABLED', '1', '-Dtests=true -Dinstall-tests=true', '-Dtests=false -Dinstall-tests=false', d)} \
                  -Dzshcompletiondir=no"

# package name changed in 1.8.1 upgrade: make sure package upgrades work
RPROVIDES_${PN} = "libinput"
RREPLACES_${PN} = "libinput"
RCONFLICTS_${PN} = "libinput"

FILES_${PN}-ptest += "${libexecdir}/libinput/libinput-test-suite"
