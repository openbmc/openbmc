SUMMARY = "Secure Socket Layer"
DESCRIPTION = "Secure Socket Layer (SSL) binary and related cryptographic tools."
HOMEPAGE = "http://www.openssl.org/"
BUGTRACKER = "http://www.openssl.org/news/vulnerabilities.html"
SECTION = "libs/network"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c75985e733726beaba57bc5253e96d04"

SRC_URI = "http://www.openssl.org/source/openssl-${PV}.tar.gz \
           file://run-ptest \
           file://0001-buildinfo-strip-sysroot-and-debug-prefix-map-from-co.patch \
           file://0001-Configure-do-not-tweak-mips-cflags.patch \
           file://0001-Added-handshake-history-reporting-when-test-fails.patch \
           file://0001-Fix-builds-on-riscv64-using-musl.patch \
           "

SRC_URI:append:class-nativesdk = " \
           file://environment.d-openssl.sh \
           "

SRC_URI[sha256sum] = "e15dda82fe2fe8139dc2ac21a36d4ca01d5313c75f99f46c4e8a27709b7294bf"

inherit lib_package multilib_header multilib_script ptest perlnative manpages
MULTILIB_SCRIPTS = "${PN}-bin:${bindir}/c_rehash"

PACKAGECONFIG ?= ""
PACKAGECONFIG:class-native = ""
PACKAGECONFIG:class-nativesdk = ""

PACKAGECONFIG[cryptodev-linux] = "enable-devcryptoeng,disable-devcryptoeng,cryptodev-linux,,cryptodev-module"
PACKAGECONFIG[no-tls1] = "no-tls1"
PACKAGECONFIG[no-tls1_1] = "no-tls1_1"
PACKAGECONFIG[manpages] = ""

B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

EXTRA_OECONF = "${@bb.utils.contains('PTEST_ENABLED', '1', '', 'no-tests', d)}"

#| ./libcrypto.so: undefined reference to `getcontext'
#| ./libcrypto.so: undefined reference to `setcontext'
#| ./libcrypto.so: undefined reference to `makecontext'
EXTRA_OECONF:append:libc-musl = " no-async"
EXTRA_OECONF:append:libc-musl:powerpc64 = " no-asm"

# adding devrandom prevents openssl from using getrandom() which is not available on older glibc versions
# (native versions can be built with newer glibc, but then relocated onto a system with older glibc)
EXTRA_OECONF:append:class-native = " --with-rand-seed=os,devrandom"
EXTRA_OECONF:append:class-nativesdk = " --with-rand-seed=os,devrandom"

# Relying on hardcoded built-in paths causes openssl-native to not be relocateable from sstate.
CFLAGS:append:class-native = " -DOPENSSLDIR=/not/builtin -DENGINESDIR=/not/builtin"
CFLAGS:append:class-nativesdk = " -DOPENSSLDIR=/not/builtin -DENGINESDIR=/not/builtin"

# This allows disabling deprecated or undesirable crypto algorithms.
# The default is to trust upstream choices.
DEPRECATED_CRYPTO_FLAGS ?= ""

do_configure () {
	# When we upgrade glibc but not uninative we see obtuse failures in openssl. Make
	# the issue really clear that perl isn't functional due to symbol mismatch issues.
	cat <<- EOF > ${WORKDIR}/perltest
	#!/usr/bin/env perl
	use POSIX;
	EOF
	chmod a+x ${WORKDIR}/perltest
	${WORKDIR}/perltest

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
	linux-arc | linux-microblaze*)
		target=linux-latomic
		;;
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
	linux-loongarch64)
		target=linux64-loongarch64
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
	linux-nios2* | linux-sh3 | linux-sh4 | linux-arc*)
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
		target=linux32-riscv32
		;;
	linux-riscv64)
		target=linux64-riscv64
		;;
	linux-sparc | linux-supersparc)
		target=linux-sparcv9
		;;
	mingw32-x86_64)
		target=mingw64
		;;
	esac

	# WARNING: do not set compiler/linker flags (-I/-D etc.) in EXTRA_OECONF, as they will fully replace the
	# environment variables set by bitbake. Adjust the environment variables instead.
	PERLEXTERNAL="$(realpath ${S}/external/perl/Text-Template-*/lib)"
	test -d "$PERLEXTERNAL" || bberror "PERLEXTERNAL '$PERLEXTERNAL' not found!"
	HASHBANGPERL="/usr/bin/env perl" PERL=perl PERL5LIB="$PERLEXTERNAL" \
	perl ${S}/Configure ${EXTRA_OECONF} ${PACKAGECONFIG_CONFARGS} ${DEPRECATED_CRYPTO_FLAGS} --prefix=${prefix} --openssldir=${libdir}/ssl-3 --libdir=${baselib} $target
	perl ${B}/configdata.pm --dump
}

