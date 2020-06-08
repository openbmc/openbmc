SUMARRY = "The ethernet monitor program; for keeping track of ethernet/ip address pairings"
LICENSE = "BSD-4-Clause"
HOME_PAGE = "http://ee.lbl.gov/"
LIC_FILES_CHKSUM = "file://configure;md5=212742e55562cf47527d31c2a492411a"

DEPENDS += "libpcap postfix"

SRC_URI = "https://ee.lbl.gov/downloads/arpwatch/${BP}.tar.gz \
           file://arpwatch.conf \
           file://arpwatch.default \
           file://arpwatch_init  \
           file://postfix_workaround.patch \
           file://host_contam_fix.patch "

SRC_URI[sha256sum] = "82e137e104aca8b1280f5cca0ebe61b978f10eadcbb4c4802c181522ad02b25b"

inherit  autotools-brokensep update-rc.d useradd

ARPWATCH_UID ?= "arpwatch"
ARPWATCH_GID ?= "arpwatch"
APRWATCH_FROM ?= "root "
ARPWATH_REPLY ?= "${ARPWATCH_UID}"

EXTRA_OECONF = " --srcdir=${S} --with-watcher=email=${APRWATCH_FROM} --with-watchee=email=${ARPWATH_REPLY}"

CONFIGUREOPTS = " --build=${BUILD_SYS} \
          --host=${HOST_SYS} \
          --target=${TARGET_SYS} \
          --prefix=${prefix} \
          --exec_prefix=${exec_prefix} \
          --bindir=${bindir} \
          --sbindir=${sbindir} \
          --libexecdir=${libexecdir} \
          --datadir=${datadir} \
          --sysconfdir=${sysconfdir} \
          --sharedstatedir=${sharedstatedir} \
          --localstatedir=${localstatedir} \
          --libdir=${libdir} \
          --includedir=${includedir} \
          --oldincludedir=${oldincludedir} \
          --infodir=${infodir} \
          --mandir=${mandir} \
          "

do_configure () {
    ${S}/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${sbindir}
    install -d ${D}${mandir}
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${prefix}/etc/rc.d
    install -d ${D}/var/lib/arpwatch

    oe_runmake install DESTDIR=${D}
    install -m 644 ${WORKDIR}/arpwatch.conf  ${D}${sysconfdir}
    install -m 655 ${WORKDIR}/arpwatch_init  ${D}${sysconfdir}/init.d/arpwatch
    install -m 644 ${WORKDIR}/arpwatch.default  ${D}${sysconfdir}/default
}

INITSCRIPT_NAME = "arpwatch"
INITSCRIPT_PARAMS = "start 02 2 3 4 5 . stop 20 0 1 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${ARPWATCH_UID}"
USERADD_PARAM_${PN} = "--system -g ${ARPWATCH_GID} --home-dir  \
    ${localstatedir}/spool/${BPN} \
    --no-create-home  --shell /bin/false ${BPN}"

CONFFILE_FILES = "${sysconfdir}/${PN}.conf"

FILES_${PN} = "${bindir} ${sbindir} ${prefix}/etc/rc.d \
               ${sysconfdir} /var/lib/arpwatch"

RDEPENDS_${PN} = "libpcap postfix postfix-cfg"
