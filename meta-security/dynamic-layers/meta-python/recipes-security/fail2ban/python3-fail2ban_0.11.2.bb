SUMMARY = "Daemon to ban hosts that cause multiple authentication errors."
DESCRIPTION = "Fail2Ban scans log files like /var/log/auth.log and bans IP addresses having too \
many failed login attempts. It does this by updating system firewall rules to reject new \
connections from those IP addresses, for a configurable amount of time. Fail2Ban comes \
out-of-the-box ready to read many standard log files, such as those for sshd and Apache, \
and is easy to configure to read any log file you choose, for any error you choose."
HOMEPAGE = "http://www.fail2ban.org"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ecabc31e90311da843753ba772885d9f"

DEPENDS = "python3-native"

SRCREV ="4fe4ac8dde6ba14841da598ec37f8c6911fe0f64"
SRC_URI = " git://github.com/fail2ban/fail2ban.git;branch=0.11;protocol=https \
        file://initd \
        file://run-ptest \
"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

inherit update-rc.d ptest setuptools3_legacy

S = "${WORKDIR}/git"

do_compile () {
    cd ${S}

    #remove symlink to python3
    # otherwise 2to3 is run against it
    rm -f bin/fail2ban-python

    ./fail2ban-2to3
}

do_install:append () {
    rm  -f ${D}/${bindir}/fail2ban-python
    install -d ${D}/${sysconfdir}/fail2ban
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/initd ${D}${sysconfdir}/init.d/fail2ban-server
    chown -R root:root ${D}/${bindir}
    rm -rf ${D}/run
}

do_install_ptest:append () {
    install -d ${D}${PTEST_PATH}
    install -d ${D}${PTEST_PATH}/bin
    sed -i -e 's/##PYTHON##/${PYTHON_PN}/g' ${D}${PTEST_PATH}/run-ptest
    install -D ${S}/bin/* ${D}${PTEST_PATH}/bin
    rm -f ${D}${PTEST_PATH}/bin/fail2ban-python
}


INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "fail2ban-server"
INITSCRIPT_PARAMS = "defaults 25"

INSANE_SKIP:${PN}:append = "already-stripped"

RDEPENDS:${PN} = "${VIRTUAL-RUNTIME_base-utils-syslog} iptables sqlite3 python3-core python3-pyinotify"
RDEPENDS:${PN} += " python3-logging python3-fcntl python3-json"
RDEPENDS:${PN}-ptest = "python3-core python3-io python3-modules python3-fail2ban"
