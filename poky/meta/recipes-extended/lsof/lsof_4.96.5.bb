SUMMARY = "LiSt Open Files tool"
DESCRIPTION = "Lsof is a Unix-specific diagnostic tool. \
Its name stands for LiSt Open Files, and it does just that."
HOMEPAGE = "http://people.freebsd.org/~abe/"
SECTION = "devel"
LICENSE = "Spencer-94"
LIC_FILES_CHKSUM = "file://00README;beginline=645;endline=679;md5=964df275d26429ba3b39dbb9f205172a"

SRC_URI = "git://github.com/lsof-org/lsof;branch=master;protocol=https \
           file://remove-host-information.patch"
SRCREV = "898916d4c7c390ea4610aebaf1d32b8a3c49f26b"

S = "${WORKDIR}/git"


inherit update-alternatives

ALTERNATIVE_${PN} = "lsof"
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
