SUMMARY = "Tomoyo"
DESCRIPTION = "TOMOYO Linux is a Mandatory Access Control (MAC) implementation for Linux that can be used to increase the security of a system, while also being useful purely as a system analysis tool. \nTo start via command line add: \nsecurity=tomoyo TOMOYO_trigger=/usr/lib/systemd/systemd \nTo initialize: \n/usr/lib/ccs/init_policy"

SECTION = "security"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING.ccs;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "ncurses"

DS = "20150505"
SRC_URI = "http://osdn.dl.sourceforge.jp/tomoyo/49693/${BPN}-${PV}-${DS}.tar.gz"

SRC_URI[md5sum] = "eeee8eb96a7680bfa9c8f6de55502c44"
SRC_URI[sha256sum] = "c358b80a2ea77a9dda79dc2a056dae3acaf3a72fcb8481cfb1cd1f16746324b4"

S = "${WORKDIR}/${BPN}"

inherit features_check

do_make(){
    oe_runmake USRLIBDIR=${libdir} all
    cd ${S}/kernel_test
    oe_runmake  all
}

do_install(){
    oe_runmake INSTALLDIR=${D}  USRLIBDIR=${libdir} install
}

PACKAGE="${PN} ${PN}-dbg ${PN}-doc"

FILES_${PN} = "\
    ${sbindir}/* \
    ${base_sbindir}/* \
    ${libdir}/* \
"

FILES_${PN}-doc = "\
    ${mandir}/man8/* \
"

FILES_${PN}-dbg = "\
    ${base_sbindir}/.debug/* \
    ${sbindir}/.debug/* \
    ${libdir}/.debug/* \
    ${libdir}/ccs/.debug/* \
    /usr/src/debug/* \
"

REQUIRED_DISTRO_FEATURES ?=" tomoyo"
