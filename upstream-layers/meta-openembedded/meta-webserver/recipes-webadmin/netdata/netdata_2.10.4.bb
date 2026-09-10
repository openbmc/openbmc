SUMMARY = "Real-time performance monitoring"
DESCRIPTION = "Netdata is high-fidelity infrastructure monitoring and troubleshooting. \
               Open-source, free, preconfigured, opinionated, and always real-time."
HOMEPAGE = "https://github.com/netdata/netdata/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc9b848046ef54b5eaee6071947abd24"

DEPENDS += "\
    bison-native \
    curl \
    flex-native \
    json-c \
    libuv \
    libyaml \
    lz4 \
    openssl \
    protobuf \
    protobuf-native \
    util-linux \
    zlib \
    "

SRC_URI = "\
    https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BPN}-v${PV}.tar.gz \
    file://0002-Do-not-hardcode-systemd-unit-directories.patch \
    file://0003-Do-not-record-the-configure-command-line-in-the-binar.patch \
    file://0004-libsensors-do-not-emit-line-directives-in-generated-s.patch \
    file://netdata.conf \
    file://netdata-volatiles.conf \
    ${@bb.utils.contains('PACKAGECONFIG', 'go', 'file://go.d.conf', '', d)} \
    "
SRC_URI[sha256sum] = "d1ae949863925d626b1d42a53000bcca2c239718e0b0ddc6a3f3f5029b21cdfe"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/tags"
UPSTREAM_CHECK_REGEX = "${BPN}/releases/tag/v(?P<pver>(?!1\.99)\d+(?:\.\d+)*)"

S = "${UNPACKDIR}/${BPN}-v${PV}"

# Stop sending anonymous statistics to Google Analytics
NETDATA_ANONYMOUS ??= "enabled"

inherit pkgconfig useradd systemd
# Inherit cmake last to use its do_compile task (and not go's)
inherit_defer ${@bb.utils.filter("PACKAGECONFIG", "go", d)} cmake

TARGET_CC_ARCH:append:libc-musl = " -D_LARGEFILE64_SOURCE"

LIBS:toolchain-clang:x86 = "-latomic"
LIBS:riscv64 = "-latomic"
LIBS:riscv32 = "-latomic"
LIBS:mips = "-latomic"
export LIBS

# Skip go.d plugins QA issues
CFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'go', \
    '-Wno-aggressive-loop-optimizations -Wno-nonnull -Wno-stringop-overflow' \
    , '', d)}"
INSANE_SKIP:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'go', 'already-stripped buildpaths', '', d)}"

# network is required by go to get dependent packages
do_compile[network] = "${@bb.utils.contains('PACKAGECONFIG', 'go', '1', '0', d)}"

#systemd
SYSTEMD_PACKAGES = "${PN}"
export SERVICE_FILES = "netdata.service netdata-updater.service netdata-updater.timer"
SYSTEMD_SERVICE:${PN} = "${SERVICE_FILES}"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

#User specific
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --home-dir ${localstatedir}/run/netdata \
    ${@bb.utils.contains('PACKAGECONFIG','docker','--groups docker','',d)} --user-group netdata"

PACKAGECONFIG ??= "freeipmi ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[brotli] = ",,brotli"
PACKAGECONFIG[freeipmi] = "-DENABLE_PLUGIN_FREEIPMI=ON,-DENABLE_PLUGIN_FREEIPMI=OFF,freeipmi"
PACKAGECONFIG[nfacct] = "-DENABLE_PLUGIN_NFACCT=ON,-DENABLE_PLUGIN_NFACCT=OFF,libmnl"
# needs meta-virtualization
PACKAGECONFIG[xenstat] = "-DENABLE_PLUGIN_XENSTAT=ON,-DENABLE_PLUGIN_XENSTAT=OFF,xen-tools"
PACKAGECONFIG[cups] = "-DENABLE_PLUGIN_CUPS=ON,-DENABLE_PLUGIN_CUPS=OFF,cups"
PACKAGECONFIG[systemd] = "-DENABLE_PLUGIN_SYSTEMD_JOURNAL=ON,-DENABLE_PLUGIN_SYSTEMD_JOURNAL=OFF,systemd"
PACKAGECONFIG[docker] = ",,virtual/docker, virtual/docker"
PACKAGECONFIG[go] = "-DENABLE_PLUGIN_GO=ON, -DENABLE_PLUGIN_GO=OFF"
# The local agent dashboard is not shipped in the release tarball; enabling it
# makes CMake download https://app.netdata.cloud/agent.tar.gz at configure
# time, which cannot work in an offline build. Off by default.
PACKAGECONFIG[dashboard] = "-DENABLE_DASHBOARD=ON,-DENABLE_DASHBOARD=OFF"

