SUMMARY = "Active Directory enrollment"
DESCRIPTION = "A helper library and tools for Active Directory client operations."

HOMEPAGE = "http://cgit.freedesktop.org/realmd/adcli"
SECTION = "net"

SRCREV = "f3b69c2497c1a66359047abc3042c11cab2199e1"

SRC_URI = "git://gitlab.freedesktop.org/realmd/adcli;protocol=https;branch=master;tag=${PV} \
           file://0001-configure.ac-Fix-selinux-error-for-cross_compiling.patch \
          "

LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

inherit autotools xmlcatalog pkgconfig

DEPENDS += "virtual/crypt krb5 openldap gettext libxslt xmlto libxml2-native \
            cyrus-sasl libxslt-native xmlto-native coreutils-native\
           "

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[selinux] = ",--disable-selinux-support,libselinux,libselinux"

EXTRA_OECONF += "--disable-silent-rules \
                 --disable-doc \
                 --disable-offline-join-support \
                "

FILES:${PN} += "${datadir}"
