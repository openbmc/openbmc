SUMMARY = "Command line tool and library for client-side URL transfers"
DESCRIPTION = "It uses URL syntax to transfer data to and from servers. \
curl is a widely used because of its ability to be flexible and complete \
complex tasks. For example, you can use curl for things like user authentication, \
HTTP post, SSL connections, proxy support, FTP uploads, and more!"
HOMEPAGE = "https://curl.se/"
BUGTRACKER = "https://github.com/curl/curl/issues"
SECTION = "console/network"
LICENSE = "curl"
LIC_FILES_CHKSUM = "file://COPYING;md5=eed2e5088e1ac619c9a1c747da291d75"

SRC_URI = " \
    https://curl.se/download/${BP}.tar.xz \
    file://run-ptest \
    file://disable-tests \
    file://no-test-timeout.patch \
"
SRC_URI[sha256sum] = "c7ca7db48b0909743eaef34250da02c19bc61d4f1dcedd6603f109409536ab56"

# Curl has used many names over the years...
CVE_PRODUCT = "haxx:curl haxx:libcurl curl:curl curl:libcurl libcurl:libcurl daniel_stenberg:curl"
CVE_STATUS[CVE-2024-32928] = "ignored: CURLOPT_SSL_VERIFYPEER was disabled on google cloud services causing a potential man in the middle attack"

inherit autotools pkgconfig binconfig multilib_header ptest

COMMON_PACKAGECONFIG = "basic-auth bearer-auth digest-auth ipfs negotiate-auth openssl proxy threaded-resolver verbose zlib"
PACKAGECONFIG ??= "${COMMON_PACKAGECONFIG} ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} aws libidn"
PACKAGECONFIG:class-native = "${COMMON_PACKAGECONFIG} ipv6"
PACKAGECONFIG:class-nativesdk = "${COMMON_PACKAGECONFIG} ipv6"

# 'ares' and 'threaded-resolver' are mutually exclusive
PACKAGECONFIG[ares] = "--enable-ares,--disable-ares,c-ares,,,threaded-resolver"
PACKAGECONFIG[aws] = "--enable-aws,--disable-aws"
PACKAGECONFIG[basic-auth] = "--enable-basic-auth,--disable-basic-auth"
PACKAGECONFIG[bearer-auth] = "--enable-bearer-auth,--disable-bearer-auth"
PACKAGECONFIG[brotli] = "--with-brotli,--without-brotli,brotli"
PACKAGECONFIG[builtinmanual] = "--enable-manual,--disable-manual"
# Don't use this in production
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[dict] = "--enable-dict,--disable-dict,"
PACKAGECONFIG[digest-auth] = "--enable-digest-auth,--disable-digest-auth"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[gopher] = "--enable-gopher,--disable-gopher,"
PACKAGECONFIG[imap] = "--enable-imap,--disable-imap,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[ipfs] = "--enable-ipfs,--disable-ipfs,"
PACKAGECONFIG[kerberos-auth] = "--enable-kerberos-auth,--disable-kerberos-auth"
PACKAGECONFIG[krb5] = "--with-gssapi,--without-gssapi,krb5"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,openldap"
PACKAGECONFIG[ldaps] = "--enable-ldaps,--disable-ldaps,openldap"
PACKAGECONFIG[libgsasl] = "--with-libgsasl,--without-libgsasl,libgsasl"
PACKAGECONFIG[libidn] = "--with-libidn2,--without-libidn2,libidn2"
PACKAGECONFIG[libssh2] = "--with-libssh2,--without-libssh2,libssh2"
PACKAGECONFIG[mbedtls] = "--with-mbedtls=${STAGING_DIR_TARGET},--without-mbedtls,mbedtls"
PACKAGECONFIG[mqtt] = "--enable-mqtt,--disable-mqtt,"
PACKAGECONFIG[negotiate-auth] = "--enable-negotiate-auth,--disable-negotiate-auth"
PACKAGECONFIG[nghttp2] = "--with-nghttp2,--without-nghttp2,nghttp2"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl"
PACKAGECONFIG[pop3] = "--enable-pop3,--disable-pop3,"
PACKAGECONFIG[proxy] = "--enable-proxy,--disable-proxy,"
PACKAGECONFIG[rtmpdump] = "--with-librtmp,--without-librtmp,rtmpdump"
PACKAGECONFIG[rtsp] = "--enable-rtsp,--disable-rtsp,"
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb,"
PACKAGECONFIG[smtp] = "--enable-smtp,--disable-smtp,"
PACKAGECONFIG[telnet] = "--enable-telnet,--disable-telnet,"
PACKAGECONFIG[tftp] = "--enable-tftp,--disable-tftp,"
PACKAGECONFIG[threaded-resolver] = "--enable-threaded-resolver,--disable-threaded-resolver,,,,ares"
PACKAGECONFIG[verbose] = "--enable-verbose,--disable-verbose"
PACKAGECONFIG[websockets] = "--enable-websockets,--disable-websockets"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_LIBDIR}/../,--without-zlib,zlib"
PACKAGECONFIG[zstd] = "--with-zstd,--without-zstd,zstd"

