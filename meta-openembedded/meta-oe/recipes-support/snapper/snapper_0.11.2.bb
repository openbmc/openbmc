SUMMARY = "Snapper is a tool for Linux file system snapshot management"
HOMEPAGE = "https://github.com/openSUSE/snapper"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "acl boost btrfs-tools dbus e2fsprogs json-c libxml2 lvm2 ncurses zlib"

# Build separation is slightly broken
inherit autotools-brokensep pkgconfig gettext

SRC_URI = " \
    git://github.com/openSUSE/snapper.git;protocol=https;branch=master \
    file://0001-Include-linux-types.h-for-__u16-__u32-__u64-type.patch \
    file://0002-Use-statvfs-instead-of-statvfs64.patch \
"
SRCREV = "6c603565f36e9996d85045c8012cd04aba5f3708"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--disable-zypp --with-pam-security=${libdir}/security"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'api-documentation systemd pam', d)}"
PACKAGECONFIG[pam] = "--enable-pam,--disable-pam,libpam"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd"
PACKAGECONFIG[api-documentation] = "--enable-doc,--disable-doc,libxslt-native docbook-xsl-stylesheets-native"

# Avoid HOSTTOOLS path in binaries
export DIFFBIN = "${bindir}/diff"
export RMBIN = "${bindir}/rm"
export TOUCHBIN = "${bindir}/touch"
export CPBIN = "${bindir}/cp"

do_install:append() {
	install -d ${D}${sysconfdir}/sysconfig
	install -m0644 ${S}/data/default-config ${D}${sysconfdir}/sysconfig/snapper
}

FILES:${PN} += "${libdir}/security ${nonarch_libdir} ${systemd_system_unitdir} ${datadir}"

# bash is needed for the testsuite
RDEPENDS:${PN} = "bash diffutils util-linux util-linux-mount"
