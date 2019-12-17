SUMMARY = "Active Directory enrollment"
DESCRIPTION = "A helper library and tools for Active Directory client operations."

HOMEPAGE = "http://cgit.freedesktop.org/realmd/adcli"
SECTION = "net"

SRCREV = "cc3ef52884a48863a81acbfc741735fe09cd85f7"

SRC_URI = "git://gitlab.freedesktop.org/realmd/adcli;branch=master \
           file://Fixed-build-error-on-musl.patch \
	   "

S = "${WORKDIR}/git"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

inherit autotools xmlcatalog 

DEPENDS += "virtual/crypt krb5 openldap gettext libxslt xmlto libxml2-native \
            cyrus-sasl libxslt-native xmlto-native coreutils-native\
           "

EXTRA_OECONF += "--disable-static \
                 --disable-silent-rules \
                 --disable-doc \
                "
