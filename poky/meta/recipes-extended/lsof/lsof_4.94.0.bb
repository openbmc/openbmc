SUMMARY = "LiSt Open Files tool"
DESCRIPTION = "Lsof is a Unix-specific diagnostic tool. \
Its name stands for LiSt Open Files, and it does just that."
HOMEPAGE = "http://people.freebsd.org/~abe/"
SECTION = "devel"
LICENSE = "Spencer-94"
LIC_FILES_CHKSUM = "file://00README;beginline=645;endline=679;md5=964df275d26429ba3b39dbb9f205172a"

# Upstream lsof releases are hosted on an ftp server which times out download
# attempts from hosts for which it can not perform a DNS reverse-lookup (See:
# https://people.freebsd.org/~abe/ ). http://www.mirrorservice.org seems to be
# the most commonly used alternative.

SRC_URI = "git://github.com/lsof-org/lsof;branch=master;protocol=https \
           file://lsof-remove-host-information.patch \
          "

SRCREV = "005e014e1abdadb2493d8b3ce87b37a2c0a2351d"

S = "${WORKDIR}/git"


inherit update-alternatives

ALTERNATIVE:${PN} = "lsof"
ALTERNATIVE_LINK_NAME[lsof] = "${sbindir}/lsof"
# Make our priority higher than busybox
ALTERNATIVE_PRIORITY = "100"


export LSOF_INCLUDE = "${STAGING_INCDIR}"

do_configure () {
	export LSOF_AR="${AR} cr"
	export LSOF_RANLIB="${RANLIB}"
	if [ "x${GLIBCVERSION}" != "x" ]; then
		LINUX_CLIB=`echo ${GLIBCVERSION} |sed -e 's,\.,,g'`
		LINUX_CLIB="-DGLIBCV=${LINUX_CLIB}"
		export LINUX_CLIB
	fi
	yes | ./Configure linux
}

export I = "${STAGING_INCDIR}"
export L = "${STAGING_INCDIR}"

do_compile () {
	oe_runmake 'CC=${CC}' 'CFGL=${LDFLAGS} -L./lib -llsof' 'DEBUG=' 'INCL=${CFLAGS}'
}

do_install () {
	install -d ${D}${sbindir} ${D}${mandir}/man8
	install -m 0755 lsof ${D}${sbindir}/lsof
	install -m 0644 Lsof.8 ${D}${mandir}/man8/lsof.8
}
