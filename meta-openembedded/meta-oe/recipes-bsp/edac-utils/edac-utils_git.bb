SUMMARY = "Userspace helper for Linux kernel EDAC drivers"
HOMEPAGE = "https://github.com/grondo/edac-utils"
SECTION = "Applications/System"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = " sysfsutils"

SRCREV = "f9aa96205f610de39a79ff43c7478b7ef02e3138"
PV = "0.18+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/grondo/edac-utils \
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
