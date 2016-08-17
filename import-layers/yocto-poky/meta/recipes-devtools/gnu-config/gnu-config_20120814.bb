SUMMARY = "gnu-configize"
DESCRIPTION = "Tool that installs the GNU config.guess / config.sub into a directory tree"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://config.guess;endline=39;md5=b79a4663475f4d724846463277817e0d"

DEPENDS_class-native = "hostperl-runtime-native"

INHIBIT_DEFAULT_DEPS = "1"


SRC_URI = "http://downloads.yoctoproject.org/releases/gnu-config/gnu-config-${PV}.tar.bz2 \
	   file://config-guess-uclibc.patch \
	   file://musl-support.patch \
           file://gnu-configize.in"

SRC_URI[md5sum] = "bcfca5a2bb39edad4aae5a65efc84094"
SRC_URI[sha256sum] = "44f99a8e76f3e8e4fec0bb5ad4762f8e44366168554ce66cb85afbe2ed3efd8b"

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
