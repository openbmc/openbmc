SUMMARY = "Lightweight http(s) proxy daemon"
HOMEPAGE = "https://tinyproxy.github.io/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.gz \
           file://disable-documentation.patch \
           file://tinyproxy.service \
           file://tinyproxy.conf \
           file://run-ptest \
           file://CVE-2025-63938.patch \
           "

SRC_URI[sha256sum] = "2c8fe5496f2c642bfd189020504ab98d74b9edbafcdb94d9f108e157b5bdf96d"

UPSTREAM_CHECK_URI = "https://github.com/tinyproxy/tinyproxy/releases"

EXTRA_OECONF += " \
	--enable-filter \
	--enable-transparent \
	--enable-reverse \
	--enable-upstream \
	--enable-xtinyproxy \
	"

inherit autotools systemd useradd ptest

#User specific
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home /dev/null \
                       --no-user-group --gid nogroup tinyproxy"

SYSTEMD_PACKAGES += "${BPN}"
SYSTEMD_SERVICE:${PN} = "tinyproxy.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
		install -m 0644 ${UNPACKDIR}/tinyproxy.service ${D}${systemd_system_unitdir}
	fi
	install -m 0644 ${UNPACKDIR}/tinyproxy.conf ${D}${sysconfdir}/tinyproxy.conf
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests/scripts
	install -d ${D}${PTEST_PATH}/data/templates
	install ${S}/tests/scripts/*.sh ${D}${PTEST_PATH}/tests/scripts
	install ${S}/tests/scripts/*.pl ${D}${PTEST_PATH}/tests/scripts
	install -m 0644 ${S}/data/templates/*.html ${D}${PTEST_PATH}/data/templates/
	# test the installed binary, not the one that was just compiled in the src folder
	sed -i 's,TINYPROXY_BIN=.*,TINYPROXY_BIN=tinyproxy,' ${D}${PTEST_PATH}/tests/scripts/run_tests.sh
}

RDEPENDS:${PN}-ptest += "\
    perl \
    perl-module-cwd \
    perl-module-encode-encoding \
    perl-module-file-spec \
    perl-module-getopt-long \
    perl-module-io-socket \
    perl-module-io-socket-inet \
    perl-module-pod-text \
    perl-module-posix \
    procps \
"
