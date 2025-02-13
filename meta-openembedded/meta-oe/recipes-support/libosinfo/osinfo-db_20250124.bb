SUMMARY = "osinfo-db provides the database files for use with the libosinfo library"
HOMEPAGE = "https://libosinfo.org"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "osinfo-db-tools-native"

SRC_URI = "git://gitlab.com/libosinfo/osinfo-db.git;branch=main;protocol=https"

SRCREV = "5be10e2c781b62bbaa55bd8f9ae990697fc325b4"

S = "${WORKDIR}/git"

inherit allarch autotools-brokensep

EXTRA_OEMAKE = "OSINFO_DB_TARGET='--dir ${datadir}/osinfo'"

do_configure[noexec] = "1"

FILES:${PN} = "${datadir}/osinfo"
