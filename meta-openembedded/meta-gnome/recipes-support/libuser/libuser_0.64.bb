SUMMARY = "user and group account administration library"
DESCRIPTION = "The libuser library implements a standardized interface for manipulating and administering user \
and group accounts"
HOMEPAGE = "https://pagure.io/libuser"
BUGTRACKER = "https://pagure.io/libuser/issues"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
                    file://lib/user.h;endline=19;md5=76b301f63c39fa992062395efbdc9558 \
                    file://samples/testuser.c;endline=19;md5=3b87fa660fa3f4a6bb31d624afe30ba1"

SECTION = "base"

SRC_URI = "https://releases.pagure.org/libuser/libuser-${PV}.tar.gz \
           file://0001-docs-Disable-building.patch \
           file://0002-remove-unused-execinfo.h.patch \
           "

SRC_URI[sha256sum] = "ea6094c72cb9e60a42fb53509dc98d124a340f1c9222783b503208adc16a0a8f"

DEPENDS = "bison-native popt libpam glib-2.0 python3"

inherit autotools features_check gettext python3native python3-dir pkgconfig gtk-doc

REQUIRED_DISTRO_FEATURES = "pam"

EXTRA_OEMAKE = "PYTHON_CPPFLAGS=-I${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}"

GTKDOC_DOCDIR = "${S}/docs/reference"

# run autopoint since it needs ABOUT-NLS and admin/config.rpath from gettext
#EXTRA_AUTORECONF:remove = "--exclude=autopoint"

do_configure:prepend() {
    install -d ${S}/admin -d ${S}/m4
    touch ${S}/ABOUT-NLS ${S}/admin/config.rpath
    cd ${S}
    bison lib/getdate.y -o lib/getdate.c
    cd -
}

PACKAGES += "${PN}-python "

FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"

