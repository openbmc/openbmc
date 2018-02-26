DESCRIPTION = "Libraries providing API for reporting different problems in applications \
to different bug targets like Bugzilla, ftp, trac, etc..."
SUMMARY = "Generic library for reporting various problems"
HOMEPAGE = "https://abrt.readthedocs.org/"
LICENSE = "GPLv2+"
DEPENDS = "xmlrpc-c xmlrpc-c-native intltool-native \
        json-c libtar libnewt libproxy rpm \
        augeas satyr systemd gtk+3 \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"


SRC_URI = "git://github.com/abrt/libreport.git;protocol=https"
SRC_URI += "file://0001-Makefile.am-remove-doc-and-apidoc.patch \
            file://0002-configure.ac-remove-prog-test-of-xmlto-and-asciidoc.patch \
            file://0003-without-build-plugins.patch \
            file://0004-configure.ac-remove-prog-test-of-augparse.patch \
            file://0005-remove-python2-support.patch \
"
SRCREV = "cfff49f06d9d17e269d463ee71b3d94c8bfb0c58"
S = "${WORKDIR}/git"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "systemd"

inherit gettext autotools python3native pkgconfig

RDEPENDS_python3-libreport += "${PN}"

do_patch[prefuncs] += "do_gen_version"
do_gen_version() {
    cd ${S}
    ./gen-version
}

PACKAGES += "python3-libreport"

FILES_${PN} += "${datadir}/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*/.debug"
FILES_python3-libreport = "${PYTHON_SITEPACKAGES_DIR}/*"

