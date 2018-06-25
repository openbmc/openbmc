SUMMARY = "Utility for viewing/manipulating IDE disk drive/driver parameters"
HOMEPAGE = "http://sourceforge.net/projects/hdparm/"
DESCRIPTION = "hdparm is a Linux shell utility for viewing \
and manipulating various IDE drive and driver parameters."
SECTION = "console/utils"

LICENSE = "BSD & GPLv2"
LICENSE_${PN} = "BSD"
LICENSE_${PN}-dbg = "BSD"
LICENSE_wiper = "GPLv2"

LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=495d03e50dc6c89d6a30107ab0df5b03 \
                    file://debian/copyright;md5=a82d7ba3ade9e8ec902749db98c592f3 \
                    file://wiper/GPLv2.txt;md5=fcb02dc552a041dee27e4b85c7396067 \
                    file://wiper/wiper.sh;beginline=7;endline=31;md5=b7bc642addc152ea307505bf1a296f09"


PACKAGES =+ "wiper"

FILES_wiper = "${bindir}/wiper.sh"

RDEPENDS_wiper = "bash gawk coreutils"

SRC_URI = "${SOURCEFORGE_MIRROR}/hdparm/${BP}.tar.gz \
           file://wiper.sh-fix-stat-path.patch \
          "

SRC_URI[md5sum] = "1e54b52e0c8cb79389d4d47eacba411d"
SRC_URI[sha256sum] = "2673f591df6916c6a1faec07069a7eb1cb8ee307615a615d808192fb0c0265e5"

EXTRA_OEMAKE = 'STRIP="echo" LDFLAGS="${LDFLAGS}"'

inherit update-alternatives

ALTERNATIVE_${PN} = "hdparm"
ALTERNATIVE_LINK_NAME[hdparm] = "${base_sbindir}/hdparm"
ALTERNATIVE_PRIORITY = "100"

do_install () {
	install -d ${D}/${base_sbindir} ${D}/${mandir}/man8 ${D}/${bindir}
	oe_runmake 'DESTDIR=${D}' 'sbindir=${base_sbindir}' install
	cp ${S}/wiper/wiper.sh ${D}/${bindir}
}
