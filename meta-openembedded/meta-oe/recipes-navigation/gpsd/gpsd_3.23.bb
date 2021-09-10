SUMMARY = "A TCP/IP Daemon simplifying the communication with GPS devices"
SECTION = "console/network"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=7a5d174db44ec45f9638b2c747806821"
DEPENDS = "dbus ncurses python3 pps-tools"
PROVIDES = "virtual/gpsd"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/${BPN}/${BP}.tar.gz \
    file://0001-SConscript-Correct-the-installation-of-gpsd.hotplug.patch \
    file://gpsd.init \
"
SRC_URI[md5sum] = "b4f96cb01cbc06542b1cb66b3296078d"
SRC_URI[sha256sum] = "522c2362a7eb2d7ac37eaa1504f12aded1c373479a87ba06cc6795974b567bbc"

inherit scons update-rc.d python3-dir python3native systemd update-alternatives

INITSCRIPT_PACKAGES = "gpsd-conf"
INITSCRIPT_NAME = "gpsd"
INITSCRIPT_PARAMS = "defaults 35"

SYSTEMD_OESCONS = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false',d)}"

export STAGING_INCDIR
export STAGING_LIBDIR

CLEANBROKEN = "1"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} usb"
PACKAGECONFIG[bluez] = "bluez='true',bluez='false',bluez5"
PACKAGECONFIG[qt] = "qt='yes' qt_versioned=5,qt='no',qtbase"
PACKAGECONFIG[usb] = "usb='true',usb='false',libusb1"
EXTRA_OESCONS = " \
    sysroot=${STAGING_DIR_TARGET} \
    libQgpsmm='false' \
    debug='false' \
    nostrip='true' \
    systemd='${SYSTEMD_OESCONS}' \
    libdir='${libdir}' \
    udevdir='${nonarch_base_libdir}/udev' \
    unitdir='${systemd_system_unitdir}' \
    manbuild='false' \
    LINK='${CC}' \
    ${PACKAGECONFIG_CONFARGS} \
"
# This cannot be used, because then chrpath is not found and only static lib is built
# target=${HOST_SYS}

do_compile:prepend() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export STAGING_PREFIX="${STAGING_DIR_HOST}/${prefix}"
    export LD="${CC}"
    export LINKFLAGS="${LDFLAGS}"
}

do_install() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export STAGING_PREFIX="${STAGING_DIR_HOST}/${prefix}"
    export LD="${CC}"
    export LINKFLAGS="${LDFLAGS}"

    export DESTDIR="${D}"
    # prefix is used for RPATH and DESTDIR/prefix for installation
    ${STAGING_BINDIR_NATIVE}/scons prefix=${prefix} python_libdir=${libdir} udev-install ${EXTRA_OESCONS} || \
      bbfatal "scons install execution failed."
}

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/gpsd.init ${D}${sysconfdir}/init.d/gpsd
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${S}/packaging/deb/etc_default_gpsd ${D}${sysconfdir}/default/gpsd.default

    # Support for python
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/gps
    install -m 755 ${S}/gps/*.py ${D}${PYTHON_SITEPACKAGES_DIR}/gps
}

PACKAGES =+ "libgps libgpsd python3-pygps gpsd-udev gpsd-conf gpsd-gpsctl gps-utils"

RPROVIDES:${PN}-dbg += "python-pygps-dbg"

FILES:${PN}-dev += "${libdir}/pkgconfdir/libgpsd.pc ${libdir}/pkgconfdir/libgps.pc \
                    ${libdir}/libQgpsmm.prl"

FILES:${PN}-doc += "${datadir}/${BPN}/doc"

RDEPENDS:${PN} = "gpsd-gpsctl"
RRECOMMENDS:${PN} = "gpsd-conf gpsd-udev gpsd-machine-conf"

SUMMARY:gpsd-udev = "udev relevant files to use gpsd hotplugging"
FILES:gpsd-udev = "${nonarch_base_libdir}/udev"
RDEPENDS:gpsd-udev += "udev gpsd-conf"

SUMMARY:libgpsd = "C service library used for communicating with gpsd"
FILES:libgpsd = "${libdir}/libgpsd.so.*"

SUMMARY:libgps = "C service library used for communicating with gpsd"
FILES:libgps = "${libdir}/libgps.so.*"

SUMMARY:gpsd-conf = "gpsd configuration files and init scripts"
FILES:gpsd-conf = "${sysconfdir}"
CONFFILES:gpsd-conf = "${sysconfdir}/default/gpsd.default"

SUMMARY:gpsd-gpsctl = "Tool for tweaking GPS modes"
FILES:gpsd-gpsctl = "${bindir}/gpsctl"

SUMMARY:gps-utils = "Utils used for simulating, monitoring,... a GPS"
# Python files are required for gps/fake, required for gpsfake.
FILES:gps-utils = "${bindir}/* ${libdir}/gps/*.py ${libdir}/gps/*.so"
RDEPENDS:gps-utils = "python3-pygps"

SUMMARY:python3-pygps = "Python bindings to gpsd"
FILES:python3-pygps = "${PYTHON_SITEPACKAGES_DIR}/* ${libdir}/gps/*.py ${libdir}/*.egg-info"
RDEPENDS:python3-pygps = " \
    python3-core \
    python3-io \
    python3-threading \
    python3-terminal \
    gpsd \
    python3-json"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "${BPN}.socket ${BPN}ctl@.service"

ALTERNATIVE:${PN} = "gpsd-defaults"
ALTERNATIVE_LINK_NAME[gpsd-defaults] = "${sysconfdir}/default/gpsd"
ALTERNATIVE_TARGET[gpsd-defaults] = "${sysconfdir}/default/gpsd.default"
