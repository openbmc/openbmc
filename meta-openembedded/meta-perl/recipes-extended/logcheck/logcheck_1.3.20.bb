SUMMARY = "Analyzes log files and sends noticeable events as email"
DESCRIPTION = "\
Logcheck is a simple utility which is designed to allow a system administrator \
to view the log-files which are produced upon hosts under their control. \
It does this by mailing summaries of the log-files to them, after first \
filtering out "normal" entries. \
Normal entries are entries which match one of the many included regular \
expression files contain in the database."
SECTION = "Applications/System"
HOMEPAGE = "http://logcheck.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c93c0550bd3173f4504b2cbd8991e50b"

SRC_URI = "${DEBIAN_MIRROR}/main/l/${BPN}/${BPN}_${PV}.tar.xz \
           file://99_logcheck \
"
SRC_URI[md5sum] = "1c6e9a97f9cc485353c25147cb99fb25"
SRC_URI[sha256sum] = "9fb6d02b933470d0b1d1efb54ea186e0d0d27336f9d146be592f65ce60dfb3e6"

S = "${WORKDIR}/${BPN}"

inherit useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "-m -g ${BPN} -G adm -r -d ${localstatedir}/lib/${BPN} \
                       -s /bin/false -c 'logcheck account' ${BPN}"
GROUPADD_PARAM_${PN} = "-r ${BPN}"

do_install() {
    # Fix QA Issue
    sed -i '/install -d $(DESTDIR)\/var\/lock\/logcheck/s/^/#/' Makefile

    # "make install" do not install the manpages. Install them manually.
    install -m 755 -d ${D}${mandir}/man1
    install -m 755 -d ${D}${mandir}/man8
    install -m 644 docs/logcheck-test.1 ${D}${mandir}/man1/
    install -m 644 docs/logtail.8 ${D}${mandir}/man8/
    install -m 644 docs/logtail2.8 ${D}${mandir}/man8/

    install -m 755 -d ${D}${sysconfdir}/cron.d
    install -m 644 debian/logcheck.cron.d ${D}${sysconfdir}/cron.d/logcheck
    install -m 755 -d ${D}/var/lib/logcheck

    oe_runmake install DESTDIR=${D}

    # install header.txt for generated mails
    install -m 0644 ${S}/debian/header.txt ${D}${sysconfdir}/${BPN}

    chown -R ${BPN}:${BPN} ${D}${localstatedir}/lib/${BPN}
    chown -R root:${BPN} ${D}${sysconfdir}/${BPN}

    # Don't install /var/lock when populating rootfs. Do it through volatile
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0644 ${WORKDIR}/99_logcheck ${D}${sysconfdir}/default/volatiles
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/lock/logcheck 0755 logcheck logcheck -" \
            > ${D}${sysconfdir}/tmpfiles.d/logcheck.conf
    fi
}

VIRTUAL-RUNTIME_syslog ??= "rsyslog"

RDEPENDS_${PN} = "\
    bash \
    cronie \
    debianutils-run-parts \
    grep \
    lockfile-progs \
    mime-construct \
    perl \
    perl-module-file-basename \
    perl-module-getopt-std \
    perl-module-file-glob \
    ${VIRTUAL-RUNTIME_syslog} \
"

FILES_${PN} += "${datadir}/logtail"
