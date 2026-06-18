SUMMARY = "A subproject of Apache NiFi to collect data where it originates."
DESCRIPTION = "MiNiFi--a subproject of Apache NiFi--is a complementary \
data collection approach that supplements the core tenets of NiFi in dataflow \
management, focusing on the collection of data at the source of its creation."
HOMEPAGE = "https://nifi.apache.org/minifi/index.html"
SECTION = "console/network"
LICENSE = "Apache-2.0 & MIT & BSD-2-Clause & BSD-3-Clause & BSL-1.0 & Zlib & ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb72a6f151096df9e2f5f01b6bf9d735"

SRC_URI = "git://github.com/apache/nifi-minifi-cpp.git;protocol=https;branch=main \
           git://github.com/martinmoene/expected-lite.git;protocol=https;branch=master;name=expected-lite;destsuffix=${S}/thirdparty/expected-lite-src \
           git://github.com/ericniebler/range-v3.git;protocol=https;branch=master;name=range-v3;destsuffix=${S}/thirdparty/range-v3-src \
           git://github.com/Neargye/magic_enum.git;protocol=https;branch=master;name=magic-enum;destsuffix=${S}/thirdparty/magic-enum-src \
           git://github.com/p-ranav/argparse.git;protocol=https;branch=master;name=argparse;destsuffix=${S}/thirdparty/argparse-src \
           git://github.com/gsl-lite/gsl-lite.git;protocol=https;branch=master;name=gsl-lite;destsuffix=${S}/thirdparty/gsl-lite-src \
           git://github.com/HowardHinnant/date.git;protocol=https;branch=master;name=date;destsuffix=${S}/thirdparty/date-src \
           git://github.com/chriskohlhoff/asio.git;protocol=https;branch=master;name=asio;destsuffix=${S}/thirdparty/asio-src \
           git://github.com/fmtlib/fmt.git;protocol=https;branch=master;name=fmt;destsuffix=${S}/thirdparty/fmt-src \
           git://github.com/gabime/spdlog.git;protocol=https;branch=v1.x;tag=v1.15.3;name=spdlog;destsuffix=${S}/thirdparty/spdlog-src \
           git://github.com/danielaparker/jsoncons.git;protocol=https;branch=master;name=jsoncons;destsuffix=${S}/thirdparty/jsoncons-src \
           ${DEBIAN_MIRROR}/main/o/ossp-uuid/ossp-uuid_1.6.2.orig.tar.gz;name=ossp-uuid;subdir=${S}/thirdparty \
           https://download.libsodium.org/libsodium/releases/libsodium-1.0.19.tar.gz;name=libsodium;subdir=${S}/thirdparty \
           file://0001-Do-not-use-bundled-packages.patch \
           file://0002-Fix-osspuuid-build.patch \
           file://0003-Fix-libsodium-build.patch \
           file://0004-Pass-noline-flag-to-flex.patch \
           file://0005-generateVersion.sh-set-correct-buildrev.patch \
           file://0006-CMakeLists.txt-do-not-use-ccache.patch \
           file://0007-libsodium-aarch64-set-compiler-attributes-after-including-arm_.patch \
           file://0001-Add-missing-include-for-malloc-free.patch;patchdir=thirdparty/fmt-src \
           file://0001-generateVersion.sh-set-BUILD_DATE-to-SOURCE_DATE_EPO.patch \
           file://0001-Fix-build-with-gcc-16.patch \
           file://systemd-volatile.conf \
           file://sysvinit-volatile.conf \
          "

