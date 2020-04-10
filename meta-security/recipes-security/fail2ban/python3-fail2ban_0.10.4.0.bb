SUMMARY = "Daemon to ban hosts that cause multiple authentication errors."
DESCRIPTION = "Fail2Ban scans log files like /var/log/auth.log and bans IP addresses having too \
many failed login attempts. It does this by updating system firewall rules to reject new \
connections from those IP addresses, for a configurable amount of time. Fail2Ban comes \
out-of-the-box ready to read many standard log files, such as those for sshd and Apache, \
and is easy to configure to read any log file you choose, for any error you choose."
HOMEPAGE = "http://www.fail2ban.org"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ecabc31e90311da843753ba772885d9f"

SRCREV ="aa565eb80ec6043317e8430cabcaf9c3f4e61578"
SRC_URI = " \
	git://github.com/fail2ban/fail2ban.git;branch=0.11 \
	file://initd \
        file://fail2ban_setup.py \
        file://run-ptest \
        file://0001-To-fix-build-error-of-xrang.patch \
"

inherit update-rc.d ptest setuptools3

S = "${WORKDIR}/git"

do_compile_prepend () {
    cp ${WORKDIR}/fail2ban_setup.py ${S}/setup.py
}

do_install_append () {
	install -d ${D}/${sysconfdir}/fail2ban
	install -d ${D}/${sysconfdir}/init.d
    	install -m 0755 ${WORKDIR}/initd ${D}${sysconfdir}/init.d/fail2ban-server
	chown -R root:root ${D}/${bindir}
}

do_install_ptest_append () {
        install -d ${D}${PTEST_PATH}
        sed -i -e 's/##PYTHON##/${PYTHON_PN}/g' ${D}${PTEST_PATH}/run-ptest
        install -D ${S}/bin/fail2ban-testcases ${D}${PTEST_PATH}
}

FILES_${PN} += "/run"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "fail2ban-server"
INITSCRIPT_PARAMS = "defaults 25"

INSANE_SKIP_${PN}_append = "already-stripped"

RDEPENDS_${PN} = "sysklogd iptables sqlite3 ${PYTHON_PN} ${PYTHON_PN}-pyinotify"
RDEPENDS_${PN}-ptest = "python3-core python3-io python3-modules python3-fail2ban"
