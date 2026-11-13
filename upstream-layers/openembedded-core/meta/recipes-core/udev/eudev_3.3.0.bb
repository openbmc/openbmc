SUMMARY = "eudev is a fork of systemd's udev"
HOMEPAGE = "https://github.com/eudev-project/eudev"
DESCRIPTION = "eudev is Gentoo's fork of udev, systemd's device file manager for the Linux kernel. It manages device nodes in /dev and handles all user space actions when adding or removing devices."
LICENSE = "GPL-2.0-or-later AND LGPL-2.1-or-later"
LICENSE:libudev = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gperf-native"

PROVIDES = "udev"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://init \
           file://local.rules \
           "

SRC_URI[sha256sum] = "8fba6e4f49ea4b1938828a3c2e7cc48874816c68621386e527076b6477f5d6ef"

GITHUB_BASE_URI = "https://github.com/eudev-project/eudev/releases"

inherit autotools bash-completion update-rc.d pkgconfig features_check manpages github-releases

CONFLICT_DISTRO_FEATURES = "systemd"

EXTRA_OECONF = " \
    --sbindir=${base_sbindir} \
    --with-rootlibdir=${base_libdir} \
    --with-rootlibexecdir=${nonarch_base_libdir}/udev \
    --with-rootprefix= \
    --with-nobody-user=nobody \
    --with-nobody-group=nogroup \
    --with-system-uid-max=999 \
    --with-system-gid-max=999 \
    --disable-systemd-hwdb \
    --with-net-ifnames-default=${@bb.utils.contains('DISTRO_FEATURES', 'pni-names', 'yes', 'no', d)} \
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
}

do_install:prepend:class-target () {
	# Remove references to buildmachine
	sed -i -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
		${B}/src/udev/keyboard-keys-from-name.h
}

INITSCRIPT_NAME = "udev"
INITSCRIPT_PARAMS = "start 04 S ."

PACKAGE_BEFORE_PN += "libudev ${PN}-hwdb ${PN}-zsh-completion"

FILES:${PN} += "${nonarch_base_libdir}/udev"
FILES:libudev = "${base_libdir}/libudev.so.*"
FILES:${PN}-hwdb = "${sysconfdir}/udev/hwdb.d ${nonarch_base_libdir}/udev/hwdb.d"
FILES:${PN}-zsh-completion = "${datadir}/zsh"


RDEPENDS:${PN}-hwdb += "eudev"
RDEPENDS:${PN} += "kmod"

RPROVIDES:${PN} = "hotplug udev"
RPROVIDES:${PN}-hwdb += "udev-hwdb"

PACKAGE_WRITE_DEPS += "qemuwrapper-cross"
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
