SUMMARY = "A indenti.ca client for E"
DEPENDS = "glib-2.0 gconf curl elementary sqlite3-native azy"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SECTION = "e/apps"
HOMEPAGE = "http://elmdentica.googlecode.com"
AUTHOR = "seabra"

inherit e gettext
SRC_URI = "${E_SVN}/trunk;module=${SRCNAME};protocol=http;scmdata=keep"
S = "${WORKDIR}/${SRCNAME}"

EXTRA_OECONF = "--with-edje-cc=${STAGING_BINDIR_NATIVE}/edje_cc"

PV = "0.9.9+svnr${SRCPV}"
SRCREV = "${EFL_SRCREV}"

RDEPENDS_${PN} = "${PN}-themes"

SRC_URI += "file://configure.patch"

PNBLACKLIST[elmdentica] ?= "OLD and depends on broken azy"
