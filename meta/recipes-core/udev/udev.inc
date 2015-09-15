SUMMARY = "/dev/ and hotplug management daemon"
DESCRIPTION = "udev is a daemon which dynamically creates and removes device nodes from \
/dev/, handles hotplug events and loads drivers at boot time."
HOMEPAGE = "http://www.kernel.org/pub/linux/utils/kernel/hotplug/udev/udev.html"
LICENSE = "GPLv2.0+ & LGPLv2.1+"
LICENSE_${PN} = "GPLv2.0+"
LICENSE_libudev = "LGPLv2.1+"
LICENSE_libgudev = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/COPYING;md5=17c4e5fb495e6707ac92a3864926f979 \
                    file://src/gudev/COPYING;md5=fb494485a7d0505308cb68e4997cc266"

LDFLAGS += "-lrt"

DEPENDS = "glib-2.0 libusb usbutils pciutils glib-2.0-native gperf-native libxslt-native util-linux"
RPROVIDES_${PN} = "hotplug"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/kernel/hotplug/udev-${PV}.tar.gz \
           file://0001-Fixing-keyboard_force_release.sh-shell-script-path.patch \
           file://avoid-mouse-autosuspend.patch \
           file://run.rules \
           file://udev.rules \
           file://devfs-udev.rules \
           file://links.conf \
           file://permissions.rules \
           file://local.rules \
           file://udev-cache \
           file://udev-cache.default \
           file://add-install-ptest.patch \
           file://fix_rule-syntax-regex-ptest.patch \
           file://run-ptest \
           file://init"

inherit autotools pkgconfig update-rc.d ptest
RDEPENDS_${PN}-ptest += "make perl python"

libexecdir = "${base_libdir}"
EXTRA_OECONF = "--disable-introspection \
                --with-rootlibdir=${base_libdir} \
                --with-pci-ids-path=${datadir}/pci.ids \
                ac_cv_file__usr_share_pci_ids=no \
                ac_cv_file__usr_share_hwdata_pci_ids=no \
                ac_cv_file__usr_share_misc_pci_ids=yes \
                --sbindir=${base_sbindir} \
                --libexecdir=${nonarch_base_libdir} \
                --with-rootlibdir=${base_libdir} \
                --with-rootprefix= \
                --without-systemdsystemunitdir \
               "

PACKAGES =+ "udev-cache"
PACKAGES =+ "libudev"
PACKAGES =+ "libgudev"

INITSCRIPT_PACKAGES = "udev udev-cache"
INITSCRIPT_NAME_udev = "udev"
INITSCRIPT_PARAMS_udev = "start 04 S ."
INITSCRIPT_NAME_udev-cache = "udev-cache"
INITSCRIPT_PARAMS_udev-cache = "start 36 S ."

FILES_${PN} += "${libexecdir} ${libdir}/ConsoleKit ${nonarch_base_libdir}/udev ${bindir}/udevadm"
RRECOMMENDS_${PN} += "udev-cache"

# udev-utils has been removed as a package.  Note that udev conflicts with udev-utils so that
# udev-utils is removed from systems on upgrade.
RCONFLICTS_${PN} += "udev-utils"
RREPLACES_${PN} += "udev-utils"

FILES_${PN}-dbg += "${libexecdir}/.debug"
FILES_${PN}-dbg += "${base_libdir}/udev/.debug/"
FILES_${PN}-dbg += "${base_libdir}/udev/.debug/*"
FILES_${PN}-dbg += "${nonarch_base_libdir}/udev/.debug/*"
FILES_${PN}-dev = "${datadir}/pkgconfig/udev.pc \
                   ${includedir}/libudev.h ${libdir}/libudev.so ${libdir}/libudev.la \
                   ${libdir}/libudev.a ${libdir}/pkgconfig/libudev.pc \
                   ${includedir}/gudev* ${libdir}/libgudev*.so ${libdir}/libgudev*.la \
                   ${libdir}/libgudev*.a ${libdir}/pkgconfig/gudev*.pc"
FILES_libudev = "${base_libdir}/libudev.so.*"
FILES_libgudev = "${base_libdir}/libgudev*.so.* ${libdir}/libgudev*.so.*"
FILES_udev-cache = "${sysconfdir}/init.d/udev-cache ${sysconfdir}/default/udev-cache"

do_install_append () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/udev
	install -m 0755 ${WORKDIR}/udev-cache ${D}${sysconfdir}/init.d/udev-cache
	sed -i s%@UDEVD@%${nonarch_base_libdir}/udev/udevd% ${D}${sysconfdir}/init.d/udev
	sed -i s%@UDEVD@%${nonarch_base_libdir}/udev/udevd% ${D}${sysconfdir}/init.d/udev-cache

	install -d ${D}${sysconfdir}/default
	install -m 0755 ${WORKDIR}/udev-cache.default ${D}${sysconfdir}/default/udev-cache

	touch ${D}${sysconfdir}/udev/cache.data

	install -d ${D}${sysconfdir}/udev/rules.d/

	install -m 0644 ${WORKDIR}/local.rules         ${D}${sysconfdir}/udev/rules.d/local.rules

	# hid2hci has moved to bluez4. removed in udev as of version 169
	rm -f ${D}${base_libdir}/udev/hid2hci

	echo 'udev_run="/var/run/udev"' >> ${D}${sysconfdir}/udev/udev.conf
}

# As systemd also builds udev, skip this package if we're doing a systemd build.
python () {
    if bb.utils.contains ('DISTRO_FEATURES', 'systemd', True, False, d):
        raise bb.parse.SkipPackage("'systemd' in DISTRO_FEATURES")
}
do_compile_ptest() {
    oe_runmake test-udev
}