do_compile:append () {
	# The test suite binaries are large and we don't need the debugging in them
	if test -d ${B}/test; then
		find ${B}/test -type f -executable -exec ${STRIP} {} \;
	fi
}

do_install () {
	oe_runmake DESTDIR="${D}" MANDIR="${mandir}" MANSUFFIX=ssl install_sw install_ssldirs ${@bb.utils.contains('PACKAGECONFIG', 'manpages', 'install_docs', '', d)}

	oe_multilib_header openssl/opensslconf.h
	oe_multilib_header openssl/configuration.h

	# Create SSL structure for packages such as ca-certificates which
	# contain hard-coded paths to /etc/ssl. Debian does the same.
	install -d ${D}${sysconfdir}/ssl
	mv ${D}${libdir}/ssl-3/certs \
	   ${D}${libdir}/ssl-3/private \
	   ${D}${libdir}/ssl-3/openssl.cnf \
	   ${D}${sysconfdir}/ssl/

	# Although absolute symlinks would be OK for the target, they become
	# invalid if native or nativesdk are relocated from sstate.
	ln -sf ${@oe.path.relative('${libdir}/ssl-3', '${sysconfdir}/ssl/certs')} ${D}${libdir}/ssl-3/certs
	ln -sf ${@oe.path.relative('${libdir}/ssl-3', '${sysconfdir}/ssl/private')} ${D}${libdir}/ssl-3/private
	ln -sf ${@oe.path.relative('${libdir}/ssl-3', '${sysconfdir}/ssl/openssl.cnf')} ${D}${libdir}/ssl-3/openssl.cnf
}

do_install:append:class-native () {
	create_wrapper ${D}${bindir}/openssl \
	    OPENSSL_CONF=${libdir}/ssl-3/openssl.cnf \
	    SSL_CERT_DIR=${libdir}/ssl-3/certs \
	    SSL_CERT_FILE=${libdir}/ssl-3/cert.pem \
	    OPENSSL_ENGINES=${libdir}/engines-3 \
	    OPENSSL_MODULES=${libdir}/ossl-modules
}

do_install:append:class-nativesdk () {
	mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
	install -m 644 ${UNPACKDIR}/environment.d-openssl.sh ${D}${SDKPATHNATIVE}/environment-setup.d/openssl.sh
	sed 's|/usr/lib/ssl/|/usr/lib/ssl-3/|g' -i ${D}${SDKPATHNATIVE}/environment-setup.d/openssl.sh
}

