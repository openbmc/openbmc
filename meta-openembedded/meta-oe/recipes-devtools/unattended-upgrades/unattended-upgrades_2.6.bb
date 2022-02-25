SUMMARY = "This script upgrades packages automatically and unattended."
DESCRIPTION = "The purpose of unattended-upgrades is to keep the computer current with the latest security (and other) updates automatically."
HOMEPAGE = "https://wiki.debian.org/UnattendedUpgrades"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://debian/copyright;md5=62b5f2ac0ede901fb245eefbe54c181f"

SRC_URI = "git://github.com/mvo5/unattended-upgrades.git;protocol=https;branch=master \
           file://0001-unattended-upgrade-Remove-distro_info-usage-to-check.patch \
           "

SRCREV = "c6db6fad26a2b83ba301b52ff5dee98cef7558ca"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "apt intltool-native python3-distutils-extra-native"
RDEPENDS:${PN} += "apt lsb-release python3-apt python3-core python3-datetime python3-email python3-fcntl python3-io python3-logging python3-stringold python3-syslog"

do_install:prepend () {
	cp -v ${S}/data/50unattended-upgrades.Debian ${S}/data/50unattended-upgrades
}

do_install:append () {
	# fix bad installation path's
	mv -v ${D}/usr/usr/share/* ${D}/usr/share/
	rm -r ${D}/usr/usr
}

FILES:${PN} = "${bindir} ${exec_prefix}/etc ${libdir} ${datadir}"