# minifi-cpp: 0.99.2
SRCREV = "92fb88dca9aaff75b5c6795d25d6e437649c1c77"
# expected-lite: 0.9.0
SRCREV_expected-lite = "e45e8d5f295d54efe9cace331b9e9f5efa8a84c3"
# range-v3: 0.12.0
SRCREV_range-v3 = "a81477931a8aa2ad025c6bda0609f38e09e4d7ec"
# magic-enum: 0.9.6
SRCREV_magic-enum = "dd6a39d0ba1852cf06907e0f0573a2a10d23c2ad"
# argparse: 3.0
SRCREV_argparse = "af442b4da0cd7a07b56fa709bd16571889dc7fda"
# gsl-lite: 0.41.0
SRCREV_gsl-lite = "755ba124b54914e672737acace6a9314f59e8d6f"
# date: 3.0.3
SRCREV_date = "5bdb7e6f31fac909c090a46dbd9fea27b6e609a4"
# asio: 1.34.2
SRCREV_asio = "ed6aa8a13d51dfc6c00ae453fc9fb7df5d6ea963"
# fmt: 11.2.0
SRCREV_fmt = "40626af88bd7df9a5fb80be7b25ac85b122d6c21"
# spdlog: 1.15.3
SRCREV_spdlog = "6fa36017cfd5731d617e1a934f0e5ea9c4445b13"
# jsoncons: 1.3.2
SRCREV_jsoncons = "64b9da1e9f15eeff4ec9d6bc856538db542118f2"

SRCREV_FORMAT .= "_expected-lite_range-v3_magic-enum_argparse_gsl-lite_date_asio_fmt_spdlog_jsoncons"

# ossp-uuid: 1.6.2
SRC_URI[ossp-uuid.sha256sum] = "11a615225baa5f8bb686824423f50e4427acd3f70d394765bdff32801f0fd5b0"
# libsodium: 1.0.19
SRC_URI[libsodium.sha256sum] = "018d79fe0a045cca07331d37bd0cb57b2e838c51bc48fd837a1472e50068bbea"


inherit pkgconfig cmake systemd

DEPENDS = "virtual/crypt bison-native flex-native flex openssl curl zlib xz bzip2 yaml-cpp zstd lz4 pugixml"

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

CXXFLAGS:append:toolchain-clang = " -Wno-error=c++11-narrowing-const-reference"
LDFLAGS:append:riscv32 = " -latomic"

EXTRA_OECMAKE = " \
                 -DCMAKE_BUILD_TYPE=Release \
                 -DHOST_SYS=${HOST_SYS} -DBUILD_SYS=${BUILD_SYS} \
                 -DFLEX_TARGET_ARG_COMPILE_FLAGS='--noline' \
                 -DBISON_TARGET_ARG_COMPILE_FLAGS='--no-lines --file-prefix-map=${S}=${TARGET_DBGSRC_DIR} --file-prefix-map=${B}=${TARGET_DBGSRC_DIR}' \
                 -DENABLE_ENCRYPT_CONFIG=ON \
                 -DENABLE_LUA_SCRIPTING=OFF \
                 -DENABLE_PYTHON_SCRIPTING=OFF \
                 -DENABLE_AWS=OFF \
                 -DENABLE_AZURE=OFF \
                 -DENABLE_GCP=OFF \
                 -DENABLE_KUBERNETES=OFF \
                 -DENABLE_MQTT=OFF \
                 -DENABLE_GRAFANA_LOKI=OFF \
                 -DENABLE_GRPC_FOR_LOKI=OFF \
                 -DENABLE_ELASTICSEARCH=OFF \
                 -DENABLE_SQL=OFF \
                 -DENABLE_PROMETHEUS=OFF \
                 -DENABLE_PROCFS=OFF \
                 -DENABLE_SPLUNK=OFF \
                 -DENABLE_OPC=OFF \
                 -DENABLE_KAFKA=OFF \
                 -DENABLE_BZIP2=ON \
                 -DENABLE_LZMA=ON \
                 -DENABLE_COUCHBASE=OFF \
                 -DENABLE_LLAMACPP=OFF \
                 -DSKIP_TESTS=ON \
                 -DMINIFI_OPENSSL_SOURCE=SYSTEM \
                 -DMINIFI_LIBCURL_SOURCE=SYSTEM \
                 -DMINIFI_ZSTD_SOURCE=SYSTEM \
                 -DMINIFI_BZIP2_SOURCE=SYSTEM \
                 -DMINIFI_LIBXML2_SOURCE=SYSTEM \
                 -DMINIFI_CATCH2_SOURCE=SYSTEM \
                 -DMINIFI_ZLIB_SOURCE=SYSTEM \
                 -DMINIFI_PUGIXML_SOURCE=SYSTEM \
                 -DMINIFI_FMT_SOURCE=BUILD \
                 -DMINIFI_SPDLOG_SOURCE=BUILD \
                 -DFETCHCONTENT_SOURCE_DIR_GSL-LITE=${S}/thirdparty/gsl-lite-src \
                 -DFETCHCONTENT_SOURCE_DIR_DATE_SRC=${S}/thirdparty/date-src \
                 -DFETCHCONTENT_SOURCE_DIR_EXPECTED-LITE=${S}/thirdparty/expected-lite-src \
                 -DFETCHCONTENT_SOURCE_DIR_RANGE-V3_SRC=${S}/thirdparty/range-v3-src \
                 -DFETCHCONTENT_SOURCE_DIR_MAGIC_ENUM=${S}/thirdparty/magic-enum-src \
                 -DFETCHCONTENT_SOURCE_DIR_ASIO=${S}/thirdparty/asio-src \
                 -DFETCHCONTENT_SOURCE_DIR_ARGPARSE=${S}/thirdparty/argparse-src \
                 -DFETCHCONTENT_SOURCE_DIR_FMT=${S}/thirdparty/fmt-src \
                 -DFETCHCONTENT_SOURCE_DIR_SPDLOG=${S}/thirdparty/spdlog-src \
                 -DFETCHCONTENT_SOURCE_DIR_JSONCONS=${S}/thirdparty/jsoncons-src \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '-DENABLE_SYSTEMD=ON', '-DENABLE_SYSTEMD=OFF', d)} \
                 -DBUILD_IDENTIFIER=${PV} \
                "

