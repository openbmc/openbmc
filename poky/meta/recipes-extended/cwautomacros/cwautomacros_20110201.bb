SUMMARY = "Collection of autoconf m4 macros"
SECTION = "base"
HOMEPAGE = "http://sourceforge.net/projects/cwautomacros.berlios/"
DESCRIPTION = "A collection of autoconf macros, plus an autogen.sh script that can be used with them."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}.berlios/${BP}.tar.bz2"
SRC_URI[md5sum] = "074afcb50d0a8bff10786a2954b2b02d"
SRC_URI[sha256sum] = "3115603b891f3a163c0bbb5fea2f3742113a183fa6745ee5e89e5f6d0e9f6121"

do_configure() {
	:
}

do_install() {
	oe_runmake LABEL=`date -d @${SOURCE_DATE_EPOCH} +%Y%m%d` CWAUTOMACROSPREFIX=${D}${prefix} install

	# cleanup buildpaths in autogen.sh
	sed -i -e 's,${D},,g' ${D}${prefix}/share/cwautomacros/scripts/autogen.sh
}

BBCLASSEXTEND = "native"
