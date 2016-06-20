SUMMARY = "Utility for viewing/manipulating IDE disk drive/driver parameters"
DESCRIPTION = "hdparm is a Linux shell utility for viewing \
and manipulating various IDE drive and driver parameters."
SECTION = "console/utils"
LICENSE = "BSD"
LICENSE_wiper = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=910a8a42c962d238619c75fdb78bdb24 \
                    file://debian/copyright;md5=a82d7ba3ade9e8ec902749db98c592f3 \
                    file://wiper/GPLv2.txt;md5=fcb02dc552a041dee27e4b85c7396067 \
                    file://wiper/wiper.sh;beginline=7;endline=31;md5=b7bc642addc152ea307505bf1a296f09"


PACKAGES =+ "wiper"

FILES_wiper = "${bindir}/wiper.sh"

RDEPENDS_wiper = "bash gawk stat"

SRC_URI = "${SOURCEFORGE_MIRROR}/hdparm/${BP}.tar.gz "

SRC_URI[md5sum] = "213efdbe7471fad3408198918e164354"
SRC_URI[sha256sum] = "ce97b4a71cb04146f54cf6f69787e7f97ddfda9836dc803b459d3b3df3a4fbee"

EXTRA_OEMAKE = '-e MAKEFLAGS= STRIP="echo"'

inherit update-alternatives

ALTERNATIVE_${PN} = "hdparm"
ALTERNATIVE_LINK_NAME[hdparm] = "${base_sbindir}/hdparm"
ALTERNATIVE_PRIORITY = "100"

do_install () {
	install -d ${D}/${base_sbindir} ${D}/${mandir}/man8 ${D}/${bindir}
	oe_runmake 'DESTDIR=${D}' 'sbindir=${base_sbindir}' install
	cp ${S}/wiper/wiper.sh ${D}/${bindir}
}
