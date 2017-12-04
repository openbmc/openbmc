SUMMARY = "Command line tool and library for client-side URL transfers"
HOMEPAGE = "http://curl.haxx.se/"
BUGTRACKER = "http://curl.haxx.se/mail/list.cgi?list=curl-tracker"
SECTION = "console/network"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;beginline=8;md5=3a34942f4ae3fbf1a303160714e664ac"

SRC_URI = "http://curl.haxx.se/download/curl-${PV}.tar.bz2 \
           file://0001-replace-krb5-config-with-pkg-config.patch \
"

# curl likes to set -g0 in CFLAGS, so we stop it
# from mucking around with debug options
#
SRC_URI += " file://configure_ac.patch"

SRC_URI[md5sum] = "015f6a0217ca6f2c5442ca406476920b"
SRC_URI[sha256sum] = "3c12c5f54ccaa1d40abc65d672107dcc75d3e1fcb38c267484334280096e5156"

CVE_PRODUCT = "libcurl"
inherit autotools pkgconfig binconfig multilib_header

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "ipv6", "", d)} gnutls proxy zlib"
PACKAGECONFIG_class-native = "ipv6 proxy ssl zlib"
PACKAGECONFIG_class-nativesdk = "ipv6 proxy ssl zlib"

PACKAGECONFIG[dict] = "--enable-dict,--disable-dict,"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[gopher] = "--enable-gopher,--disable-gopher,"
PACKAGECONFIG[imap] = "--enable-imap,--disable-imap,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,"
PACKAGECONFIG[ldaps] = "--enable-ldaps,--disable-ldaps,"
PACKAGECONFIG[libidn] = "--with-libidn,--without-libidn,libidn"
PACKAGECONFIG[libssh2] = "--with-libssh2,--without-libssh2,libssh2"
PACKAGECONFIG[pop3] = "--enable-pop3,--disable-pop3,"
PACKAGECONFIG[proxy] = "--enable-proxy,--disable-proxy,"
PACKAGECONFIG[rtmpdump] = "--with-librtmp,--without-librtmp,rtmpdump"
PACKAGECONFIG[rtsp] = "--enable-rtsp,--disable-rtsp,"
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb,"
PACKAGECONFIG[smtp] = "--enable-smtp,--disable-smtp,"
PACKAGECONFIG[ssl] = "--with-ssl --with-random=/dev/urandom,--without-ssl,openssl"
PACKAGECONFIG[telnet] = "--enable-telnet,--disable-telnet,"
PACKAGECONFIG[tftp] = "--enable-tftp,--disable-tftp,"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_LIBDIR}/../,--without-zlib,zlib"
PACKAGECONFIG[krb5] = "--with-gssapi,--without-gssapi,krb5"

EXTRA_OECONF = " \
    --enable-crypto-auth \
    --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
    --without-libmetalink \
    --without-libpsl \
    --without-nghttp2 \
"

do_install_append() {
	oe_multilib_header curl/curlbuild.h
}

do_install_append_class-target() {
	# cleanup buildpaths from curl-config
	sed -i -e 's,${STAGING_DIR_HOST},,g' ${D}${bindir}/curl-config
}

PACKAGES =+ "lib${BPN}"

FILES_lib${BPN} = "${libdir}/lib*.so.*"
RRECOMMENDS_lib${BPN} += "ca-certificates"

FILES_${PN} += "${datadir}/zsh"

BBCLASSEXTEND = "native nativesdk"