PACKAGECONFIG ??= "libarchive expression-language"

# rocksdb is not compatible with libc-musl:powerpc & armv5
PACKAGECONFIG:remove:libc-musl:powerpc = "rocksdb"
PACKAGECONFIG:remove:armv5 = "rocksdb"

PACKAGECONFIG[libarchive] = "-DENABLE_LIBARCHIVE=ON,-DENABLE_LIBARCHIVE=OFF,libarchive"
PACKAGECONFIG[expression-language] = "-DENABLE_EXPRESSION_LANGUAGE=ON, -DENABLE_EXPRESSION_LANGUAGE=OFF"
PACKAGECONFIG[civetweb] = "-DENABLE_CIVET=ON -DMINIFI_CIVETWEB_SOURCE=SYSTEM,-DENABLE_CIVET=OFF,civetweb"
PACKAGECONFIG[rocksdb] = "-DENABLE_ROCKSDB=ON -DBUILD_ROCKSDB=OFF -DMINIFI_ROCKSDB_SOURCE=SYSTEM,-DENABLE_ROCKSDB=OFF,rocksdb"

SYSTEMD_PACKAGES = "minifi-cpp"
SYSTEMD_SERVICE:${PN} = "minifi.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install[cleandirs] += "${WORKDIR}/minifi-install"
PSEUDO_CONSIDER_PATHS .= ",${WORKDIR}/minifi-install"

do_compile:prepend() {
    # Remove build host references
    sed -i -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
        -e 's|${DEBUG_PREFIX_MAP}||g' \
        -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
        ${B}/libminifi/agent_version.cpp
}

