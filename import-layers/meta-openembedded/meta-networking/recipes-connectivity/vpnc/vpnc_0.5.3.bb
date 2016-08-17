SUMMARY = "A client for the Cisco3000 VPN Concentrator"
HOMEPAGE = "http://www.unix-ag.uni-kl.de/~massar/vpnc/"
AUTHOR = "Maurice Massar vpnc@unix-ag.uni-kl.de"
SECTION = "net"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=173b74cb8ac640a9992c03f3bce22a33"
DEPENDS = "libgcrypt"

inherit perlnative

EXTRA_OEMAKE = "-e MAKEFLAGS="
CFLAGS_append = ' -DVERSION=\\"${PV}\\"'
LDFLAGS_append = " -lgcrypt -lgpg-error"

do_configure_append () {
    # Make sure we use our nativeperl wrapper
    sed -i "1s:#!.*:#!/usr/bin/env nativeperl:" ${S}/*.pl
}

do_install () {
    sed -i s:m600:m\ 600:g Makefile    
    oe_runmake 'DESTDIR=${D}' 'PREFIX=/usr' install
    rm -f ${D}${sysconfdir}/vpnc/vpnc.conf #This file is useless
    install ${WORKDIR}/default.conf ${D}${sysconfdir}/vpnc/default.conf
}

SYSROOT_PREPROCESS_FUNCS += "vpnc_sysroot_preprocess"

vpnc_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${sysconfdir}/vpnc
    install -m 755 ${D}${sysconfdir}/vpnc/vpnc-script ${SYSROOT_DESTDIR}${sysconfdir}/vpnc
}

CONFFILES_${PN} = "${sysconfdir}/vpnc/default.conf"
RDEPENDS_${PN} = "perl-module-io-file"
RRECOMMENDS_${PN} = "kernel-module-tun"

SRC_URI = "http://www.unix-ag.uni-kl.de/~massar/vpnc/vpnc-${PV}.tar.gz \
           file://makeman.patch \
           file://vpnc-install.patch \
           file://long-help \
           file://default.conf"

SRC_URI[md5sum] = "4378f9551d5b077e1770bbe09995afb3"
SRC_URI[sha256sum] = "46cea3bd02f207c62c7c6f2f22133382602baeda1dc320747809e94881414884"
