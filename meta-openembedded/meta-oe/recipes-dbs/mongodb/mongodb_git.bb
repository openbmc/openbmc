SUMMARY = "mongodb"
LICENSE = "SSPL-1 & Apache-2.0 & Zlib"
LIC_FILES_CHKSUM = "file://LICENSE-Community.txt;md5=3a865f27f11f43ecbe542d9ea387dcf1 \
                    file://APACHE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl libpcre libpcap zlib boost curl python \
           python-setuptools-native python-typing-native \
           python-pyyaml-native python-cheetah-native \
           "

inherit scons dos2unix siteinfo pythonnative

PV = "4.0.6+git${SRCPV}"
#v4.0.6
SRCREV = "caa42a1f75a56c7643d0b68d3880444375ec42e3"
SRC_URI = "git://github.com/mongodb/mongo.git;branch=v4.0 \
           file://0001-Tell-scons-to-use-build-settings-from-environment-va.patch \
           file://0001-Use-long-long-instead-of-int64_t.patch \
           file://0001-Use-__GLIBC__-to-control-use-of-gnu_get_libc_version.patch \
           file://0002-Add-a-definition-for-the-macro-__ELF_NATIVE_CLASS.patch \
           file://arm64-support.patch \
           file://0001-IntelRDFPMathLib20U1-Check-for-__DEFINED_wchar_t.patch \
           file://0001-Support-deprecated-resolver-functions.patch \
           "
SRC_URI_append_libc-musl ="\
           file://0002-Fix-default-stack-size-to-256K.patch \
           file://0004-wiredtiger-Disable-strtouq-on-musl.patch \
           "

SRC_URI_append_toolchain-clang = "\
           file://0001-asio-Dont-use-experimental-with-clang.patch \
           "

S = "${WORKDIR}/git"

COMPATIBLE_HOST ?= '(x86_64|i.86|powerpc64|arm|aarch64).*-linux'

COMPATIBLE_HOST_arm = "null"
COMPATIBLE_HOST_libc-musl_x86 = "null"

PACKAGECONFIG ??= "tcmalloc"
# gperftools compilation fails for arm below v7 because of missing support of
# dmb operation. So we use system-allocator instead of tcmalloc
PACKAGECONFIG_remove_armv6 = "tcmalloc"
PACKAGECONFIG_remove_libc-musl = "tcmalloc"

PACKAGECONFIG[tcmalloc] = "--use-system-tcmalloc,--allocator=system,gperftools,"
PACKAGECONFIG[shell] = ",--js-engine=none,,"

EXTRA_OESCONS = "--prefix=${D}${prefix} \
                 LIBPATH=${STAGING_LIBDIR} \
                 LINKFLAGS='${LDFLAGS}' \
                 CXXFLAGS='${CXXFLAGS}' \
                 TARGET_ARCH=${TARGET_ARCH} \
                 --ssl \
                 --disable-warnings-as-errors \
                 --use-system-pcre \
                 --use-system-zlib \
                 --nostrip \
                 --endian=${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
                 --wiredtiger=${@['off','on'][d.getVar('SITEINFO_BITS') != '32']} \
                 ${PACKAGECONFIG_CONFARGS} \
                 core"

do_configure_prepend() {
        # tests use hex floats, not supported in plain C++
        sed -e 's|-std=c++11|-std=gnu++11|g' -i ${S}/SConstruct
}
scons_do_compile() {
        ${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} ${EXTRA_OESCONS} || \
        die "scons build execution failed."
}

scons_do_install() {
        ${STAGING_BINDIR_NATIVE}/scons install ${EXTRA_OESCONS}|| \
        die "scons install execution failed."
}

PNBLACKLIST[mongodb] = "Since bbclass scons convert to python3, build mongodb failed"
