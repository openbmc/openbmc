SUMMARY = "A TCP/IP Daemon simplifying the communication with GPS devices"
SECTION = "console/network"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d217a23f408e91c94359447735bc1800"
DEPENDS = "dbus dbus-glib ncurses python libusb1 chrpath-replacement-native pps-tools"
PROVIDES = "virtual/gpsd"

EXTRANATIVEPATH += "chrpath-native"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/${BPN}/${BP}.tar.gz \
    file://0001-SConstruct-respect-sysroot-also-in-SPLINTOPTS.patch \
    file://0002-SConstruct-remove-rpath.patch \
    file://0001-SConstruct-prefix-includepy-with-sysroot-and-drop-sy.patch \
    file://0004-SConstruct-disable-html-and-man-docs-building-becaus.patch \
    file://0001-Check-for-__STDC_NO_ATOMICS__-before-using-stdatomic.patch \
    file://0002-Add-a-test-for-C11-and-check-we-have-C11-before-usin.patch \
    file://0003-Whoops-check-for-C11-not-for-not-C11-in-stdatomic.h-.patch \
    file://0001-Include-stdatomic.h-only-in-C-mode.patch \
    file://0001-libgpsd-core-Fix-issue-with-ACTIVATE-hook-not-being-.patch \
    file://gpsd-default \
    file://gpsd \
    file://60-gpsd.rules \
    file://gpsd.service \
"
SRC_URI[md5sum] = "bc7467009b99e07ba461377b5da6c039"
SRC_URI[sha256sum] = "504fc812f3c1525a1a48e04bf4d77f9a8066c201448d98089df89d58ef53a8cb"

inherit scons update-rc.d python-dir pythonnative systemd bluetooth

INITSCRIPT_NAME = "gpsd"
INITSCRIPT_PARAMS = "defaults 35"

SYSTEMD_OESCONS = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false',d)}"

export STAGING_INCDIR
export STAGING_LIBDIR

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)}"
PACKAGECONFIG[bluez] = "bluez='true',bluez='false',${BLUEZ}"
PACKAGECONFIG[qt] = "qt='yes',qt='no',qt4-x11-free"
EXTRA_OESCONS = " \
    sysroot=${STAGING_DIR_TARGET} \
    libQgpsmm='false' \
    debug='true' \
    strip='false' \
    chrpath='yes' \
    systemd='${SYSTEMD_OESCONS}' \
    ${EXTRA_OECONF} \
"
DISABLE_STATIC = ""
# this cannot be used, because then chrpath is not found and only static lib is built
# target=${HOST_SYS}

do_compile_prepend() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export STAGING_PREFIX="${STAGING_DIR_HOST}/${prefix}"

    export BUILD_SYS="${BUILD_SYS}"
    export HOST_SYS="${HOST_SYS}"
}

do_install() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export STAGING_PREFIX="${STAGING_DIR_HOST}/${prefix}"

    export BUILD_SYS="${BUILD_SYS}"
    export HOST_SYS="${HOST_SYS}"

    export DESTDIR="${D}"
    # prefix is used for RPATH and DESTDIR/prefix for instalation
    ${STAGING_BINDIR_NATIVE}/scons prefix=${prefix} install ${EXTRA_OESCONS}|| \
      bbfatal "scons install execution failed."
}

do_install_append() {
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/gpsd ${D}/${sysconfdir}/init.d/
    install -d ${D}/${sysconfdir}/default
    install -m 0644 ${WORKDIR}/gpsd-default ${D}/${sysconfdir}/default/gpsd.default

    #support for udev
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/60-gpsd.rules ${D}/${sysconfdir}/udev/rules.d
    install -d ${D}${base_libdir}/udev/
    install -m 0755 ${S}/gpsd.hotplug ${D}${base_libdir}/udev/

    #support for python
    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}/gps
    install -m 755 ${S}/gps/*.py ${D}/${PYTHON_SITEPACKAGES_DIR}/gps

    #support for systemd
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/${BPN}.service ${D}${systemd_unitdir}/system/${BPN}.service
    install -m 0644 ${S}/systemd/${BPN}.socket ${D}${systemd_unitdir}/system/${BPN}.socket
}

pkg_postinst_${PN}-conf() {
    update-alternatives --install ${sysconfdir}/default/gpsd gpsd-defaults ${sysconfdir}/default/gpsd.default 10
}

pkg_postrm_${PN}-conf() {
    update-alternatives --remove gpsd-defaults ${sysconfdir}/default/gpsd.default
}

PACKAGES =+ "libgps libgpsd python-pygps-dbg python-pygps gpsd-udev gpsd-conf gpsd-gpsctl gps-utils"

FILES_${PN}-dev += "${libdir}/pkgconfdir/libgpsd.pc ${libdir}/pkgconfdir/libgps.pc \
                    ${libdir}/libQgpsmm.prl"

FILES_python-pygps-dbg += " ${libdir}/python*/site-packages/gps/.debug"

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
FILES_gps-utils = "${bindir}/*"
RDEPENDS_gps-utils = "python-pygps"

SUMMARY_python-pygps = "Python bindings to gpsd"
FILES_python-pygps = "${PYTHON_SITEPACKAGES_DIR}/*"
RDEPENDS_python-pygps = "python-core python-curses gpsd python-json"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${PN}.socket"
