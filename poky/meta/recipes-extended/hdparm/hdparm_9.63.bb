SUMMARY = "Utility for viewing/manipulating IDE disk drive/driver parameters"
HOMEPAGE = "http://sourceforge.net/projects/hdparm/"
DESCRIPTION = "hdparm is a Linux shell utility for viewing \
and manipulating various IDE drive and driver parameters."
SECTION = "console/utils"

LICENSE = "BSD-2-Clause & GPL-2.0-only & hdparm"
LICENSE:${PN} = "BSD-2-Clause & hdparm"
LICENSE:${PN}-dbg = "BSD-2-Clause & hdparm"
LICENSE:wiper = "GPL-2.0-only"
NO_GENERIC_LICENSE[hdparm] = "LICENSE.TXT"

LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=495d03e50dc6c89d6a30107ab0df5b03 \
                    file://debian/copyright;md5=a82d7ba3ade9e8ec902749db98c592f3 \
                    file://wiper/GPLv2.txt;md5=fcb02dc552a041dee27e4b85c7396067 \
                    file://wiper/wiper.sh;beginline=7;endline=31;md5=b7bc642addc152ea307505bf1a296f09"


PACKAGES =+ "wiper"

FILES:wiper = "${bindir}/wiper.sh"

RDEPENDS:wiper = "bash gawk coreutils"

SRC_URI = "${SOURCEFORGE_MIRROR}/hdparm/${BP}.tar.gz \
           file://wiper.sh-fix-stat-path.patch \
          "

SRC_URI[sha256sum] = "70785deaebba5877a89c123568b41dee990da55fc51420f13f609a1072899691"

EXTRA_OEMAKE = 'STRIP="echo" LDFLAGS="${LDFLAGS}"'

inherit update-alternatives

ALTERNATIVE:${PN} = "hdparm"
ALTERNATIVE_LINK_NAME[hdparm] = "${base_sbindir}/hdparm"
ALTERNATIVE_PRIORITY = "100"

do_install () {
	install -d ${D}/${base_sbindir} ${D}/${mandir}/man8 ${D}/${bindir}
	oe_runmake 'DESTDIR=${D}' 'sbindir=${base_sbindir}' install
	cp ${S}/wiper/wiper.sh ${D}/${bindir}
}
