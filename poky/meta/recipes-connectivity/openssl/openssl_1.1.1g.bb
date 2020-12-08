SUMMARY = "Secure Socket Layer"
DESCRIPTION = "Secure Socket Layer (SSL) binary and related cryptographic tools."
HOMEPAGE = "http://www.openssl.org/"
BUGTRACKER = "http://www.openssl.org/news/vulnerabilities.html"
SECTION = "libs/network"

# "openssl" here actually means both OpenSSL and SSLeay licenses apply
# (see meta/files/common-licenses/OpenSSL to which "openssl" is SPDXLICENSEMAPped)
LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d343e62fc9c833710bbbed25f27364c8"

DEPENDS = "hostperl-runtime-native"

SRC_URI = "http://www.openssl.org/source/openssl-${PV}.tar.gz \
           file://run-ptest \
           file://0001-skip-test_symbol_presence.patch \
           file://0001-buildinfo-strip-sysroot-and-debug-prefix-map-from-co.patch \
           file://afalg.patch \
           file://reproducible.patch \
           "

SRC_URI_append_class-nativesdk = " \
           file://environment.d-openssl.sh \
           "

SRC_URI[sha256sum] = "ddb04774f1e32f0c49751e21b67216ac87852ceb056b75209af2443400636d46"

inherit lib_package multilib_header multilib_script ptest
MULTILIB_SCRIPTS = "${PN}-bin:${bindir}/c_rehash"

PACKAGECONFIG ?= ""
PACKAGECONFIG_class-native = ""
PACKAGECONFIG_class-nativesdk = ""

PACKAGECONFIG[cryptodev-linux] = "enable-devcryptoeng,disable-devcryptoeng,cryptodev-linux,,cryptodev-module"

B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

#| ./libcrypto.so: undefined reference to `getcontext'
#| ./libcrypto.so: undefined reference to `setcontext'
#| ./libcrypto.so: undefined reference to `makecontext'
EXTRA_OECONF_append_libc-musl = " no-async"
EXTRA_OECONF_append_libc-musl_powerpc64 = " no-asm"

# adding devrandom prevents openssl from using getrandom() which is not available on older glibc versions
# (native versions can be built with newer glibc, but then relocated onto a system with older glibc)
EXTRA_OECONF_class-native = "--with-rand-seed=os,devrandom"
EXTRA_OECONF_class-nativesdk = "--with-rand-seed=os,devrandom"

# Relying on hardcoded built-in paths causes openssl-native to not be relocateable from sstate.
CFLAGS_append_class-native = " -DOPENSSLDIR=/not/builtin -DENGINESDIR=/not/builtin"
CFLAGS_append_class-nativesdk = " -DOPENSSLDIR=/not/builtin -DENGINESDIR=/not/builtin"

do_configure () {
	os=${HOST_OS}
	case $os in
	linux-gnueabi |\
	linux-gnuspe |\
	linux-musleabi |\
	linux-muslspe |\
	linux-musl )
		os=linux
		;;
	*)
		;;
	esac
	target="$os-${HOST_ARCH}"
	case $target in
	linux-arm*)
		target=linux-armv4
		;;
	linux-aarch64*)
		target=linux-aarch64
		;;
	linux-i?86 | linux-viac3)
		target=linux-x86
		;;
	linux-gnux32-x86_64 | linux-muslx32-x86_64 )
		target=linux-x32
		;;
	linux-gnu64-x86_64)
		target=linux-x86_64
		;;
	linux-mips | linux-mipsel)
		# specifying TARGET_CC_ARCH prevents openssl from (incorrectly) adding target architecture flags
		target="linux-mips32 ${TARGET_CC_ARCH}"
		;;
	linux-gnun32-mips*)
		target=linux-mips64
		;;
	linux-*-mips64 | linux-mips64 | linux-*-mips64el | linux-mips64el)
		target=linux64-mips64
		;;
	linux-microblaze* | linux-nios2* | linux-sh3 | linux-sh4 | linux-arc*)
		target=linux-generic32
		;;
	linux-powerpc)
		target=linux-ppc
		;;
	linux-powerpc64)
		target=linux-ppc64
		;;
	linux-powerpc64le)
		target=linux-ppc64le
		;;
	linux-riscv32)
		target=linux-generic32
		;;
	linux-riscv64)
		target=linux-generic64
		;;
	linux-sparc | linux-supersparc)
		target=linux-sparcv9
		;;
	esac

	useprefix=${prefix}
	if [ "x$useprefix" = "x" ]; then
		useprefix=/
	fi
	# WARNING: do not set compiler/linker flags (-I/-D etc.) in EXTRA_OECONF, as they will fully replace the
	# environment variables set by bitbake. Adjust the environment variables instead.
	HASHBANGPERL="/usr/bin/env perl" PERL=perl PERL5LIB="${S}/external/perl/Text-Template-1.46/lib/" \
	perl ${S}/Configure ${EXTRA_OECONF} ${PACKAGECONFIG_CONFARGS} --prefix=$useprefix --openssldir=${libdir}/ssl-1.1 --libdir=${libdir} $target
	perl ${B}/configdata.pm --dump
}

