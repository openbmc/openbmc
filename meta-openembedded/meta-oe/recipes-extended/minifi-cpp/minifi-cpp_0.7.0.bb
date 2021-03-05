SUMMARY = "A subproject of Apache NiFi to collect data where it originates."
DESCRIPTION = "MiNiFi--a subproject of Apache NiFi--is a complementary \
data collection approach that supplements the core tenets of NiFi in dataflow \
management, focusing on the collection of data at the source of its creation."
HOMEPAGE = "https://nifi.apache.org/minifi/index.html"
SECTION = "console/network"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9534eb5f4ab800b573a37bffc62f3a7"

DEPENDS = "virtual/crypt expat flex python3 bison-native libxml2 nettle lz4"
RDEPENDS_${PN} = "python3-core"

SRCREV = "aa42957a2e227df41510047cece3cd606dc1cb6a"
SRC_URI = "git://github.com/apache/nifi-minifi-cpp.git \
            https://curl.haxx.se/download/curl-7.64.0.tar.bz2;name=curl;subdir=git/thirdparty \
            https://ftp.openbsd.org/pub/OpenBSD/LibreSSL/libressl-2.8.3.tar.gz;name=libressl;subdir=git/thirdparty \
            ${DEBIAN_MIRROR}/main/o/ossp-uuid/ossp-uuid_1.6.2.orig.tar.gz;name=ossp-uuid;subdir=git/thirdparty \
            file://fix-minifi-compile.patch \
            file://fix-libressl-compile.patch \
            file://fix-libressl-avoid-BSWAP-assembly-for-ARM-v6.patch \
            file://fix-osspuuid-compile.patch \
            file://fix-osspuuid-cross-compile.patch \
            file://fix-osspuuid-musl-compile.patch \
            file://fix-rocksdb-cross-compile.patch \
            file://remove_const_due_to_std_lock_guard.patch \
            file://0001-Add-lxml2-to-linker-cmdline-of-xml-is-found.patch \
            file://0001-CMakeLists.txt-use-curl-local-source-tarball.patch \
            file://0002-cmake-LibreSSL.cmake-use-libressl-local-source-tarba.patch \
            file://0003-cmake-BundledOSSPUUID.cmake-use-ossp-uuid-local-sour.patch \
            file://0001-civetweb-CMakeLists.txt-do-not-search-gcc-ar-and-gcc.patch \
            file://0001-cxxopts-Add-limits-header.patch \
            file://minifi.service \
            file://systemd-volatile.conf \
            file://sysvinit-volatile.conf \
            "

SRC_URI[curl.md5sum] = "d0bcc586873cfef08b4b9594e5395a33"
SRC_URI[curl.sha256sum] = "d573ba1c2d1cf9d8533fadcce480d778417964e8d04ccddcc76e591d544cf2eb"
SRC_URI[libressl.md5sum] = "0f1127bd21b4aa8495a910379c2ad936"
SRC_URI[libressl.sha256sum] = "9b640b13047182761a99ce3e4f000be9687566e0828b4a72709e9e6a3ef98477"
SRC_URI[ossp-uuid.md5sum] = "5db0d43a9022a6ebbbc25337ae28942f"
SRC_URI[ossp-uuid.sha256sum] = "11a615225baa5f8bb686824423f50e4427acd3f70d394765bdff32801f0fd5b0"

S = "${WORKDIR}/git"

inherit pkgconfig cmake systemd

SYSTEMD_PACKAGES = "minifi-cpp"
SYSTEMD_SERVICE_${PN} = "minifi.service"
SYSTEMD_AUTO_ENABLE = "disable"

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

EXTRA_OECMAKE += " \
    -DHOST_SYS=${HOST_SYS} -DBUILD_SYS=${BUILD_SYS} \
    -DSKIP_TESTS=ON \
    -DGCC_AR=${STAGING_BINDIR_TOOLCHAIN}/${AR} \
    -DGCC_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${RANLIB} \
    "
EXTRA_OECMAKE_append_toolchain-clang = " -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib"
LDFLAGS_append_toolchain-clang = " -fuse-ld=lld"

# RV lld errors out:
# riscv64-yoe-linux-ld.lld: error: init.c:(.text+0x0): relocation R_RISCV_ALIGN requires unimplemented linker relaxation; recompile with -mno-relax
LDFLAGS_remove_riscv32 = "-fuse-ld=lld"
LDFLAGS_remove_riscv64 = "-fuse-ld=lld"

