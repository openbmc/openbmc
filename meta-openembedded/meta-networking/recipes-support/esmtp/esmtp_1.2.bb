SUMMARY = "User configurable send-only Mail Transfer Agent"
DESCRIPTION = "ESMTP is a user-configurable relay-only MTA \
with a sendmail-compatible syntax, based on libESMTP and \
supporting the AUTH (including the CRAM-MD5 and NTLM SASL \
mechanisms) and StartTLS SMTP extensions."
HOMEPAGE = "http://esmtp.sourceforge.net/"
SECTION = "net"

DEPENDS = "libesmtp"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${PV}/${BPN}-${PV}.tar.bz2"

# Have to set this or we get -L/lib in LDFLAGS
EXTRA_OECONF = "--with-libesmtp=${STAGING_EXECPREFIXDIR}"

inherit autotools update-alternatives

ALTERNATIVE_${PN} += "sendmail mailq newaliases"
ALTERNATIVE_TARGET[mailq] = "${bindir}/mailq"
ALTERNATIVE_TARGET[newaliases] = "${bindir}/newaliases"
ALTERNATIVE_LINK_NAME[sendmail] = "${sbindir}/sendmail"
ALTERNATIVE_TARGET[sendmail] = "${bindir}/esmtp"

ALTERNATIVE_PRIORITY = "10"

ALTERNATIVE_${PN}-doc += "mailq.1 newaliases.1 sendmail.1"
ALTERNATIVE_LINK_NAME[mailq.1] = "${mandir}/man1/mailq.1"
ALTERNATIVE_LINK_NAME[newaliases.1] = "${mandir}/man1/newaliases.1"
ALTERNATIVE_LINK_NAME[sendmail.1] = "${mandir}/man1/sendmail.1"

SRC_URI[md5sum] = "79a9c1f9023d53f35bb82bf446150a72"
SRC_URI[sha256sum] = "a0d26931bf731f97514da266d079d8bc7d73c65b3499ed080576ab606b21c0ce"

do_install_append() {
    # only one file /usr/lib/sendmail in ${D}${libdir}
    rm -rf ${D}${libdir}
}

pkg_postinst_${PN}_linuxstdbase () {
    # /usr/lib/sendmial is required by LSB core test
    [ ! -L $D/usr/lib/sendmail ] && ln -sf ${sbindir}/sendmail $D/usr/lib/
}

FILES_${PN} += "${libdir}/"
