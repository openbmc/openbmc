SUMMARY = "GNU grep utility"
HOMEPAGE = "http://savannah.gnu.org/projects/grep/"
BUGTRACKER = "http://savannah.gnu.org/bugs/?group=grep"
SECTION = "console/utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/grep/grep-${PV}.tar.xz"

SRC_URI[md5sum] = "05d0718a1b7cc706a4bdf8115363f1ed"
SRC_URI[sha256sum] = "b960541c499619efd6afe1fa795402e4733c8e11ebf9fafccc0bb4bccdc5b514"

inherit autotools gettext texinfo pkgconfig

# Fix "Argument list too long" error when len(TMPDIR) = 410
acpaths = "-I ./m4"

do_configure_prepend () {
	rm -f ${S}/m4/init.m4
}

do_install () {
	autotools_do_install
	if [ "${base_bindir}" != "${bindir}" ]; then
		install -d ${D}${base_bindir}
		mv ${D}${bindir}/grep ${D}${base_bindir}/grep
		mv ${D}${bindir}/egrep ${D}${base_bindir}/egrep
		mv ${D}${bindir}/fgrep ${D}${base_bindir}/fgrep
		rmdir ${D}${bindir}/
	fi
}

inherit update-alternatives

PACKAGECONFIG ??= "pcre"
PACKAGECONFIG[pcre] = "--enable-perl-regexp,--disable-perl-regexp,libpcre"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN} = "grep egrep fgrep"
ALTERNATIVE_LINK_NAME[grep] = "${base_bindir}/grep"
ALTERNATIVE_LINK_NAME[egrep] = "${base_bindir}/egrep"
ALTERNATIVE_LINK_NAME[fgrep] = "${base_bindir}/fgrep"

export CONFIG_SHELL="/bin/sh"
