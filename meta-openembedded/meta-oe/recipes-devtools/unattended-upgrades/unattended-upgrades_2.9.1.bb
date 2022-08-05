SUMMARY = "This script upgrades packages automatically and unattended."
DESCRIPTION = "The purpose of unattended-upgrades is to keep the computer current with the latest security (and other) updates automatically."
HOMEPAGE = "https://wiki.debian.org/UnattendedUpgrades"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://debian/copyright;md5=62b5f2ac0ede901fb245eefbe54c181f"

SRC_URI = "git://github.com/mvo5/unattended-upgrades.git;protocol=https;branch=master \
           file://0001-unattended-upgrade-Remove-distro_info-usage-to-check.patch \
           file://0001-setup.py-Disable-autodection-of-modules.patch \
           "

SRCREV = "5aff8fa2b5b60b7c11bbfb39c884477e72d11d02"

S = "${WORKDIR}/git"

inherit setuptools3_legacy

DEPENDS += "apt intltool-native python3-distutils-extra-native"
RDEPENDS:${PN} += "apt lsb-release python3-apt python3-core python3-datetime python3-email python3-fcntl python3-io python3-logging python3-stringold python3-syslog"

do_configure:prepend () {
	install -Dm 0644 ${S}/data/50unattended-upgrades.Debian ${S}/data/50unattended-upgrades
}

do_install:append () {
	# fix bad installation path's
	mv -v ${D}/usr/usr/share/* ${D}/usr/share/
	rm -r ${D}/usr/usr
}

FILES:${PN} = "${bindir} ${exec_prefix}/etc ${libdir} ${datadir} ${nonarch_libdir}"
