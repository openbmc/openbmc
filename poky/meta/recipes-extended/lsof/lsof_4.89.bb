SUMMARY = "LiSt Open Files tool"
DESCRIPTION = "Lsof is a Unix-specific diagnostic tool. \
Its name stands for LiSt Open Files, and it does just that."
HOMEPAGE = "http://people.freebsd.org/~abe/"
SECTION = "devel"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://00README;beginline=645;endline=679;md5=964df275d26429ba3b39dbb9f205172a"

# Upstream lsof releases are hosted on an ftp server which times out download
# attempts from hosts for which it can not perform a DNS reverse-lookup (See:
# https://people.freebsd.org/~abe/ ). http://www.mirrorservice.org seems to be
# the most commonly used alternative.

SRC_URI = "http://www.mirrorservice.org/sites/lsof.itap.purdue.edu/pub/tools/unix/lsof/lsof_${PV}.tar.bz2 \
           file://lsof-remove-host-information.patch \
          "

SRC_URI[md5sum] = "1b9cd34f3fb86856a125abbf2be3a386"
SRC_URI[sha256sum] = "81ac2fc5fdc944793baf41a14002b6deb5a29096b387744e28f8c30a360a3718"

LOCALSRC = "file://${WORKDIR}/lsof_${PV}/lsof_${PV}_src.tar"

S = "${WORKDIR}/lsof_${PV}_src"

python do_unpack () {
    # temporarily change S for unpack
    # of lsof_${PV}
    s = d.getVar('S', False)
    d.setVar('S', '${WORKDIR}/lsof_${PV}')
    bb.build.exec_func('base_do_unpack', d)
    # temporarily change SRC_URI for unpack
    # of lsof_${PV}_src
    src_uri = d.getVar('SRC_URI', False)
    d.setVar('SRC_URI', '${LOCALSRC}')
    d.setVar('S', s)
    bb.build.exec_func('base_do_unpack', d)
    d.setVar('SRC_URI', src_uri)
}

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
	install -m 0644 lsof.8 ${D}${mandir}/man8/lsof.8
}
