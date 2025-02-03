SUMMARY = "eudev is a fork of systemd's udev"
HOMEPAGE = "https://github.com/eudev-project/eudev"
DESCRIPTION = "eudev is Gentoo's fork of udev, systemd's device file manager for the Linux kernel. It manages device nodes in /dev and handles all user space actions when adding or removing devices."
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:libudev = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gperf-native"

PROVIDES = "udev"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://netifnames.patch \
           file://init \
           file://local.rules \
           file://0001-random-util.c-sync-dev_urandom-implementation-to-sys.patch \
           "

SRC_URI[sha256sum] = "8da4319102f24abbf7fff5ce9c416af848df163b29590e666d334cc1927f006f"

GITHUB_BASE_URI = "https://github.com/eudev-project/eudev/releases"

inherit autotools update-rc.d qemu pkgconfig features_check manpages github-releases

CONFLICT_DISTRO_FEATURES = "systemd"

EXTRA_OECONF = " \
    --sbindir=${base_sbindir} \
    --with-rootlibdir=${base_libdir} \
    --with-rootlibexecdir=${nonarch_base_libdir}/udev \
    --with-rootprefix= \
"

PACKAGECONFIG ?= "blkid hwdb kmod \
                  ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)} \
"
PACKAGECONFIG[blkid] = "--enable-blkid,--disable-blkid,util-linux"
PACKAGECONFIG[hwdb] = "--enable-hwdb,--disable-hwdb"
PACKAGECONFIG[kmod] = "--enable-kmod,--disable-kmod,kmod"
PACKAGECONFIG[manpages] = "--enable-manpages,--disable-manpages"
PACKAGECONFIG[rule-generator] = "--enable-rule-generator,--disable-rule-generator"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"

do_install:append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${UNPACKDIR}/init ${D}${sysconfdir}/init.d/udev
	sed -i s%@UDEVD@%${base_sbindir}/udevd% ${D}${sysconfdir}/init.d/udev
	sed -i s%@KMOD@%${base_bindir}/kmod% ${D}${sysconfdir}/init.d/udev

	install -d ${D}${sysconfdir}/udev/rules.d
	install -m 0644 ${UNPACKDIR}/local.rules ${D}${sysconfdir}/udev/rules.d/local.rules

	# Use classic network interface naming scheme if no 'pni-names' distro feature
	if ${@bb.utils.contains('DISTRO_FEATURES', 'pni-names', 'false', 'true', d)}; then
		touch ${D}${sysconfdir}/udev/rules.d/80-net-name-slot.rules
	fi
}

do_install:prepend:class-target () {
	# Remove references to buildmachine
	sed -i -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
		${B}/src/udev/keyboard-keys-from-name.h
}

INITSCRIPT_NAME = "udev"
INITSCRIPT_PARAMS = "start 04 S ."

PACKAGE_BEFORE_PN = "libudev ${PN}-hwdb"

FILES:${PN} += "${nonarch_base_libdir}/udev"
FILES:libudev = "${base_libdir}/libudev.so.*"
FILES:${PN}-hwdb = "${sysconfdir}/udev/hwdb.d"

RDEPENDS:${PN}-hwdb += "eudev"
RDEPENDS:${PN} += "kmod"

RPROVIDES:${PN} = "hotplug udev"
RPROVIDES:${PN}-hwdb += "udev-hwdb"

PACKAGE_WRITE_DEPS += "qemu-native"
pkg_postinst:${PN}-hwdb () {
	if test -n "$D"; then
		$INTERCEPT_DIR/postinst_intercept update_udev_hwdb ${PKG} mlprefix=${MLPREFIX} binprefix=${MLPREFIX}
	else
		udevadm hwdb --update
	fi
}

pkg_prerm:${PN}-hwdb () {
	rm -f $D${sysconfdir}/udev/hwdb.bin
}
