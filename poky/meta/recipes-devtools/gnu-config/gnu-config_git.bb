SUMMARY = "gnu-configize"
DESCRIPTION = "Tool that installs the GNU config.guess / config.sub into a directory tree"
HOMEPAGE = "https://git.savannah.gnu.org/cgit/config.git"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://config.guess;beginline=9;endline=29;md5=b75d42f59f706ea56d6a8e00216fca6a"

DEPENDS:class-native = "hostperl-runtime-native"

INHIBIT_DEFAULT_DEPS = "1"

SRCREV = "191bcb948f7191c36eefe634336f5fc5c0c4c2be"
PV = "20211108+git${SRCPV}"

SRC_URI = "git://git.savannah.gnu.org/git/config.git;protocol=https;branch=master \
           file://gnu-configize.in"
S = "${WORKDIR}/git"
UPSTREAM_CHECK_COMMITS = "1"

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
FILES:${PN} = "${bindir} ${datadir}/gnu-config"

BBCLASSEXTEND = "native nativesdk"
