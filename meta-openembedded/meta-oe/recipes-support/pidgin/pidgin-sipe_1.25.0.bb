SUMMARY = "Protocol plugin for Office 365/Lync/OCS for Adium, Pidgin, Miranda and Telepathy IM Framework"
SECTION = "webos/services"
LICENSE = "GPLv2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "pidgin gmime intltool-native glib-2.0-native"

inherit autotools gettext pkgconfig

SRC_URI = "${SOURCEFORGE_MIRROR}/sipe/pidgin-sipe-${PV}.tar.xz \
           file://0001-sipe-consider-64bit-time_t-when-printing.patch \
           file://0001-Align-structs-casts-with-time_t-elements-to-8byte-bo.patch \
"

SRC_URI[md5sum] = "0e742f021dc8c3f17435aea05c3e0314"
SRC_URI[sha256sum] = "738b121b11f2b3f1744150c00cb381222eb6cf67161a7742797eb4f03e64a2ba"

PACKAGECONFIG ??= "nss krb5"
PACKAGECONFIG[nss] = "--enable-nss=yes,--enable-nss=no,nss"
PACKAGECONFIG[openssl] = "--enable-openssl=yes,--enable-openssl=no,openssl"
PACKAGECONFIG[krb5] = "--with-krb5=yes,--with-krb5=no,krb5"
#PACKAGECONFIG[voice_and_video] = "--with-vv=yes,--with-vv=no,libnice gstreamer"
PACKAGECONFIG[telepathy] = "--enable-telepathy=yes,--enable-telepathy=no,telepathy-glib"
#PACKAGECONFIG[gssapi_only] = "--enable-gssapi-only=yes,--enable-gssapi-only=no,krb5"
PACKAGECONFIG[debug] = "--enable-debug=yes,--enable-debug=no,valgrind"

FILES_${PN}-dev += " \
    ${libdir}/purple-2/*.la \
"

FILES_${PN} += " \
    ${libdir}/purple-2/libsipe.so \
    ${datadir}/appdata \
    ${datadir}/metainfo \
"
