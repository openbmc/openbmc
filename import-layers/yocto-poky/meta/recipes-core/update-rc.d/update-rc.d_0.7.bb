SUMMARY = "manage symlinks in /etc/rcN.d"
HOMEPAGE = "http://github.com/philb/update-rc.d/"
DESCRIPTION = "update-rc.d is a utility that allows the management of symlinks to the initscripts in the /etc/rcN.d directory structure."
SECTION = "base"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://update-rc.d;beginline=5;endline=15;md5=148a48321b10eb37c1fa3ee02b940a75"

PR = "r5"

# Revision corresponding to tag update-rc.d_0.7
SRCREV = "eca680ddf28d024954895f59a241a622dd575c11"

SRC_URI = "git://github.com/philb/update-rc.d.git \
           file://add-verbose.patch \
           file://check-if-symlinks-are-valid.patch \
           file://fix-to-handle-priority-numbers-correctly.patch \
          "
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit allarch

do_compile() {
}

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/update-rc.d ${D}${sbindir}/update-rc.d
}

BBCLASSEXTEND = "native"
