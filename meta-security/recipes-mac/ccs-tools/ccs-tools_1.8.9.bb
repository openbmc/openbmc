SUMMARY = "Tomoyo"
DESCRIPTION = "TOMOYO Linux is a Mandatory Access Control (MAC) implementation for Linux that can be used to increase the security of a system, while also being useful purely as a system analysis tool. \nTo start via command line add: \nsecurity=tomoyo TOMOYO_trigger=/usr/lib/systemd/systemd \nTo initialize: \n/usr/lib/ccs/init_policy"

SECTION = "security"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.ccs;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "ncurses"

DS = "20210910"
SRC_URI = "http://osdn.dl.sourceforge.jp/tomoyo/49693/${BPN}-${PV}-${DS}.tar.gz"

SRC_URI[sha256sum] = "7900126cf2dd8706c42c2c1ef7a37fd8b50f1505abd7d9c3d653dc390fb4d620"

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

PACKAGE="${PN} ${PN}-dbg ${PN}-doc"

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

REQUIRED_DISTRO_FEATURES ?=" tomoyo"
