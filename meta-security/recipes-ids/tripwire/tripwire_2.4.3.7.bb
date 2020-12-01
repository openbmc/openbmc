SUMMARY = "Tripwire: A system integrity assessment tool (IDS)"
DESCRIPTION = "Open Source Tripwire® software is a security and data \
integrity tool useful for monitoring and alerting on specific file change(s) on a range of systems"
HOMEPAGE="http://sourceforge.net/projects/tripwire"
SECTION = "security Monitor/Admin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=1c069be8dbbe48e89b580ab4ed86c127"

SRCREV = "6e64a9e5b70a909ec439bc5a099e3fcf38c614b0"

SRC_URI = "\
	git://github.com/Tripwire/tripwire-open-source.git \
	file://tripwire.cron \
	file://tripwire.sh \
	file://tripwire.txt \
	file://twcfg.txt \
	file://twinstall.sh \
	file://twpol-yocto.txt \
	file://run-ptest \
       "

S = "${WORKDIR}/git"

inherit autotools-brokensep update-rc.d ptest

INITSCRIPT_NAME = "tripwire"
INITSCRIPT_PARAMS = "start 40 S ."
TRIPWIRE_HOST = "${HOST_SYS}"
TRIPWIRE_TARGET = "${TARGET_SYS}"

CXXFLAGS += "-fno-strict-aliasing"
EXTRA_OECONF = "--disable-openssl  --enable-static --sysconfdir=/etc/tripwire"

do_install () {
    install -d ${D}${libdir} ${D}${datadir} ${D}${base_libdir}
    install -d ${D}${sysconfdir} ${D}${mandir} ${D}${sbindir}
    install -d ${D}${sysconfdir}/${PN}
    install -d ${D}${localstatedir}/lib/${PN} ${D}${localstatedir}/lib/${BPN}/report
    install -d ${D}${mandir}/man4 ${D}${mandir}/man5 ${D}${mandir}/man8
    install -d ${D}${docdir}/${BPN} ${D}${docdir}/${BPN}/templates
    install -d ${D}${sysconfdir}/init.d

    install -m 0755 ${S}/bin/* ${D}${sbindir}
    install -m 0644 ${S}/lib/* ${D}${base_libdir}
    install -m 0644 ${S}/lib/* ${D}${localstatedir}/lib/${PN}
    install -m 0755 ${WORKDIR}/tripwire.cron ${D}${sysconfdir}
    install -m 0755 ${WORKDIR}/tripwire.sh ${D}${sysconfdir}/init.d/tripwire
    install -m 0755 ${WORKDIR}/twinstall.sh ${D}${sysconfdir}/${PN}
    install -m 0644 ${WORKDIR}/twpol-yocto.txt ${D}${sysconfdir}/${PN}/twpol.txt
    install -m 0644 ${WORKDIR}/twcfg.txt ${D}${sysconfdir}/${PN}

    install -m 0644 ${S}/man/man4/* ${D}${mandir}/man4
    install -m 0644 ${S}/man/man5/* ${D}${mandir}/man5
    install -m 0644 ${S}/man/man8/* ${D}${mandir}/man8
    rm ${D}${mandir}/man*/Makefile*
    install -m 0644 ${S}/policy/templates/* ${D}${docdir}/${BPN}/templates
    install -m 0644 ${S}/policy/*txt ${D}${docdir}/${BPN}
    install -m 0644 ${S}/COPYING ${D}${docdir}/${BPN}
    install -m 0644 ${S}/TRADEMARK ${D}${docdir}/${BPN}
    install -m 0644 ${WORKDIR}/tripwire.txt ${D}${docdir}/${BPN}
}

do_install_ptest_append () {
	install -d ${D}${PTEST_PATH}/tests
	cp -a ${S}/src/test-harness/* ${D}${PTEST_PATH}
	sed -i -e 's@../../../../bin@${sbindir}@'  ${D}${PTEST_PATH}/twtools.pm
}

FILES_${PN} += "${libdir} ${docdir}/${PN}/*"
FILES_${PN}-dbg += "${sysconfdir}/${PN}/.debug"
FILES_${PN}-staticdev += "${localstatedir}/lib/${PN}/lib*.a"
FILES_${PN}-ptest += "${PTEST_PATH}/tests "

RDEPENDS_${PN} += " perl nano msmtp cronie"
RDEPENDS_${PN}-ptest = " perl lib-perl perl-modules "