# There are endian issues when communicating with the x86 nifi on the the mips and the ppc machines.
COMPATIBLE_MACHINE_mips = "(!.*mips).*"
COMPATIBLE_MACHINE_mips64 = "(!.*mips64).*"
COMPATIBLE_MACHINE_powerpc = "(!.*ppc).*"

TARGET_CFLAGS_append_riscv32 = " -fpic"
TARGET_CXXFLAGS_append_riscv32 = " -fpic"
TARGET_CFLAGS_append_riscv64 = " -fpic"
TARGET_CXXFLAGS_append_riscv64 = " -fpic"

do_install[cleandirs] += "${WORKDIR}/minifi-install"
PSEUDO_CONSIDER_PATHS .= ",${WORKDIR}/minifi-install"

do_install() {
    DESTDIR='${WORKDIR}/minifi-install' cmake_runcmake_build --target ${OECMAKE_TARGET_INSTALL}
    MINIFI_BIN=${bindir}
    MINIFI_HOME=${sysconfdir}/minifi
    MINIFI_RUN=${localstatedir}/lib/minifi
    MINIFI_LOG=${localstatedir}/log/minifi

    install -d ${D}${MINIFI_BIN}
    install -d ${D}${MINIFI_HOME}/conf
    install -m 755 -d ${D}${localstatedir}/lib/minifi
    cp -a ${WORKDIR}/minifi-install/usr/bin/*   ${D}${MINIFI_BIN}/
    cp -a ${WORKDIR}/minifi-install/usr/conf/*  ${D}${MINIFI_HOME}/conf/

    sed -i 's|#appender.rolling.directory=.*|appender.rolling.directory='${MINIFI_LOG}'|g' \
        ${D}${MINIFI_HOME}/conf/minifi-log.properties
    sed -i 's|nifi.provenance.repository.directory.default=.*|nifi.provenance.repository.directory.default='${MINIFI_RUN}'/provenance_repository|g' \
        ${D}${MINIFI_HOME}/conf/minifi.properties
    sed -i 's|nifi.flowfile.repository.directory.default=.*|nifi.flowfile.repository.directory.default='${MINIFI_RUN}'/flowfile_repository|g' \
        ${D}${MINIFI_HOME}/conf/minifi.properties
    sed -i 's|nifi.database.content.repository.directory.default=.*|nifi.database.content.repository.directory.default='${MINIFI_RUN}'/content_repository|g' \
        ${D}${MINIFI_HOME}/conf/minifi.properties
    sed -i 's|nifi.flow.configuration.file=.*|nifi.flow.configuration.file='${MINIFI_HOME}'/conf/config.yml|g' \
        ${D}${MINIFI_HOME}/conf/minifi.properties

    sed -i 's|export MINIFI_HOME=.*|export MINIFI_HOME='${MINIFI_HOME}'|g' ${D}${MINIFI_BIN}/minifi.sh
    sed -i 's|bin_dir=${MINIFI_HOME}/bin|bin_dir='${MINIFI_BIN}'|g' ${D}${MINIFI_BIN}/minifi.sh
    sed -i 's|pid_file=${bin_dir}/.|pid_file='${localstatedir}/run/'|g' ${D}${MINIFI_BIN}/minifi.sh

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d/
        install -m 0644 ${WORKDIR}/systemd-volatile.conf ${D}${sysconfdir}/tmpfiles.d/minifi.conf
        install -m 0755 -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/minifi.service ${D}${systemd_unitdir}/system/

        sed -i 's|@LOCALSTATEDIR@|${localstatedir}|g' ${D}${systemd_unitdir}/system/minifi.service
        sed -i 's|@SYSCONFDIR@|${sysconfdir}|g' ${D}${systemd_unitdir}/system/minifi.service
        sed -i 's|@BINDIR@|${bindir}|g' ${D}${systemd_unitdir}/system/minifi.service

        sed -i 's|@MINIFI_LOG@|'${MINIFI_LOG}'|g' ${D}${sysconfdir}/tmpfiles.d/minifi.conf

    elif ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0644 ${WORKDIR}/sysvinit-volatile.conf ${D}${sysconfdir}/default/volatiles/99_minifi

        sed -i 's|@MINIFI_LOG@|'${MINIFI_LOG}'|g' ${D}${sysconfdir}/default/volatiles/99_minifi
    fi
}

pkg_postinst_${PN}() {
    if [ -z "$D" ]; then
        if type systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}
