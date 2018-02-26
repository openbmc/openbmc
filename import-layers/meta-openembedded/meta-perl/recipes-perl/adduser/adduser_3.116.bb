SUMMARY = "a utility to add users/groups to the system"
DESCRIPTION = "adduser, addgroup - add a user or group to the system"
HOMEPAGE = "http://alioth.debian.org/projects/adduser/"
SECTION = "base/utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=caed49ab166f22ef31bf1127f558d0ef"

SRC_URI = "http://ftp.de.debian.org/debian/pool/main/a/${BPN}/${BPN}_${PV}.tar.xz \
           file://adduser-add-M-option-for-useradd.patch \
"

SRC_URI[md5sum] = "e042a5842b8f24312d6f5a99a1af8f2c"
SRC_URI[sha256sum] = "72d811ad3ba17d2794b14d19acd1d6b57f9dd31d9250d51e786895dee2daeac0"

inherit cpan-base update-alternatives

S = "${WORKDIR}/${BPN}"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/adduser ${D}${sbindir}
    install -m 0755 ${S}/deluser ${D}${sbindir}

    install -D -m 0644 ${S}/AdduserCommon.pm ${D}${libdir}/perl/${@get_perl_version(d)}/Debian/AdduserCommon.pm
    sed -i -e "s/VERSION/${PV}/" ${D}${sbindir}/*

    install -d ${D}/${sysconfdir}
    install -m 0644 ${S}/*.conf ${D}/${sysconfdir}

    install -d ${D}${mandir}/man5
    install -m 0644 ${S}/doc/*.conf.5 ${D}${mandir}/man5
    install -d ${D}${mandir}/man8
    install -m 0644 ${S}/doc/*.8 ${D}${mandir}/man8
    install -d ${D}${docdir}/${BPN}
    cp -rf ${S}/examples ${D}${docdir}/${BPN}
}

RDEPENDS_${PN} += "\
    shadow \
    perl-module-getopt-long \
    perl-module-overloading \
    perl-module-file-find \
    perl-module-file-temp \
"

ALTERNATIVE_${PN} = "adduser deluser addgroup delgroup"
ALTERNATIVE_PRIORITY = "60"
ALTERNATIVE_LINK_NAME[adduser] = "${sbindir}/adduser"
ALTERNATIVE_LINK_NAME[deluser] = "${sbindir}/deluser"
ALTERNATIVE_LINK_NAME[addgroup] = "${sbindir}/addgroup"
ALTERNATIVE_LINK_NAME[delgroup] = "${sbindir}/delgroup"
ALTERNATIVE_TARGET[addgroup] = "${sbindir}/adduser.${BPN}"
ALTERNATIVE_TARGET[delgroup] = "${sbindir}/deluser.${BPN}"
