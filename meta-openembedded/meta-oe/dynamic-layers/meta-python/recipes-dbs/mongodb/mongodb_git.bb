SUMMARY = "mongodb"
LICENSE = "SSPL-1 & Apache-2.0 & Zlib"
LIC_FILES_CHKSUM = "file://LICENSE-Community.txt;md5=3a865f27f11f43ecbe542d9ea387dcf1 \
                    file://APACHE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl libpcap zlib boost curl python3 \
           python3-setuptools-native \
           python3-pyyaml-native python3-cheetah-native \
           python3-psutil-native python3-regex-native \
           "

inherit scons dos2unix siteinfo python3native systemd useradd

PV = "4.4.24"
#v4.4.24
SRCREV = "0b86b9b7b42ad9970c5f818c527dd86c0634243a"
SRC_URI = "git://github.com/mongodb/mongo.git;branch=v4.4;protocol=https \
           file://0001-Tell-scons-to-use-build-settings-from-environment-va.patch \
           file://0001-Use-long-long-instead-of-int64_t.patch \
           file://0001-Use-__GLIBC__-to-control-use-of-gnu_get_libc_version.patch \
           file://0002-Add-a-definition-for-the-macro-__ELF_NATIVE_CLASS.patch \
           file://arm64-support.patch \
           file://0001-IntelRDFPMathLib20U1-Check-for-__DEFINED_wchar_t.patch \
           file://0001-Support-deprecated-resolver-functions.patch \
           file://0003-Fix-unknown-prefix-env.patch \
           file://1296.patch \
           file://0001-Fix-compilation-with-fno-common.patch \
           file://0001-stacktrace-Define-ARCH_BITS-for-x86.patch \
           file://0001-include-needed-c-header.patch \
           file://disable_runtime_check.patch \
           file://ppc64_ARCH_BITS.patch \
           file://0001-add-explict-static_cast-size_t-to-maxMemoryUsageByte.patch \
           file://0001-server-Adjust-the-cache-alignment-assumptions.patch \
           file://0001-The-std-lib-unary-binary_function-base-classes-are-d.patch \
           file://0001-free_mon-Include-missing-cstdint.patch \
           file://0001-apply-msvc-workaround-for-clang-16.patch \
           file://0001-Fix-type-mismatch-on-32bit-arches.patch \
           file://0001-Fix-build-on-32bit.patch \
           "
SRC_URI:append:libc-musl ="\
           file://0001-Mark-one-of-strerror_r-implementation-glibc-specific.patch \
           file://0002-Fix-default-stack-size-to-256K.patch \
           file://0004-wiredtiger-Disable-strtouq-on-musl.patch \
           file://0001-wiredtiger-Avoid-using-off64_t.patch \
           "

SRC_URI:append:toolchain-clang = "\
           file://0001-asio-Dont-use-experimental-with-clang.patch \
           "

S = "${WORKDIR}/git"

CVE_STATUS[CVE-2014-8180] = "not-applicable-config: Not affecting our configuration so it can be safely ignored."
CVE_STATUS[CVE-2017-2665] = "not-applicable-config: Not affecting our configuration so it can be safely ignored."

COMPATIBLE_HOST ?= '(x86_64|i.86|powerpc64|arm|aarch64).*-linux'

PACKAGECONFIG ??= "tcmalloc system-pcre"
# gperftools compilation fails for arm below v7 because of missing support of
# dmb operation. So we use system-allocator instead of tcmalloc
PACKAGECONFIG:remove:armv6 = "tcmalloc"
PACKAGECONFIG:remove:libc-musl = "tcmalloc"
PACKAGECONFIG:remove:riscv64 = "tcmalloc"
PACKAGECONFIG:remove:riscv32 = "tcmalloc"

PACKAGECONFIG[tcmalloc] = "--use-system-tcmalloc,--allocator=system,gperftools,"
PACKAGECONFIG[shell] = ",--js-engine=none,,"
PACKAGECONFIG[system-pcre] = "--use-system-pcre,,libpcre,"

MONGO_ARCH ?= "${HOST_ARCH}"
MONGO_ARCH:powerpc64le = "ppc64le"
WIREDTIGER ?= "off"
WIREDTIGER:x86-64 = "on"
WIREDTIGER:aarch64 = "on"

# ld.gold: fatal error: build/59f4f0dd/mongo/mongod: Structure needs cleaning
LDFLAGS:append:x86:libc-musl = " -fuse-ld=bfd"
LDFLAGS:remove:toolchain-clang = "-fuse-ld=bfd"

EXTRA_OESCONS = "PREFIX=${prefix} \
                 DESTDIR=${D} \
                 MAXLINELENGTH='2097152' \
                 LIBPATH=${STAGING_LIBDIR} \
                 LINKFLAGS='${LDFLAGS}' \
                 CXXFLAGS='${CXXFLAGS}' \
                 TARGET_ARCH=${MONGO_ARCH} \
                 MONGO_VERSION=${PV} \
                 OBJCOPY=${OBJCOPY} \
                 --ssl \
                 --disable-warnings-as-errors \
                 --use-system-zlib \
                 --nostrip \
                 --endian=${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
                 --use-hardware-crc32=${@bb.utils.contains('TUNE_FEATURES', 'crc', 'on', 'off', d)} \
                 --wiredtiger='${WIREDTIGER}' \
                 --separate-debug \
                 ${PACKAGECONFIG_CONFARGS}"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --home-dir /var/run/${BPN} --shell /bin/false --user-group ${BPN}"

scons_do_compile() {
    ${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} ${EXTRA_OESCONS} install-core ||
        die "scons build execution failed."
}

scons_do_install() {
    # install binaries
    install -d ${D}${bindir}
    for i in mongod mongos mongo; do
        if [ -f ${B}/build/*/mongo/$i ]; then
            install -m 0755 ${B}/build/*/mongo/$i ${D}${bindir}
        else
            bbnote "$i does not exist"
        fi
    done

    # install config
    install -d ${D}${sysconfdir}
    install -m 0644 ${S}/debian/mongod.conf ${D}${sysconfdir}

    # install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/debian/mongod.service ${D}${systemd_system_unitdir}

    # install mongo data folder
    install -m 755 -d ${D}${localstatedir}/lib/${BPN}
    chown ${BPN}:${BPN} ${D}${localstatedir}/lib/${BPN}

    # Create /var/log/mongodb in runtime.
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -d ${D}${nonarch_libdir}/tmpfiles.d
        echo "d ${localstatedir}/log/${BPN} 0755 ${BPN} ${BPN} -" > ${D}${nonarch_libdir}/tmpfiles.d/${BPN}.conf
    fi
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d ${BPN} ${BPN} 0755 ${localstatedir}/log/${BPN} none" > ${D}${sysconfdir}/default/volatiles/99_${BPN}
    fi
}

CONFFILES:${PN} = "${sysconfdir}/mongod.conf"

SYSTEMD_SERVICE:${PN} = "mongod.service"

FILES:${PN} += "${nonarch_libdir}/tmpfiles.d"

RDEPENDS:${PN} += "tzdata-core"

SKIP_RECIPE[mongodb] ?= "Needs porting to python 3.12"
