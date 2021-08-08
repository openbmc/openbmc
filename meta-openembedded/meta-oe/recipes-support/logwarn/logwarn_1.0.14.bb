SUMMARY = "A utility for finding interesting messages in log files"
DESCRIPTION = "Logwarn searches for interesting messages in log files, \
  where 'interesting' is defined by a user-supplied list of positive and \
  negative extended regular expressions. \
"
HOMEPAGE = "https://github.com/archiecobbs/logwarn/wiki"
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "https://s3.amazonaws.com/archie-public/${BPN}/${BP}.tar.gz"

SRC_URI[md5sum] = "e544a6230673ea54f7430bf817bb39d8"
SRC_URI[sha256sum] = "8dbfcf9b28c782ab3bddd6a620d4fb95d1b0ffcbe93276996cdc4800aa9aebd1"

inherit autotools-brokensep

# This directory is NOT volatile.
#
lcl_default_state_dir = "${localstatedir}/lib/logwarn"

CFLAGS += '-DDEFAULT_STATE_DIR=\""${lcl_default_state_dir}\""'

CACHED_CONFIGUREVARS += " \
    ac_cv_path_BASH_SHELL=${base_bindir}/bash \
    ac_cv_path_CAT=${base_bindir}/cat \
    ac_cv_path_RM=${base_bindir}/rm \
    ac_cv_path_SED=${base_bindir}/sed \
"

# Make sure some files exist for autoreconf.
#
do_configure:prepend () {
    touch ${S}/NEWS
    touch ${S}/ChangeLog
    touch ${S}/README
}

# Create a directory for logfile state info, usually under /var/lib.
#
do_install:append () {
    install -d ${D}${lcl_default_state_dir}
}

# Make a package for the nagios plug-in (script).
#
PACKAGES += "${PN}-nagios"

FILES:${PN}-nagios = "${nonarch_libdir}/nagios"

RDEPENDS:${PN}-nagios += "bash coreutils sed"
