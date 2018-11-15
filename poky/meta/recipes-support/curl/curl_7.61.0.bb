SUMMARY = "Command line tool and library for client-side URL transfers"
HOMEPAGE = "http://curl.haxx.se/"
BUGTRACKER = "http://curl.haxx.se/mail/list.cgi?list=curl-tracker"
SECTION = "console/network"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ef889a37a5a874490ac7ce116396f29a"

SRC_URI = "http://curl.haxx.se/download/curl-${PV}.tar.bz2 \
           file://0001-replace-krb5-config-with-pkg-config.patch \
           file://CVE-2018-14618.patch \
           file://CVE-2018-16839.patch \
           file://CVE-2018-16840.patch \
           file://CVE-2018-16842.patch \
"

SRC_URI[md5sum] = "31d0a9f48dc796a7db351898a1e5058a"
SRC_URI[sha256sum] = "5f6f336921cf5b84de56afbd08dfb70adeef2303751ffb3e570c936c6d656c9c"

CVE_PRODUCT = "curl libcurl"
inherit autotools pkgconfig binconfig multilib_header

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} gnutls proxy threaded-resolver zlib"
PACKAGECONFIG_class-native = "ipv6 proxy ssl threaded-resolver zlib"
PACKAGECONFIG_class-nativesdk = "ipv6 proxy ssl threaded-resolver zlib"

# 'ares' and 'threaded-resolver' are mutually exclusive
PACKAGECONFIG[ares] = "--enable-ares,--disable-ares,c-ares"
PACKAGECONFIG[dict] = "--enable-dict,--disable-dict,"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[gopher] = "--enable-gopher,--disable-gopher,"
PACKAGECONFIG[imap] = "--enable-imap,--disable-imap,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[krb5] = "--with-gssapi,--without-gssapi,krb5"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,"
PACKAGECONFIG[ldaps] = "--enable-ldaps,--disable-ldaps,"
PACKAGECONFIG[libidn] = "--with-libidn2,--without-libidn2,libidn2"
PACKAGECONFIG[libssh2] = "--with-libssh2,--without-libssh2,libssh2"
PACKAGECONFIG[nghttp2] = "--with-nghttp2,--without-nghttp2,nghttp2"
PACKAGECONFIG[pop3] = "--enable-pop3,--disable-pop3,"
PACKAGECONFIG[proxy] = "--enable-proxy,--disable-proxy,"
PACKAGECONFIG[rtmpdump] = "--with-librtmp,--without-librtmp,rtmpdump"
PACKAGECONFIG[rtsp] = "--enable-rtsp,--disable-rtsp,"
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb,"
PACKAGECONFIG[smtp] = "--enable-smtp,--disable-smtp,"
PACKAGECONFIG[ssl] = "--with-ssl --with-random=/dev/urandom,--without-ssl,openssl"
PACKAGECONFIG[telnet] = "--enable-telnet,--disable-telnet,"
PACKAGECONFIG[tftp] = "--enable-tftp,--disable-tftp,"
PACKAGECONFIG[threaded-resolver] = "--enable-threaded-resolver,--disable-threaded-resolver"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_LIBDIR}/../,--without-zlib,zlib"

EXTRA_OECONF = " \
    --enable-crypto-auth \
    --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
    --without-libmetalink \
    --without-libpsl \
"

do_install_append_class-target() {
	# cleanup buildpaths from curl-config
	sed -i \
	    -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's,--with-libtool-sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    ${D}${bindir}/curl-config
}

PACKAGES =+ "lib${BPN}"

FILES_lib${BPN} = "${libdir}/lib*.so.*"
RRECOMMENDS_lib${BPN} += "ca-certificates"

FILES_${PN} += "${datadir}/zsh"

BBCLASSEXTEND = "native nativesdk"
