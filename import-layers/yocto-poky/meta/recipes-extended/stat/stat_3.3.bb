SUMMARY = "Command line file status display utility"
HOMEPAGE = "http://www.ibiblio.org/pub/Linux/utils/file/"
DESCRIPTION = "Displays all information about a file that the stat() call provides and all information about a filesystem that statfs() provides."
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=39886b077fd072e876e5c4c16310b631 \
                    file://GPL;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://www.ibiblio.org/pub/Linux/utils/file/${BP}.tar.gz \
           file://fix-security-format.patch \
           file://fix-error-return.patch"

SRC_URI[md5sum] = "37e247e8e400ad9205f1b0500b728fd3"
SRC_URI[sha256sum] = "7071f0384a423a938dd542c1f08547a02824f6359acd3ef3f944b2c4c2d1ee09"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
	install -d ${D}${base_bindir} ${D}${mandir}/man1
	install -m 755 stat ${D}${base_bindir}/stat.stat
	install -m 644 stat.1 ${D}${mandir}/man1
}

inherit update-alternatives

ALTERNATIVE_${PN} = "stat"
ALTERNATIVE_PRIORITY[stat] = "200"
ALTERNATIVE_LINK_NAME[stat] = "${base_bindir}/stat"
ALTERNATIVE_TARGET[stat] = "${base_bindir}/stat.stat"

ALTERNATIVE_${PN}-doc = "stat.1"
ALTERNATIVE_LINK_NAME[stat.1] = "${mandir}/man1/stat.1"

BBCLASSEXTEND = "native"