EXTRA_OECONF = " \
    --disable-libcurl-option \
    --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
    --without-libpsl \
    --enable-optimize \
    ${@'--without-ssl' if (bb.utils.filter('PACKAGECONFIG', 'gnutls mbedtls openssl', d) == '') else ''} \
"

fix_absolute_paths () {
	# cleanup buildpaths from curl-config
	sed -i \
	    -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's,--with-libtool-sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's|${@" ".join(d.getVar("DEBUG_PREFIX_MAP").split())}||g' \
	    ${D}${bindir}/curl-config
}

do_install:append:class-target() {
	fix_absolute_paths
}

do_install:append:class-nativesdk() {
	fix_absolute_paths
}

do_compile_ptest() {
	oe_runmake -C ${B}/tests
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp ${S}/tests/*.p[lmy] ${D}${PTEST_PATH}/tests/

	install -d ${D}${PTEST_PATH}/tests/libtest
	for name in $(makefile-getvar ${B}/tests/libtest/Makefile noinst_PROGRAMS noinst_LTLIBRARIES); do
		${B}/libtool --mode=install install ${B}/tests/libtest/$name ${D}${PTEST_PATH}/tests/libtest
	done
	rm -f ${D}${PTEST_PATH}/tests/libtest/libhostname.la

	install -d ${D}${PTEST_PATH}/tests/server
	for name in $(makefile-getvar ${B}/tests/server/Makefile noinst_PROGRAMS); do
		${B}/libtool --mode=install install ${B}/tests/server/$name ${D}${PTEST_PATH}/tests/server
	done

	cp -r ${S}/tests/data ${D}${PTEST_PATH}/tests/

	# More tests that we disable for automated QA as they're not reliable
	cat ${UNPACKDIR}/disable-tests >>${D}${PTEST_PATH}/tests/data/DISABLED
}

RDEPENDS:${PN}-ptest += " \
	locale-base-en-us \
	perl-module-b \
	perl-module-base \
	perl-module-cwd \
	perl-module-digest \
	perl-module-digest-md5 \
	perl-module-digest-sha \
	perl-module-file-basename \
	perl-module-file-spec \
	perl-module-file-temp \
	perl-module-i18n-langinfo \
	perl-module-io-socket \
	perl-module-ipc-open2 \
	perl-module-list-util \
	perl-module-memoize \
	perl-module-storable \
	perl-module-time-hires \
"

PACKAGES =+ "lib${BPN}"

FILES:lib${BPN} = "${libdir}/lib*.so.*"
RRECOMMENDS:lib${BPN} += "ca-certificates"

FILES:${PN} += "${datadir}/zsh"

inherit multilib_script
MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/curl-config"

BBCLASSEXTEND = "native nativesdk"
