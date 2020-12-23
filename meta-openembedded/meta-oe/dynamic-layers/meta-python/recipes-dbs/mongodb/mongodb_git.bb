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

PV = "4.4.1"
#v4.4.1
SRCREV = "ad91a93a5a31e175f5cbf8c69561e788bbc55ce1"
SRC_URI = "git://github.com/mongodb/mongo.git;branch=v4.4 \
           file://0001-kms-message-bump-libmongocrypto-to-v1.0.4.patch \
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

EXTRA_OESCONS = "PREFIX=${prefix} \
                 DESTDIR=${D} \
                 LIBPATH=${STAGING_LIBDIR} \
                 LINKFLAGS='${LDFLAGS}' \
                 CXXFLAGS='${CXXFLAGS}' \
                 TARGET_ARCH=${TARGET_ARCH} \
                 MONGO_VERSION=${PV} \
                 OBJCOPY=${OBJCOPY} \
                 --ssl \
                 --disable-warnings-as-errors \
                 --use-system-zlib \
                 --nostrip \
                 --endian=${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
                 --wiredtiger=${@['off','on'][d.getVar('SITEINFO_BITS') != '32']} \
                 --separate-debug \
                 ${PACKAGECONFIG_CONFARGS}"


USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home-dir /var/run/${BPN} --shell /bin/false --user-group ${BPN}"


scons_do_compile() {
        ${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} ${EXTRA_OESCONS} install-core || \
        die "scons build execution failed."
}

scons_do_install() {
        # install binaries
        install -d ${D}${bindir}
        for i in mongod mongos mongo
        do
            if [ -f ${B}/build/opt/mongo/${i} ]
            then
                install -m 0755 ${B}/build/opt/mongo/${i} ${D}${bindir}/${i}
            else
                bbnote "${i} does not exist"
            fi
        done

        # install config
        install -d ${D}${sysconfdir}
        install -m 0644 ${S}/debian/mongod.conf ${D}${sysconfdir}/

        # install systemd service
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/debian/mongod.service ${D}${systemd_system_unitdir}

        # install mongo data folder
        install -m 755 -d ${D}${localstatedir}/lib/${BPN}
        chown ${PN}:${PN} ${D}${localstatedir}/lib/${BPN}

        # Log files
        install -m 755 -d ${D}${localstatedir}/log/${BPN}
        chown ${PN}:${PN} ${D}${localstatedir}/log/${BPN}
}

CONFFILES_${PN} = "${sysconfdir}/mongod.conf"

SYSTEMD_SERVICE_${PN} = "mongod.service"


