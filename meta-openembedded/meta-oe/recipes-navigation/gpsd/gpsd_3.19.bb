SUMMARY = "A TCP/IP Daemon simplifying the communication with GPS devices"
SECTION = "console/network"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=01764c35ae34d9521944bb6ab312af53"
DEPENDS = "dbus ncurses python3 pps-tools"
PROVIDES = "virtual/gpsd"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/${BPN}/${BP}.tar.gz \
    file://0001-SConstruct-prefix-includepy-with-sysroot-and-drop-sy.patch \
    file://0001-gps_shm_close-Free-privdata.patch \
"
SRC_URI[md5sum] = "b3bf88706794eb8e5f2c2543bf7ba87b"
SRC_URI[sha256sum] = "27dd24d45b2ac69baab7933da2bf6ae5fb0be90130f67e753c110a3477155f39"

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
    manbuild='false' \
    ${PACKAGECONFIG_CONFARGS} \
"
# this cannot be used, because then chrpath is not found and only static lib is built
# target=${HOST_SYS}

do_compile_prepend() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export STAGING_PREFIX="${STAGING_DIR_HOST}/${prefix}"
    export LINKFLAGS="${LDFLAGS}"
}

do_install() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export STAGING_PREFIX="${STAGING_DIR_HOST}/${prefix}"
    export LINKFLAGS="${LDFLAGS}"

    export DESTDIR="${D}"
    # prefix is used for RPATH and DESTDIR/prefix for instalation
    ${STAGING_BINDIR_NATIVE}/scons prefix=${prefix} python_libdir=${libdir} install ${EXTRA_OESCONS} || \
      bbfatal "scons install execution failed."
}

do_install_append() {
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${S}/packaging/deb/etc_init.d_gpsd ${D}/${sysconfdir}/init.d/gpsd
    install -d ${D}/${sysconfdir}/default
    install -m 0644 ${S}/packaging/deb/etc_default_gpsd ${D}/${sysconfdir}/default/gpsd.default

    #support for udev
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/gpsd.rules ${D}/${sysconfdir}/udev/rules.d/
    install -d ${D}${base_libdir}/udev/
    install -m 0755 ${S}/gpsd.hotplug ${D}${base_libdir}/udev/

    #support for python
    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}/gps
    install -m 755 ${S}/gps/*.py ${D}/${PYTHON_SITEPACKAGES_DIR}/gps

    #support for systemd
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/systemd/${BPN}.service ${D}${systemd_unitdir}/system/${BPN}.service
    sed -i -e 's,/usr/local,/usr,g' ${D}${systemd_unitdir}/system/${BPN}.service
    install -m 0644 ${S}/systemd/${BPN}ctl@.service ${D}${systemd_unitdir}/system/${BPN}ctl@.service
    sed -i -e 's,/usr/local,/usr,g' ${D}${systemd_unitdir}/system/${BPN}ctl@.service
    install -m 0644 ${S}/systemd/${BPN}.socket ${D}${systemd_unitdir}/system/${BPN}.socket
}

PACKAGES =+ "libgps libgpsd python3-pygps gpsd-udev gpsd-conf gpsd-gpsctl gps-utils"

RPROVIDES_${PN}-dbg += "python-pygps-dbg"

FILES_${PN}-dev += "${libdir}/pkgconfdir/libgpsd.pc ${libdir}/pkgconfdir/libgps.pc \
                    ${libdir}/libQgpsmm.prl"

RDEPENDS_${PN} = "gpsd-gpsctl"
RRECOMMENDS_${PN} = "gpsd-conf gpsd-udev gpsd-machine-conf"

SUMMARY_gpsd-udev = "udev relevant files to use gpsd hotplugging"
FILES_gpsd-udev = "${base_libdir}/udev ${sysconfdir}/udev/*"
RDEPENDS_gpsd-udev += "udev gpsd-conf"

SUMMARY_libgpsd = "C service library used for communicating with gpsd"
FILES_libgpsd = "${libdir}/libgpsd.so.*"

SUMMARY_libgps = "C service library used for communicating with gpsd"
FILES_libgps = "${libdir}/libgps.so.*"

SUMMARY_gpsd-conf = "gpsd configuration files and init scripts"
FILES_gpsd-conf = "${sysconfdir}"
CONFFILES_gpsd-conf = "${sysconfdir}/default/gpsd.default"

SUMMARY_gpsd-gpsctl = "Tool for tweaking GPS modes"
FILES_gpsd-gpsctl = "${bindir}/gpsctl"

SUMMARY_gps-utils = "Utils used for simulating, monitoring,... a GPS"
# Python files are required for gps/fake, required for gpsfake.
FILES_gps-utils = "${bindir}/* ${libdir}/gps/*.py ${libdir}/gps/*.so"
RDEPENDS_gps-utils = "python3-pygps"

SUMMARY_python3-pygps = "Python bindings to gpsd"
FILES_python3-pygps = "${PYTHON_SITEPACKAGES_DIR}/* ${libdir}/gps/*.py ${libdir}/*.egg-info"
RDEPENDS_python3-pygps = " \
    python3-core \
    python3-io \
    python3-threading \
    python3-terminal \
    gpsd \
    python3-json"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${BPN}.socket ${BPN}ctl@.service"


ALTERNATIVE_${PN} = "gpsd-defaults"
ALTERNATIVE_LINK_NAME[gpsd-defaults] = "${sysconfdir}/default/gpsd"
ALTERNATIVE_TARGET[gpsd-defaults] = "${sysconfdir}/default/gpsd.default"
