DESCRIPTION = "Libraries providing API for reporting different problems in applications \
to different bug targets like Bugzilla, ftp, trac, etc..."
SUMMARY = "Generic library for reporting various problems"
HOMEPAGE = "https://abrt.readthedocs.org/"
LICENSE = "GPL-2.0-or-later"
DEPENDS = "xmlrpc-c xmlrpc-c-native intltool-native \
        json-c libarchive libtar libnewt libproxy rpm \
        augeas satyr systemd \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://github.com/abrt/libreport.git;protocol=https;branch=master"
SRC_URI += "file://0001-Makefile.am-remove-doc-and-apidoc.patch \
            file://0002-configure.ac-remove-prog-test-of-xmlto-and-asciidoc.patch \
            file://0003-without-build-plugins.patch \
            file://0004-configure.ac-remove-prog-test-of-augparse.patch \
"

SRCREV = "d58110e1fc663c92ac3e36b166f114b6904796ff"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit features_check
REQUIRED_DISTRO_FEATURES = "systemd"

inherit gettext autotools python3native python3targetconfig pkgconfig

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES','x11','gtk','',d)}"
PACKAGECONFIG[gtk] = "--with-gtk, --without-gtk, gtk+3,"

EXTRA_OECONF += "--with-python3"

RDEPENDS:python3-libreport += "${PN}"

do_patch[prefuncs] += "do_gen_version"
do_gen_version() {
    cd ${S}
    ./gen-version
}

PACKAGES += "python3-libreport"

FILES:${PN} += "${datadir}/*"
FILES:${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*/.debug"
FILES:python3-libreport = "${PYTHON_SITEPACKAGES_DIR}/*"

