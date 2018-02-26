SUMMARY = "Secure Socket Layer"
DESCRIPTION = "Secure Socket Layer (SSL) binary and related cryptographic tools."
HOMEPAGE = "http://www.openssl.org/"
BUGTRACKER = "http://www.openssl.org/news/vulnerabilities.html"
SECTION = "libs/network"

# "openssl | SSLeay" dual license
LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cae6da10f4ffd9703214776d2aabce32"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "ba5f1b8b835b88cadbce9b35ed9531a6"
SRC_URI[sha256sum] = "de4d501267da39310905cb6dc8c6121f7a2cad45a7707f76df828fe1b85073af"

SRC_URI = "http://www.openssl.org/source/openssl-${PV}.tar.gz \
           file://run-ptest \
           file://openssl-c_rehash.sh \
           file://0001-Take-linking-flags-from-LDFLAGS-env-var.patch \
           file://0001-Remove-test-that-requires-running-as-non-root.patch \
           file://0001-aes-asm-aes-armv4-bsaes-armv7-.pl-make-it-work-with-.patch \
          "

S = "${WORKDIR}/openssl-${PV}"

inherit lib_package multilib_header ptest

do_configure () {
	os=${HOST_OS}
	case $os in
	linux-uclibc |\
	linux-uclibceabi |\
	linux-gnueabi |\
	linux-uclibcspe |\
	linux-gnuspe |\
	linux-musl*)
		os=linux
		;;
		*)
		;;
	esac
	target="$os-${HOST_ARCH}"
	case $target in
	linux-arm)
		target=linux-armv4
		;;
	linux-armeb)
		target=linux-armv4
		;;
	linux-aarch64*)
		target=linux-aarch64
		;;
	linux-sh3)
		target=linux-generic32
		;;
	linux-sh4)
		target=linux-generic32
		;;
	linux-i486)
		target=linux-elf
		;;
	linux-i586 | linux-viac3)
		target=linux-elf
		;;
	linux-i686)
		target=linux-elf
		;;
	linux-gnux32-x86_64)
		target=linux-x32
		;;
	linux-gnu64-x86_64)
		target=linux-x86_64
		;;
	linux-mips)
                # specifying TARGET_CC_ARCH prevents openssl from (incorrectly) adding target architecture flags
		target="linux-mips32 ${TARGET_CC_ARCH}"
		;;
	linux-mipsel)
		target="linux-mips32 ${TARGET_CC_ARCH}"
		;;
        linux-gnun32-mips*)
               target=linux-mips64
                ;;
        linux-*-mips64 | linux-mips64)
               target=linux64-mips64
                ;;
        linux-*-mips64el | linux-mips64el)
               target=linux64-mips64
                ;;
	linux-microblaze*|linux-nios2*)
		target=linux-generic32
		;;
	linux-powerpc)
		target=linux-ppc
		;;
	linux-powerpc64)
		target=linux-ppc64
		;;
	linux-supersparc)
		target=linux-sparcv9
		;;
	linux-sparc)
		target=linux-sparcv9
		;;
	darwin-i386)
		target=darwin-i386-cc
		;;
	esac
        useprefix=${prefix}
        if [ "x$useprefix" = "x" ]; then
                useprefix=/
        fi
	perl ./Configure ${EXTRA_OECONF} --prefix=$useprefix --openssldir=${libdir}/ssl-1.1 --libdir=`basename ${libdir}` $target
}

#| engines/afalg/e_afalg.c: In function 'eventfd':
#| engines/afalg/e_afalg.c:110:20: error: '__NR_eventfd' undeclared (first use in this function)
#|      return syscall(__NR_eventfd, n);
#|                     ^~~~~~~~~~~~
EXTRA_OECONF_aarch64 += "no-afalgeng"

#| ./libcrypto.so: undefined reference to `getcontext'
#| ./libcrypto.so: undefined reference to `setcontext'
#| ./libcrypto.so: undefined reference to `makecontext'
EXTRA_OECONF_libc-musl += "-DOPENSSL_NO_ASYNC"

do_install () {
        oe_runmake DESTDIR="${D}" MANDIR="${mandir}" MANSUFFIX=ssl install
        oe_multilib_header openssl/opensslconf.h
}

do_install_append_class-native () {
        # Install a custom version of c_rehash that can handle sysroots properly.
        # This version is used for example when installing ca-certificates during
        # image creation.
        install -Dm 0755 ${WORKDIR}/openssl-c_rehash.sh ${D}${bindir}/c_rehash
        sed -i -e 's,/etc/openssl,${sysconfdir}/ssl,g' ${D}${bindir}/c_rehash
}

do_install_ptest() {
        cp -r * ${D}${PTEST_PATH}

        # Putting .so files in ptest package will mess up the dependencies of the main openssl package
        # so we rename them to .so.ptest and patch the test accordingly
        mv ${D}${PTEST_PATH}/libcrypto.so ${D}${PTEST_PATH}/libcrypto.so.ptest
        mv ${D}${PTEST_PATH}/libssl.so ${D}${PTEST_PATH}/libssl.so.ptest
        sed -i 's/$target{shared_extension_simple}/".so.ptest"/' ${D}${PTEST_PATH}/test/recipes/90-test_shlibload.t
}

RDEPENDS_${PN}-ptest += "perl-module-file-spec-functions bash python"

FILES_${PN} =+ " ${libdir}/ssl-1.1/*"

PACKAGES =+ "${PN}-engines"
FILES_${PN}-engines = "${libdir}/engines-1.1"