do_install () {
	oe_runmake DESTDIR="${D}" MANDIR="${mandir}" MANSUFFIX=ssl install

	oe_multilib_header openssl/opensslconf.h

	# Create SSL structure for packages such as ca-certificates which
	# contain hard-coded paths to /etc/ssl. Debian does the same.
	install -d ${D}${sysconfdir}/ssl
	mv ${D}${libdir}/ssl-1.1/certs \
	   ${D}${libdir}/ssl-1.1/private \
	   ${D}${libdir}/ssl-1.1/openssl.cnf \
	   ${D}${sysconfdir}/ssl/

	# Although absolute symlinks would be OK for the target, they become
	# invalid if native or nativesdk are relocated from sstate.
	ln -sf ${@oe.path.relative('${libdir}/ssl-1.1', '${sysconfdir}/ssl/certs')} ${D}${libdir}/ssl-1.1/certs
	ln -sf ${@oe.path.relative('${libdir}/ssl-1.1', '${sysconfdir}/ssl/private')} ${D}${libdir}/ssl-1.1/private
	ln -sf ${@oe.path.relative('${libdir}/ssl-1.1', '${sysconfdir}/ssl/openssl.cnf')} ${D}${libdir}/ssl-1.1/openssl.cnf
}

do_install_append_class-native () {
	create_wrapper ${D}${bindir}/openssl \
	    OPENSSL_CONF=${libdir}/ssl-1.1/openssl.cnf \
	    SSL_CERT_DIR=${libdir}/ssl-1.1/certs \
	    SSL_CERT_FILE=${libdir}/ssl-1.1/cert.pem \
	    OPENSSL_ENGINES=${libdir}/engines-1.1
}

do_install_append_class-nativesdk () {
	mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
	install -m 644 ${WORKDIR}/environment.d-openssl.sh ${D}${SDKPATHNATIVE}/environment-setup.d/openssl.sh
	sed 's|/usr/lib/ssl/|/usr/lib/ssl-1.1/|g' -i ${D}${SDKPATHNATIVE}/environment-setup.d/openssl.sh
}

PTEST_BUILD_HOST_FILES += "configdata.pm"
PTEST_BUILD_HOST_PATTERN = "perl_version ="
do_install_ptest () {
	# Prune the build tree
	rm -f ${B}/fuzz/*.* ${B}/test/*.*

	cp ${S}/Configure ${B}/configdata.pm ${D}${PTEST_PATH}
	cp -r ${S}/external ${B}/test ${S}/test ${B}/fuzz ${S}/util ${B}/util ${D}${PTEST_PATH}

	# For test_shlibload
	ln -s ${libdir}/libcrypto.so.1.1 ${D}${PTEST_PATH}/
	ln -s ${libdir}/libssl.so.1.1 ${D}${PTEST_PATH}/

	install -d ${D}${PTEST_PATH}/apps
	ln -s ${bindir}/openssl ${D}${PTEST_PATH}/apps
	install -m644 ${S}/apps/*.pem ${S}/apps/*.srl ${S}/apps/openssl.cnf ${D}${PTEST_PATH}/apps
	install -m755 ${B}/apps/CA.pl ${D}${PTEST_PATH}/apps

	install -d ${D}${PTEST_PATH}/engines
	install -m755 ${B}/engines/ossltest.so ${D}${PTEST_PATH}/engines
}

# Add the openssl.cnf file to the openssl-conf package. Make the libcrypto
# package RRECOMMENDS on this package. This will enable the configuration
# file to be installed for both the openssl-bin package and the libcrypto
# package since the openssl-bin package depends on the libcrypto package.

PACKAGES =+ "libcrypto libssl openssl-conf ${PN}-engines ${PN}-misc"

FILES_libcrypto = "${libdir}/libcrypto${SOLIBS}"
FILES_libssl = "${libdir}/libssl${SOLIBS}"
FILES_openssl-conf = "${sysconfdir}/ssl/openssl.cnf \
                      ${libdir}/ssl-1.1/openssl.cnf* \
                      "
FILES_${PN}-engines = "${libdir}/engines-1.1"
FILES_${PN}-misc = "${libdir}/ssl-1.1/misc"
FILES_${PN} =+ "${libdir}/ssl-1.1/*"
FILES_${PN}_append_class-nativesdk = " ${SDKPATHNATIVE}/environment-setup.d/openssl.sh"

CONFFILES_openssl-conf = "${sysconfdir}/ssl/openssl.cnf"

RRECOMMENDS_libcrypto += "openssl-conf"
RDEPENDS_${PN}-ptest += "openssl-bin perl perl-modules bash"

RDEPENDS_${PN}-bin += "openssl-conf"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT = "openssl:openssl"

# Only affects OpenSSL >= 1.1.1 in combination with Apache < 2.4.37
# Apache in meta-webserver is already recent enough
CVE_CHECK_WHITELIST += "CVE-2019-0190"
