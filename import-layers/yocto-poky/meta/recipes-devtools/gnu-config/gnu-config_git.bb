SUMMARY = "gnu-configize"
DESCRIPTION = "Tool that installs the GNU config.guess / config.sub into a directory tree"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://config.guess;beginline=7;endline=27;md5=9bac8b1743c2240ae07cce6e546ac2f2"

DEPENDS_class-native = "hostperl-runtime-native"

INHIBIT_DEFAULT_DEPS = "1"

SRCREV = "b576fa87c140b824466ef1638e945e87dc5c0343"
PV = "20150728+git${SRCPV}"

SRC_URI = "git://git.savannah.gnu.org/config.git \
           file://gnu-configize.in"
S = "${WORKDIR}/git"
UPSTREAM_VERSION_UNKNOWN = "1"

CLEANBROKEN = "1"

do_compile[noexec] = "1"

do_install () {
	install -d ${D}${datadir}/gnu-config \
		   ${D}${bindir}
	cat ${WORKDIR}/gnu-configize.in | \
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
FILES_${PN} = "${bindir} ${datadir}/gnu-config"

BBCLASSEXTEND = "native nativesdk"