PTEST_BUILD_HOST_FILES += "configdata.pm"
PTEST_BUILD_HOST_PATTERN = "perl_version ="
do_install_ptest() {
	install -m644 ${S}/Configure ${B}/configdata.pm ${D}${PTEST_PATH}
	cp -rf ${S}/Configurations ${S}/external ${D}${PTEST_PATH}/

	install -d ${D}${PTEST_PATH}/apps
	ln -s ${bindir}/openssl ${D}${PTEST_PATH}/apps

	cd ${S}
	find test/certs test/ct test/d2i-tests test/recipes test/ocsp-tests test/ssl-tests test/smime-certs -type f -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;
	find apps test -name \*.cnf -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;
	find apps test -name \*.der -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;
	find apps test -name \*.pem -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;
	find util -name \*.p[lm] -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;

	cd ${B}
	# Everything but .? (.o and .d)
	find test -type f -name \*[^.]? -exec install -m755 -D {} ${D}${PTEST_PATH}/{} \;
	find apps test -name \*.cnf -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;
	find apps test -name \*.pem -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;
	find apps test -name \*.srl -exec install -m644 -D {} ${D}${PTEST_PATH}/{} \;
	install -m755 ${B}/util/*wrap.* ${D}${PTEST_PATH}/util/

	install -m755 ${B}/apps/CA.pl ${D}${PTEST_PATH}/apps/
	install -m755 ${S}/test/*.pl ${D}${PTEST_PATH}/test/
	install -m755 ${S}/test/shibboleth.pfx ${D}${PTEST_PATH}/test/
	install -m755 ${S}/test/*.bin ${D}${PTEST_PATH}/test/
	install -m755 ${S}/test/dane*.in ${D}${PTEST_PATH}/test/
	install -m755 ${S}/test/smcont*.txt ${D}${PTEST_PATH}/test/
	install -m755 ${S}/test/ssl_test.tmpl ${D}${PTEST_PATH}/test/

	sed 's|${S}|${PTEST_PATH}|g' -i ${D}${PTEST_PATH}/configdata.pm ${D}${PTEST_PATH}/util/wrap.pl

	install -d ${D}${PTEST_PATH}/engines
	install -m755 ${B}/engines/dasync.so ${D}${PTEST_PATH}/engines/
	install -m755 ${B}/engines/ossltest.so ${D}${PTEST_PATH}/engines/
	ln -s ${libdir}/engines-3/loader_attic.so ${D}${PTEST_PATH}/engines/
	ln -s ${libdir}/ossl-modules/ ${D}${PTEST_PATH}/providers
}

# Add the openssl.cnf file to the openssl-conf package. Make the libcrypto
# package RRECOMMENDS on this package. This will enable the configuration
# file to be installed for both the openssl-bin package and the libcrypto
# package since the openssl-bin package depends on the libcrypto package.

PACKAGES =+ "libcrypto libssl openssl-conf ${PN}-engines ${PN}-misc ${PN}-ossl-module-legacy"

FILES:libcrypto = "${libdir}/libcrypto${SOLIBS}"
FILES:libssl = "${libdir}/libssl${SOLIBS}"
FILES:openssl-conf = "${sysconfdir}/ssl/openssl.cnf \
                      ${libdir}/ssl-3/openssl.cnf* \
                      "
FILES:${PN}-engines = "${libdir}/engines-3"
# ${prefix} comes from what we pass into --prefix at configure time (which is used for INSTALLTOP)
FILES:${PN}-engines:append:mingw32:class-nativesdk = " ${prefix}${libdir}/engines-3"
FILES:${PN}-misc = "${libdir}/ssl-3/misc ${bindir}/c_rehash"
FILES:${PN}-ossl-module-legacy = "${libdir}/ossl-modules/legacy.so"
FILES:${PN} =+ "${libdir}/ssl-3/* ${libdir}/ossl-modules/"
FILES:${PN}:append:class-nativesdk = " ${SDKPATHNATIVE}/environment-setup.d/openssl.sh"

CONFFILES:openssl-conf = "${sysconfdir}/ssl/openssl.cnf"

RRECOMMENDS:libcrypto += "openssl-conf ${PN}-ossl-module-legacy"
RDEPENDS:${PN}-misc = "perl"
RDEPENDS:${PN}-ptest += "openssl-bin perl perl-modules bash sed openssl-engines openssl-ossl-module-legacy"

RDEPENDS:${PN}-bin += "openssl-conf"

# The test suite is installed stripped
INSANE_SKIP:${PN} = "already-stripped"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT = "openssl:openssl"
