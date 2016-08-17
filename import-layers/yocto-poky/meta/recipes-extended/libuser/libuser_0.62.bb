SUMMARY = "user and group account administration library"
DESCRIPTION = "The libuser library implements a standardized interface for manipulating and administering user \
and group accounts"
HOMEPAGE = "https://fedorahosted.org/libuser/"
BUGTRACKER = "https://fedorahosted.org/libuser/newticket"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
                    file://lib/user.h;endline=19;md5=76b301f63c39fa992062395efbdc9558 \
                    file://samples/testuser.c;endline=19;md5=3b87fa660fa3f4a6bb31d624afe30ba1"

SECTION = "base"

SRC_URI = "https://fedorahosted.org/releases/l/i/libuser/libuser-${PV}.tar.xz \
           file://0001-Check-for-issetugid.patch \
           file://0002-remove-unused-execinfo.h.patch \
          "

SRC_URI[md5sum] = "63e5e5c551e99dc5302b40b80bd6d4f2"
SRC_URI[sha256sum] = "a58ff4fabb01a25043b142185a33eeea961109dd60d4b40b6a9df4fa3cace20b"

DEPENDS = "popt libpam glib-2.0 docbook-utils-native linuxdoc-tools-native python"

inherit autotools gettext pythonnative python-dir pkgconfig

EXTRA_OEMAKE = "PYTHON_CPPFLAGS=-I${STAGING_INCDIR}/${PYTHON_DIR}"

PACKAGES += "${PN}-python "

FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"

