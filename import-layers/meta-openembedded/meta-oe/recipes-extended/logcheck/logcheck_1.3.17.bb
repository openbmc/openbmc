SUMMARY = "Analyzes log files and sends noticeable events as email"
DESCRIPTION = "\
Logcheck is a simple utility which is designed to allow a system administrator \
to view the log-files which are produced upon hosts under their control. \
It does this by mailing summaries of the log-files to them, after first \
filtering out "normal" entries. \
Normal entries are entries which match one of the many included regular \
expression files contain in the database."
SECTION = "Applications/System"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c93c0550bd3173f4504b2cbd8991e50b"

SRC_URI = "git://git.debian.org/git/logcheck/logcheck.git"
SRCREV = "2429e67ad875fee8a0234c64d504277b038c89cd"

S = "${WORKDIR}/git"

do_install() {
    # Fix QA Issue
    sed -i '/install -d $(DESTDIR)\/var\/lock\/logcheck/s/^/#/' Makefile

    # "make install" do not install the manpages. Install them manually.
    install -m 755 -d ${D}${mandir}/man1
    install -m 755 -d ${D}${mandir}/man8
    install -m 644 docs/logcheck-test.1 ${D}${mandir}/man1/
    install -m 644 docs/logtail.8 ${D}${mandir}/man8/
    install -m 644 docs/logtail2.8 ${D}${mandir}/man8/
    sed -i "s/syslog/messages/" etc/logcheck.logfiles
    sed -i "s/auth\.log/secure/" etc/logcheck.logfiles
    install -m 755 -d ${D}${sysconfdir}/cron.d
    install -m 644 debian/logcheck.cron.d ${D}${sysconfdir}/cron.d/logcheck
    install -m 755 -d ${D}/var/lib/logcheck
    oe_runmake install DESTDIR=${D}
}

RDEPENDS_${PN} = "perl"

FILES_${PN} += "${datadir}/logtail"
