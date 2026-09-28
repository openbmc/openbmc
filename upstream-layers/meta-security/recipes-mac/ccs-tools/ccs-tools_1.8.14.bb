SUMMARY = "Tomoyo"
DESCRIPTION = "TOMOYO Linux is a Mandatory Access Control (MAC) implementation for Linux that can be used to increase the security of a system, while also being useful purely as a system analysis tool. \nTo start via command line add: \nsecurity=tomoyo TOMOYO_trigger=/usr/lib/systemd/systemd \nTo initialize: \n/usr/lib/ccs/init_policy"

SECTION = "security"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.ccs;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "ncurses"

DS = "20250707"
SRC_URI = "https://sourceforge.net/projects/tomoyo/files/ccs-tools/1.8/${BPN}-${PV}-${DS}.tar.gz"

SRC_URI[sha256sum] = "b62e344cc357a982310a08760f2a2cebf4cf48b1341ea9bd3990ae55f45bab03"

S = "${UNPACKDIR}/${BPN}"

inherit features_check

do_make(){
    oe_runmake USRLIBDIR=${libdir} all
    cd ${S}/kernel_test
    oe_runmake  all
}

do_install(){
    oe_runmake INSTALLDIR=${D}  USRLIBDIR=${libdir} SBINDIR=${sbindir} install
}

PACKAGES = "${PN} ${PN}-dbg ${PN}-doc"

FILES:${PN} = "\
    ${sbindir}/* \
    ${base_sbindir}/* \
    ${libdir}/* \
"

FILES:${PN}-doc = "\
    ${mandir}/man8/* \
"

FILES:${PN}-dbg = "\
    ${base_sbindir}/.debug/* \
    ${sbindir}/.debug/* \
    ${libdir}/.debug/* \
    ${libdir}/ccs/.debug/* \
    /usr/src/debug/* \
"

REQUIRED_DISTRO_FEATURES ?= " tomoyo"