do_install() {
    DESTDIR='${WORKDIR}/minifi-install' cmake_runcmake_build --target ${OECMAKE_TARGET_INSTALL}
    MINIFI_BIN=${bindir}
    MINIFI_HOME=${sysconfdir}/nifi-minifi-cpp
    MINIFI_RUN=${localstatedir}/lib/nifi-minifi-cpp
    MINIFI_LOG=${localstatedir}/log/nifi-minifi-cpp

    install -m 755 -d ${D}${MINIFI_BIN}
    install -m 755 -d ${D}${MINIFI_HOME}
    install -m 755 -d ${D}${MINIFI_RUN}

    for i in minifi-encrypt-config minifi minifi.sh minifi-controller; do
        install -m 755 ${WORKDIR}/minifi-install/usr/bin/${i} ${D}${MINIFI_BIN}
    done
    for i in config.yml minifi-log.properties minifi.properties minifi-uid.properties; do
        install -m 644 ${WORKDIR}/minifi-install/usr/conf/${i} ${D}${MINIFI_HOME}
    done

    install -m 755 -d ${D}${libdir}/minifi-extensions
    install -m 755 ${WORKDIR}/minifi-install/usr/bin/libcore-minifi.so ${D}${libdir}
    install -m 755 ${WORKDIR}/minifi-install/usr/extensions/*.so ${D}${libdir}/minifi-extensions

    install -m 755 -d ${D}${libexecdir}/minifi-python
    install -m 755 -d ${D}${libexecdir}/minifi-python-examples
    cp -rf ${WORKDIR}/minifi-install/usr/minifi-python/* ${D}${libexecdir}/minifi-python/
    cp -rf ${WORKDIR}/minifi-install/usr/minifi-python-examples/* ${D}${libexecdir}/minifi-python-examples/

    sed -i "s|MINIFI_HOME=.*|MINIFI_HOME=${MINIFI_HOME}|g" ${D}${MINIFI_BIN}/minifi.sh
    sed -i "s|bin_dir=.*|bin_dir=${MINIFI_BIN}|g" ${D}${MINIFI_BIN}/minifi.sh

    sed -i "s|#appender.rolling.directory=.*|appender.rolling.directory=${MINIFI_LOG}|g" \
        ${D}${MINIFI_HOME}/minifi-log.properties
    sed -i "s|nifi.provenance.repository.directory.default=.*|nifi.provenance.repository.directory.default=${MINIFI_RUN}/provenance_repository|g" \
        ${D}${MINIFI_HOME}/minifi.properties
    sed -i "s|nifi.flowfile.repository.directory.default=.*|nifi.flowfile.repository.directory.default=${MINIFI_RUN}/flowfile_repository|g" \
        ${D}${MINIFI_HOME}/minifi.properties
    sed -i "s|nifi.database.content.repository.directory.default=.*|nifi.database.content.repository.directory.default=${MINIFI_RUN}/content_repository|g" \
        ${D}${MINIFI_HOME}/minifi.properties
    sed -i "s|nifi.flow.configuration.file=.*|nifi.flow.configuration.file=${MINIFI_HOME}/config.yml|g" \
        ${D}${MINIFI_HOME}/minifi.properties
    sed -i "s|nifi.python.processor.dir=.*|nifi.python.processor.dir=${libexecdir}/minifi-python|g" \
        ${D}${MINIFI_HOME}/minifi.properties
    sed -i "s|nifi.extension.path=.*|nifi.extension.path=${libdir}/minifi-extensions/*|g" \
        ${D}${MINIFI_HOME}/minifi.properties

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -m 755 -d ${D}${sysconfdir}/tmpfiles.d
        install -m 644 ${UNPACKDIR}/systemd-volatile.conf ${D}${sysconfdir}/tmpfiles.d/minifi.conf
        sed -i "s|@MINIFI_LOG@|${MINIFI_LOG}|g" ${D}${sysconfdir}/tmpfiles.d/minifi.conf

        install -m 755 -d ${D}${systemd_system_unitdir}
        install -m 644 ${WORKDIR}/minifi-install/usr/bin/minifi.service ${D}${systemd_system_unitdir}

        sed -i -e "s|^Environment=.*|Environment=MINIFI_HOME=${MINIFI_HOME}|g" ${D}${systemd_system_unitdir}/minifi.service
        sed -i -e "s|^ExecStart=.*|ExecStart=${MINIFI_BIN}/minifi|g" ${D}${systemd_system_unitdir}/minifi.service
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0644 ${UNPACKDIR}/sysvinit-volatile.conf ${D}${sysconfdir}/default/volatiles/99_minifi

        sed -i "s|@MINIFI_LOG@|${MINIFI_LOG}|g" ${D}${sysconfdir}/default/volatiles/99_minifi
    fi
    
    for ss in $(find ${D}${libexecdir}/minifi-python-examples -type f); do
        sed -i 's,/usr/bin/env python$,/usr/bin/env python3,' "$ss"
    done

}

pkg_postinst:${PN}() {
    if [ -z "$D" ]; then
        if type systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

FILES:${PN}-dev = ""
FILES:${PN} += "${libdir}/libcore-minifi.so \
                ${libdir}/minifi-extensions \
                ${libexecdir}/minifi-python \
                ${libexecdir}/minifi-python-examples \
               "

INSANE_SKIP:${PN} += "dev-deps"

CLEANBROKEN = "1"
