SUMMARY = "A subproject of Apache NiFi to collect data where it originates."
DESCRIPTION = "MiNiFi--a subproject of Apache NiFi--is a complementary \
data collection approach that supplements the core tenets of NiFi in dataflow \
management, focusing on the collection of data at the source of its creation."
HOMEPAGE = "https://nifi.apache.org/minifi/index.html"
SECTION = "console/network"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9534eb5f4ab800b573a37bffc62f3a7"

DEPENDS = "virtual/crypt expat flex python3 bison-native libxml2"
RDEPENDS_${PN} = "python3-core"

SRCREV = "aa42957a2e227df41510047cece3cd606dc1cb6a"
SRC_URI = "git://github.com/apache/nifi-minifi-cpp.git \
            file://fix-minifi-compile.patch \
            file://fix-libressl-compile.patch \
            file://fix-libressl-avoid-BSWAP-assembly-for-ARM-v6.patch \
            file://fix-osspuuid-compile.patch \
            file://fix-osspuuid-cross-compile.patch \
            file://fix-osspuuid-musl-compile.patch \
            file://fix-rocksdb-cross-compile.patch \
            file://remove_const_due_to_std_lock_guard.patch \
            file://0001-Add-lxml2-to-linker-cmdline-of-xml-is-found.patch \
            file://minifi.service \
            file://systemd-volatile.conf \
            file://sysvinit-volatile.conf \
            "
S = "${WORKDIR}/git"

inherit pkgconfig cmake systemd

SYSTEMD_PACKAGES = "minifi-cpp"
SYSTEMD_SERVICE_${PN} = "minifi.service"
SYSTEMD_AUTO_ENABLE = "disable"

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

EXTRA_OECMAKE += " \
    -DHOST_SYS=${HOST_SYS} -DBUILD_SYS=${BUILD_SYS} \
    -DSKIP_TESTS=ON \
    "
EXTRA_OECMAKE_append_toolchain-clang = " -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib"
LDFLAGS_append_toolchain-clang = " -fuse-ld=lld"

# There are endian issues when communicating with the x86 nifi on the the mips and the ppc machines.
COMPATIBLE_MACHINE_mips = "(!.*mips).*"
COMPATIBLE_MACHINE_mips64 = "(!.*mips64).*"
COMPATIBLE_MACHINE_powerpc = "(!.*ppc).*"

TARGET_CFLAGS_append_riscv32 += "-fpic"
TARGET_CXXFLAGS_append_riscv32 += "-fpic"
TARGET_CFLAGS_append_riscv64 += "-fpic"
TARGET_CXXFLAGS_append_riscv64 += "-fpic"


do_install() {
    DESTDIR='${B}/minifi-install' cmake_runcmake_build --target ${OECMAKE_TARGET_INSTALL}

    MINIFI_BIN=${base_prefix}${bindir}
    MINIFI_HOME=${base_prefix}${sysconfdir}/minifi
    MINIFI_RUN=${base_prefix}${localstatedir}/run/minifi
    MINIFI_LOG=${base_prefix}${localstatedir}/log/minifi

    install -d ${D}${MINIFI_BIN}
    install -d ${D}${MINIFI_HOME}/conf
    cp -a ${B}/minifi-install/usr/bin/*   ${D}${MINIFI_BIN}/
    cp -a ${B}/minifi-install/usr/conf/*  ${D}${MINIFI_HOME}/conf/

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
    sed -i 's|pid_file=${bin_dir}|pid_file='${MINIFI_RUN}'|g' ${D}${MINIFI_BIN}/minifi.sh

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d/
        install -m 0644 ${WORKDIR}/systemd-volatile.conf ${D}${sysconfdir}/tmpfiles.d/
        install -m 0755 -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/minifi.service ${D}${systemd_unitdir}/system/

        sed -i 's|@LOCALSTATEDIR@|${localstatedir}|g' ${D}${systemd_unitdir}/system/minifi.service
        sed -i 's|@SYSCONFDIR@|${sysconfdir}|g' ${D}${systemd_unitdir}/system/minifi.service
        sed -i 's|@BINDIR@|${bindir}|g' ${D}${systemd_unitdir}/system/minifi.service

        sed -i 's|@MINIFI_RUN@|'${MINIFI_RUN}'|g' ${D}${sysconfdir}/tmpfiles.d/systemd-volatile.conf
        sed -i 's|@MINIFI_LOG@|'${MINIFI_LOG}'|g' ${D}${sysconfdir}/tmpfiles.d/systemd-volatile.conf

    elif ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0644 ${WORKDIR}/sysvinit-volatile.conf ${D}${sysconfdir}/default/volatiles/99_minifi

        sed -i 's|@MINIFI_RUN@|'${MINIFI_RUN}'|g' ${D}${sysconfdir}/default/volatiles/99_minifi
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

FILES_${PN} = " \
        ${bindir} \
        ${sysconfdir} \
        ${systemd_unitdir} \
        "
