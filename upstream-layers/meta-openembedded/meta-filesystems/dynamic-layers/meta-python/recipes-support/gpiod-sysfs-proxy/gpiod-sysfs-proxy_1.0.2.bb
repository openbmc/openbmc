SUMMARY = "User-space, libgpiod-based compatibility layer for linux GPIO sysfs interface."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT.txt;md5=b1008aa4e86ef6163fc80a22d1547bea"

inherit systemd update-rc.d ptest pypi python_pep517 python_setuptools_build_meta useradd

PYPI_PACKAGE = "gpiod_sysfs_proxy"

SRC_URI += " \
    file://gpiod-sysfs-proxy.init.in \
    file://run-ptest.in \
"

SRC_URI[sha256sum] = "a12fc9d0778078bf351daa4fab244910ac550e7bef77a12e47c16a1161bc539d"

# For full backward compatibility with the kernel sysfs interface, this option
# must be selected. However, we don't make it the default as - with kernel sysfs
# disabled - it plays a silly game with /sys/class, where it mounts a read-only
# overlay containing the missing /sys/class/gpio directory. This is a rather
# non-standard behavior so make sure the user actually wants it.
PACKAGECONFIG[sys-class-mount] = ""

export MOUNTPOINT = "${@bb.utils.contains('PACKAGECONFIG', 'sys-class-mount', '/sys/class/gpio', '/run/gpio', d)}"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${UNPACKDIR}/gpiod-sysfs-proxy.init.in ${D}${sysconfdir}/init.d/gpiod-sysfs-proxy
        sed -i "s:@mountpoint@:$MOUNTPOINT:g" ${D}${sysconfdir}/init.d/gpiod-sysfs-proxy
    fi
}

SYSTEMD_SERVICE:${PN} = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'sys-class-mount', \
                         'gpiod-sysfs-proxy@sys-class-gpio.service', \
                         'gpiod-sysfs-proxy@run-gpio.service', d)} \
"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_NAME = "gpiod-sysfs-proxy"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 20 0 1 6 ."

FILES:${PN} += "/usr/lib/systemd/system"

RDEPENDS:${PN} += " \
    python3-gpiod \
    python3-pyfuse3 \
    python3-pyudev \
    python3-trio \
"

python __anonymous() {
    if d.getVar("PTEST_ENABLED") == "1":
        d.appendVar("SRC_URI", "git://github.com/brgl/gpio-sysfs-compat-tests;protocol=https;branch=main;destsuffix=tests;name=tests")
        d.setVar("SRCREV_tests", "2882af358480afcf7eed85584cddd560d6673637")
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests/
    install -m 0755 ${UNPACKDIR}/run-ptest.in ${D}${PTEST_PATH}/run-ptest
    sed -i "s:@mountpoint@:$MOUNTPOINT:g" ${D}${PTEST_PATH}/run-ptest
    install -m 0755 ${UNPACKDIR}/tests/gpio-sysfs-compat-tests ${D}${PTEST_PATH}/tests/gpio-sysfs-compat-tests
}

# Test user is created for verifying chown() and chmod() operations.
USERADD_PACKAGES = "${PN}-ptest"
GROUPADD_PARAM:${PN}-ptest = "--system gpio-test"
USERADD_PARAM:${PN}-ptest = "--system -M -s /bin/nologin -g gpio-test gpio-test"

RDEPENDS:${PN}-ptest += "kmod python3-multiprocess"
RRECOMMENDS:${PN}-ptest += "kernel-module-gpio-sim kernel-module-configfs"
