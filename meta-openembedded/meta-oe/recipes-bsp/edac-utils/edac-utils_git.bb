SUMMARY = "Userspace helper for Linux kernel EDAC drivers"
HOMEPAGE = "https://github.com/grondo/edac-utils"
DESCRIPTION = "EDAC (Error Detection and Correction) is a set of Linux kernel \
modules that handle reporting of hardware-related errors. Currently \
these modules mainly handle detection of ECC memory errors for many \
x86 and x86-64 chipsets and PCI bus parity errors."
SECTION = "Applications/System"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " sysfsutils"

SRCREV = "1c57818ecee186aa47af0342149577df5302c022"
PV = "0.18+git"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/grondo/edac-utils;branch=master;protocol=https \
    file://make-init-script-be-able-to-automatically-load-EDAC-.patch \
    file://add-restart-to-initscript.patch \
    file://edac.service \
"

inherit autotools-brokensep systemd

do_configure:prepend () {
    touch ${S}/ChangeLog
    ${S}/bootstrap
}

RDEPENDS:${PN}:x86 = "dmidecode"
RDEPENDS:${PN}:x86-64 = "dmidecode"
RDEPENDS:${PN}:arm = "dmidecode"
RDEPENDS:${PN}:aarch64 = "dmidecode"
RDEPENDS:${PN}:powerpc = "dmidecode"
RDEPENDS:${PN}:powerpc64 = "dmidecode"
RDEPENDS:${PN}:powerpc64le = "dmidecode"
RDEPENDS:${PN}:append = " \
    perl \
    perl-module-file-basename \
    perl-module-file-find \
    perl-module-getopt-long \
    perl-module-posix \
    perl-module-overload \
    perl-module-overloading \
    perl-module-file-glob \
"

do_install:append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 644 ${WORKDIR}/edac.service ${D}/${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}/${systemd_unitdir}/system/edac.service
}

SYSTEMD_SERVICE:${PN} = "edac.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"
