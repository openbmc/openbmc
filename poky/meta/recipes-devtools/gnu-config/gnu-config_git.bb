SUMMARY = "gnu-configize"
DESCRIPTION = "Tool that installs the GNU config.guess / config.sub into a directory tree"
HOMEPAGE = "https://git.savannah.gnu.org/cgit/config.git"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://config.guess;beginline=9;endline=29;md5=10922f9231863a06f6efb67691fa46e0"

DEPENDS:class-native = "hostperl-runtime-native"

INHIBIT_DEFAULT_DEPS = "1"

SRCREV = "948ae97ca5703224bd3eada06b7a69f40dd15a02"
PV = "20240101+git"

SRC_URI = "git://git.savannah.gnu.org/git/config.git;protocol=https;branch=master \
           file://gnu-configize.in"
S = "${WORKDIR}/git"
UPSTREAM_CHECK_COMMITS = "1"

CLEANBROKEN = "1"

do_compile[noexec] = "1"

do_install () {
	install -d ${D}${datadir}/gnu-config \
		   ${D}${bindir}
	cat ${UNPACKDIR}/gnu-configize.in | \
		sed -e 's,@gnu-configdir@,${datadir}/gnu-config,g' \
		    -e 's,@autom4te_perllibdir@,${datadir}/autoconf,g' > ${D}${bindir}/gnu-configize
	# In the native case we want the system perl as perl-native can't have built yet
	if [ "${PN}" != "gnu-config-native" -a "${PN}" != "nativesdk-gnu-config" ]; then
		sed -i -e 's,/usr/bin/env,${bindir}/env,g' ${D}${bindir}/gnu-configize
	fi
	chmod 755 ${D}${bindir}/gnu-configize
	install -m 0644 config.guess config.sub ${D}${datadir}/gnu-config/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir} ${datadir}/gnu-config"

BBCLASSEXTEND = "native nativesdk"
