SUMMARY = "mongodb"
LICENSE = "SSPL-1 & Apache-2.0 & Zlib"
LIC_FILES_CHKSUM = "file://LICENSE-Community.txt;md5=3a865f27f11f43ecbe542d9ea387dcf1 \
                    file://APACHE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl libpcap zlib boost curl python3 \
           python3-setuptools-native \
           python3-pyyaml-native python3-cheetah-native \
           python3-psutil-native python3-regex-native \
           "

inherit scons dos2unix siteinfo python3native

PV = "4.2.2"
#v4.2.2
SRCREV = "a0bbbff6ada159e19298d37946ac8dc4b497eadf"
SRC_URI = "git://github.com/mongodb/mongo.git;branch=v4.2;protocol=https \
           file://0001-Tell-scons-to-use-build-settings-from-environment-va.patch \
           file://0001-Use-long-long-instead-of-int64_t.patch \
           file://0001-Use-__GLIBC__-to-control-use-of-gnu_get_libc_version.patch \
           file://0002-Add-a-definition-for-the-macro-__ELF_NATIVE_CLASS.patch \
           file://arm64-support.patch \
           file://0001-IntelRDFPMathLib20U1-Check-for-__DEFINED_wchar_t.patch \
           file://0001-Support-deprecated-resolver-functions.patch \
           file://0003-Fix-unknown-prefix-env.patch \
           file://1296.patch \
           "
SRC_URI_append_libc-musl ="\
           file://0001-Mark-one-of-strerror_r-implementation-glibc-specific.patch \
           file://0002-Fix-default-stack-size-to-256K.patch \
           file://0004-wiredtiger-Disable-strtouq-on-musl.patch \
           "

SRC_URI_append_toolchain-clang = "\
           file://0001-asio-Dont-use-experimental-with-clang.patch \
           "

S = "${WORKDIR}/git"

COMPATIBLE_HOST ?= '(x86_64|i.86|powerpc64|arm|aarch64).*-linux'

PACKAGECONFIG ??= "tcmalloc system-pcre"
# gperftools compilation fails for arm below v7 because of missing support of
# dmb operation. So we use system-allocator instead of tcmalloc
PACKAGECONFIG_remove_armv6 = "tcmalloc"
PACKAGECONFIG_remove_libc-musl = "tcmalloc"
PACKAGECONFIG_remove_riscv64 = "tcmalloc"
PACKAGECONFIG_remove_riscv32 = "tcmalloc"

PACKAGECONFIG[tcmalloc] = "--use-system-tcmalloc,--allocator=system,gperftools,"
PACKAGECONFIG[shell] = ",--js-engine=none,,"
PACKAGECONFIG[system-pcre] = "--use-system-pcre,,libpcre,"

EXTRA_OESCONS = "--prefix=${D}${prefix} \
                 LIBPATH=${STAGING_LIBDIR} \
                 LINKFLAGS='${LDFLAGS}' \
                 CXXFLAGS='${CXXFLAGS}' \
                 TARGET_ARCH=${TARGET_ARCH} \
                 --ssl \
                 --disable-warnings-as-errors \
                 --use-system-zlib \
                 --nostrip \
                 --endian=${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
                 --wiredtiger=${@['off','on'][d.getVar('SITEINFO_BITS') != '32']} \
                 ${PACKAGECONFIG_CONFARGS} \
                 core"

scons_do_compile() {
        ${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} ${EXTRA_OESCONS} || \
        die "scons build execution failed."
}

scons_do_install() {
        ${STAGING_BINDIR_NATIVE}/scons install ${EXTRA_OESCONS}|| \
        die "scons install execution failed."
}
