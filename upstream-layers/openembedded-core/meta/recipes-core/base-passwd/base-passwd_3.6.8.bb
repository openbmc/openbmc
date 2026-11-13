SUMMARY = "Base system master password/group files"
DESCRIPTION = "The master copies of the user database files (/etc/passwd and /etc/group).  The update-passwd tool is also provided to keep the system databases synchronized with these master files."
HOMEPAGE = "https://launchpad.net/base-passwd"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

# We carry a number of extra groups in group.master which can neither go
# upstream nor be created by the recipes which need them:
#
# - Upstream base-passwd only serves Debian and Debian-based systems. Debian
#   policy (9.2.2) restricts base-passwd to IDs which must be statically
#   allocated and identical everywhere; any other group is created by the
#   package which needs it (adduser/systemd-sysusers). Upstream has told us
#   anyone else using base-passwd is on their own:
#   https://salsa.debian.org/debian/base-passwd/-/merge_requests/17
#
# - We can't create them with useradd.bbclass in the recipes which use them.
#   Using useradd also brings shadow's login into the image, which fails with
#   "setgid: Function not implemented" on kernels without CONFIG_MULTIUSER,
#   unlike busybox's login.

SRC_URI = "https://launchpad.net/debian/+archive/primary/+files/${BPN}_${PV}.tar.xz \
           file://0001-Add-a-shutdown-group.patch \
           file://0002-Use-bin-sh-instead-of-bin-bash-for-the-root-user.patch \
           file://0003-Remove-for-root-since-we-do-not-have-an-etc-shadow.patch \
           file://0004-Add-an-input-group-for-the-dev-input-devices.patch \
           file://0005-Add-kvm-group.patch \
           file://0007-Add-wheel-group.patch \
           file://0001-base-passwd-Add-the-sgx-group.patch \
           file://0001-Add-clock-group.patch \
           file://0001-base-passwd-add-the-render-group.patch \
           "

SRC_URI[sha256sum] = "fab3d0e6e8b641e116bda9bd5f7a7ed24482384c1513f6a369b506327fbc8dde"

# the package is taken from launchpad; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/b/base-passwd/"

S = "${UNPACKDIR}/work"

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[selinux] = "--enable-selinux, --disable-selinux, libselinux"

inherit autotools

EXTRA_OECONF += "--disable-debconf --disable-docs"

NOLOGIN ?= "${base_sbindir}/nologin"

do_install () {
	install -d -m 755 ${D}${sbindir}
	install -o root -g root -p -m 755 ${B}/update-passwd ${D}${sbindir}/
	install -d -m 755 ${D}${mandir}/man8 ${D}${mandir}/pl/man8
	install -p -m 644 ${S}/man/update-passwd.8 ${D}${mandir}/man8/
	install -p -m 644 ${S}/man/update-passwd.pl.8 \
		${D}${mandir}/pl/man8/update-passwd.8
	gzip -9 ${D}${mandir}/man8/* ${D}${mandir}/pl/man8/*
	install -d -m 755 ${D}${datadir}/base-passwd
	install -o root -g root -p -m 644 ${S}/passwd.master ${D}${datadir}/base-passwd/
	sed -i 's#:/root:#:${ROOT_HOME}:#' ${D}${datadir}/base-passwd/passwd.master
	sed -i 's#/usr/sbin/nologin#${NOLOGIN}#' ${D}${datadir}/base-passwd/passwd.master
	install -o root -g root -p -m 644 ${S}/group.master ${D}${datadir}/base-passwd/

	install -d -m 755 ${D}${docdir}/${BPN}
	install -p -m 644 ${S}/debian/changelog ${D}${docdir}/${BPN}/
	gzip -9 ${D}${docdir}/${BPN}/*
	install -p -m 644 ${S}/README ${D}${docdir}/${BPN}/
	install -p -m 644 ${S}/debian/copyright ${D}${docdir}/${BPN}/
}

basepasswd_sysroot_postinst() {
#!/bin/sh -e

# Install passwd.master and group.master to sysconfdir
install -d -m 755 ${STAGING_DIR_TARGET}${sysconfdir}
for i in passwd group; do
	install -p -m 644 ${STAGING_DIR_TARGET}${datadir}/base-passwd/\$i.master \
		${STAGING_DIR_TARGET}${sysconfdir}/\$i
done

# Run any useradd postinsts
for script in ${STAGING_DIR_TARGET}${bindir}/postinst-useradd-*; do
	if [ -f \$script ]; then
		\$script
	fi
done
}

SYSROOT_DIRS += "${sysconfdir}"
SYSROOT_PREPROCESS_FUNCS += "base_passwd_tweaksysroot"

base_passwd_tweaksysroot () {
	mkdir -p ${SYSROOT_DESTDIR}${bindir}
	dest=${SYSROOT_DESTDIR}${bindir}/postinst-${PN}
	echo "${basepasswd_sysroot_postinst}" > $dest
	chmod 0755 $dest
}

python populate_packages:prepend() {
    # Add in the preinst function for ${PN}
    # We have to do this here as prior to this, passwd/group.master
    # would be unavailable. We need to create these files at preinst
    # time before the files from the package may be available, hence
    # storing the data from the files in the preinst directly.

    f = open(d.expand("${STAGING_DATADIR}/base-passwd/passwd.master"), 'r')
    passwd = "".join(f.readlines())
    f.close()
    f = open(d.expand("${STAGING_DATADIR}/base-passwd/group.master"), 'r')
    group = "".join(f.readlines())
    f.close()

    preinst = """#!/bin/sh
mkdir -p $D${sysconfdir}
if [ ! -e $D${sysconfdir}/passwd ]; then
\tcat << 'EOF' > $D${sysconfdir}/passwd
""" + passwd + """EOF
fi
if [ ! -e $D${sysconfdir}/group ]; then
\tcat << 'EOF' > $D${sysconfdir}/group
""" + group + """EOF
fi
"""
    d.setVar(d.expand('pkg_preinst:${PN}'), preinst)
}

addtask do_package after do_populate_sysroot

ALLOW_EMPTY:${PN} = "1"

PACKAGES =+ "${PN}-update"
FILES:${PN}-update = "${sbindir}/* ${datadir}/${PN}"

pkg_postinst:${PN}-update () {
#!/bin/sh
if [ -n "$D" ]; then
	exit 0
fi
${sbindir}/update-passwd
}
