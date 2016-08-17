SUMMARY = "mongodb"
LICENSE = "AGPL-3.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://GNU-AGPL-3.0.txt;md5=73f1eb20517c55bf9493b7dd6e480788 \
                    file://APACHE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl libpcre libpcap zlib"

inherit scons

PV = "3.3.0+git${SRCPV}"
SRCREV = "aacd231be0626a204cb40908afdf62c4b67bb0ad"
SRC_URI = "git://github.com/mongodb/mongo.git;branch=master \
           file://0001-Tell-scons-to-use-build-settings-from-environment-va.patch \
           "

S = "${WORKDIR}/git"

# Wiredtiger supports only 64-bit platforms
PACKAGECONFIG_x86-64 ??= "tcmalloc wiredtiger"
PACKAGECONFIG_aarch64 ??= "tcmalloc wiredtiger"
PACKAGECONFIG ??= "tcmalloc"
# gperftools compilation fails for arm below v7 because of missing support of
# dmb operation. So we use system-allocator instead of tcmalloc
PACKAGECONFIG_remove_armv6 = "tcmalloc"

#std::current_exception is undefined for arm < v6
COMPATIBLE_MACHINE_armv4 = "(!.*armv4).*"
COMPATIBLE_MACHINE_armv5 = "(!.*armv5).*"
COMPATIBLE_MACHINE_mips64 = "(!.*mips64).*"

PACKAGECONFIG[tcmalloc] = "--use-system-tcmalloc,--allocator=system,gperftools,"
PACKAGECONFIG[wiredtiger] = "--wiredtiger=on,--wiredtiger=off,,"

EXTRA_OESCONS = "--prefix=${D}${prefix} \
                 LIBPATH=${STAGING_LIBDIR} \
                 LINKFLAGS='${LDFLAGS}' \
                 CXXFLAGS='${CXXFLAGS}' \
                 TARGET_ARCH=${TARGET_ARCH} \
                 --ssl \
                 --disable-warnings-as-errors \
                 --use-system-pcre \
                 --use-system-zlib \
                 --js-engine=none \
                 --nostrip \
                 ${EXTRA_OECONF} \
                 mongod mongos"
DISABLE_STATIC = ""

scons_do_compile() {
        ${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} ${EXTRA_OESCONS} || \
        die "scons build execution failed."
}

scons_do_install() {
        ${STAGING_BINDIR_NATIVE}/scons install ${EXTRA_OESCONS}|| \
        die "scons install execution failed."
}
