SUMMARY = "Protocol plugin for Office 365/Lync/OCS for Adium, Pidgin, Miranda and Telepathy IM Framework"
SECTION = "webos/services"
LICENSE = "GPLv2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "pidgin gmime intltool-native glib-2.0-native"

inherit pkgconfig
inherit autotools

SRC_URI = "${SOURCEFORGE_MIRROR}/sipe/pidgin-sipe-${PV}.tar.xz \
"

SRC_URI[md5sum] = "b91106d28c235b347a63dcb676f7b66a"
SRC_URI[sha256sum] = "958803722b23d869131f76bd90df9da19116d4ca5a873e5253371479b7390f43"

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
