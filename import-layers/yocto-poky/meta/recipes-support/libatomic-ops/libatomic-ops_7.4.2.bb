SUMMARY = "A library for atomic integer operations"
HOMEPAGE = "https://github.com/ivmai/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://doc/LICENSING.txt;md5=e00dd5c8ac03a14c5ae5225a4525fa2d \
		   "

SRC_URI = "\
	http://www.ivmaisoft.com/_bin/atomic_ops/libatomic_ops-${PV}.tar.gz \
	file://0001-Add-initial-nios2-architecture-support.patch \
	"

SRC_URI[md5sum] = "1d6538604b314d2fccdf86915e5c0857"
SRC_URI[sha256sum] = "04fa615f62992547bcbda562260e28b504bc4c06e2f985f267f3ade30304b5dd"

S = "${WORKDIR}/libatomic_ops-${PV}"

ALLOW_EMPTY_${PN} = "1"

ARM_INSTRUCTION_SET = "arm"

inherit autotools pkgconfig

do_install_append() {
	# those contain only docs, not necessary for now.
	install -m 0755 -d ${D}${docdir}
	mv ${D}${datadir}/libatomic_ops ${D}${docdir}/${BPN}
}

BBCLASSEXTEND = "native nativesdk"
