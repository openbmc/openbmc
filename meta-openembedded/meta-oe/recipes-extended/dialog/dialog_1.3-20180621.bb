SUMMARY = "display dialog boxes from shell scripts"
DESCRIPTION = "Dialog lets you to present a variety of questions \
or display messages using dialog boxes from a shell \
script (or any scripting language)."
HOMEPAGE = "http://invisible-island.net/dialog/"
SECTION = "console/utils"
DEPENDS = "ncurses"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI = "ftp://ftp.invisible-island.net/${BPN}/${BP}.tgz \
          "
SRC_URI[md5sum] = "1dba09cf45ac4ba40b3cb4a3f1d694b2"
SRC_URI[sha256sum] = "4a4859e2b22d24e46c1a529b5a5605b95503aa04da4432f7bbd713e3e867587a"

# hardcoded here for use in dialog-static recipe
S = "${WORKDIR}/dialog-${PV}"

inherit autotools-brokensep pkgconfig

EXTRA_OECONF = "--with-ncurses \
                --disable-rpath-hack"

do_configure() {
    gnu-configize --force
    sed -i 's,${cf_ncuconfig_root}6-config,${cf_ncuconfig_root}-config,g' -i configure
    sed -i 's,cf_have_ncuconfig=unknown,cf_have_ncuconfig=yes,g' -i configure
    oe_runconf
}
