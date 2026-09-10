SUMMARY = "extremely simple MTA to get mail off the system to a mail hub"
HOMEPAGE = "http://packages.qa.debian.org/s/ssmtp.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c56db0143f4f80c369ee3af7425af6e"

SRC_URI = "${DEBIAN_MIRROR}/main/s/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://build-ouside_srcdir.patch \
           file://use-DESTDIR.patch \
           file://0001-include-libgen.h-for-basename.patch \
           file://0001-Fix-incompatible-pointer-types-error-with-gcc-15.patch \
           "

SRC_URI[md5sum] = "f9894703fcc559f63cffacfea08f9b75"
SRC_URI[sha256sum] = "6e639eafce4c8a3b7782c2a8a2d467be097c5896eae86fcde0f608666ea88249"

inherit autotools update-alternatives

PACKAGECONFIG ?= "ssl ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[ssl] = "--enable-ssl,--disable-ssl,openssl"
PACKAGECONFIG[ipv6] = "--enable-inet6,--disable-inet6"

EXTRA_OECONF += "--mandir=${mandir}"

EXTRA_OEMAKE = "GEN_CONFIG='/bin/true'"

LDFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'ssl', '-lssl -lcrypto', '', d)}"

do_install:append () {
    install -d ${D}${mandir}/
    mv ${D}${exec_prefix}/man/* ${D}${mandir}/
    rmdir ${D}${exec_prefix}/man
    ln -s ssmtp ${D}${sbindir}/sendmail
    ln -s ssmtp ${D}${sbindir}/newaliases
    ln -s ssmtp ${D}${sbindir}/mailq
}

ALTERNATIVE_PRIORITY_${PN} = "100"

ALTERNATIVE:${PN} = "mailq newaliases sendmail"
ALTERNATIVE_LINK_NAME[sendmail] = "${sbindir}/sendmail"
ALTERNATIVE_LINK_NAME[newaliases] = "${sbindir}/newaliases"
ALTERNATIVE_LINK_NAME[mailq] = "${sbindir}/mailq"