# ebpf doesn't compile (or detect) the cross compilation well.
# Several 2.x features fetch sources at configure time, which cannot work in
# an offline build, so they are disabled here:
#  - ENABLE_PLUGIN_OTEL / ENABLE_PLUGIN_OTEL_SIGNAL_VIEWER pull Rust/Corrosion
#    via CMake FetchContent.
#  - ENABLE_PLUGIN_SCRIPTS requires Go (as does ENABLE_PLUGIN_GO).
#  - ENABLE_ML FetchContent-downloads dlib.
#  - ENABLE_LIBBACKTRACE ExternalProject-downloads libbacktrace.
# Protobuf is detected unconditionally and needs the native protoc.
EXTRA_OECMAKE += "-DENABLE_PLUGIN_EBPF=OFF \
                  -DENABLE_PLUGIN_OTEL=OFF -DENABLE_PLUGIN_OTEL_SIGNAL_VIEWER=OFF \
                  -DENABLE_PLUGIN_SCRIPTS=OFF \
                  -DENABLE_ML=OFF \
                  -DENABLE_LIBBACKTRACE=OFF \
                  -DProtobuf_PROTOC_EXECUTABLE=${STAGING_BINDIR_NATIVE}/protoc \
                  -DBUILD_FOR_PACKAGING=${@bb.utils.contains('DISTRO_FEATURES','systemd','ON','OFF',d)} \
                  -DENABLE_EXPORTER_PROMETHEUS_REMOTE_WRITE=OFF -DCMAKE_INSTALL_PREFIX='${base_prefix}'"

do_compile:append() {
    # Go dependencies are protected with read-only permissions, but would prevent cleaning
    if ${@bb.utils.contains('PACKAGECONFIG', 'go', 'true', 'false', d)}; then
        chmod -R a+w ${B}/pkg
    fi
}

do_install:append() {
    #set S UID for plugins
    chown root:netdata ${D}${libexecdir}/netdata/plugins.d/apps.plugin
    chmod 4750 ${D}${libexecdir}/netdata/plugins.d/apps.plugin
    rm -rf ${D}/${localstatedir}/

    if ${@bb.utils.contains('PACKAGECONFIG', 'xenstat', 'true', 'false', d)}; then
        # Set S UID for xenstat plugin
        chown root:netdata ${D}${libexecdir}/netdata/plugins.d/xenstat.plugin
        chmod 4750 ${D}${libexecdir}/netdata/plugins.d/xenstat.plugin
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        # Install systemd unit files
        install -Dm 0644 ${UNPACKDIR}/netdata-volatiles.conf ${D}${sysconfdir}/tmpfiles.d/netdata.conf
    fi

    # Install default netdata.conf
    install -d ${D}${sysconfdir}/netdata
    install -m 0644 ${UNPACKDIR}/netdata.conf ${D}${sysconfdir}/netdata/
    if ${@bb.utils.contains('PACKAGECONFIG', 'go', 'true', 'false', d)}; then
        install -m 0644 ${UNPACKDIR}/go.d.conf ${D}${sysconfdir}/netdata/
    fi
    sed -i -e 's,@@datadir,${datadir},g' ${D}${sysconfdir}/netdata/netdata.conf
    sed -i -e 's,@@libdir,${libdir},g' ${D}${sysconfdir}/netdata/netdata.conf
    sed -i -e 's,@@libexecdir,${libexecdir},g' ${D}${sysconfdir}/netdata/netdata.conf
    sed -i -e 's,@@localstatedir,${localstatedir},g' ${D}${sysconfdir}/netdata/netdata.conf
    sed -i -e 's,@@sysconfdir,${sysconfdir},g' ${D}${sysconfdir}/netdata/netdata.conf

    if [ "${NETDATA_ANONYMOUS}" = "enabled" ]; then
        touch ${D}${sysconfdir}/netdata/.opt-out-from-anonymous-statistics
    fi

    install --group netdata --owner netdata --directory ${D}${localstatedir}/cache/netdata
    install --group netdata --owner netdata --directory ${D}${localstatedir}/lib/netdata

    # BUILD_FOR_PACKAGING installs its own sysusers.d/tmpfiles.d snippets, but
    # user creation is handled by useradd.bbclass and tmpfiles by our own
    # netdata-volatiles.conf above, so drop the upstream ones to avoid
    # shipping duplicate/unmanaged files.
    rm -rf ${D}${nonarch_libdir}/sysusers.d ${D}${nonarch_libdir}/tmpfiles.d

    # 2.x ships a gzipped copy of CMakeCache.txt purely so that
    # "netdata -W buildinfo" can report it; it is full of host build paths.
    # It carries no runtime value in a cross build, so drop it.
    rm -f ${D}${datadir}/netdata/build-info-cmake-cache.gz
}

FILES:${PN} += "\
    ${localstatedir}/cache/netdata/ \
    ${localstatedir}/lib/netdata/ \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/journald@netdata.conf.d', '', d)} \
    "

RDEPENDS:${PN} = "bash python3-core zlib"
