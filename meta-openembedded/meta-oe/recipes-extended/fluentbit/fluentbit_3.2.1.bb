SUMMARY = "Fast Log Processor and Forwarder"
DESCRIPTION = "Fluent Bit allows to collect log events or metrics from \
different sources, process them and deliver them to different backends \
such as Fluentd, Elasticsearch, Splunk, DataDog, Kafka, New Relic, Azure \
services, AWS services, Google services, NATS, InfluxDB or any custom \
HTTP end-point."
HOMEPAGE = "http://fluentbit.io"
BUGTRACKER = "https://github.com/fluent/fluent-bit/issues"
SECTION = "net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
DEPENDS = "\
    bison-native \
    flex-native \
    openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"
DEPENDS:append:libc-musl = " fts"

SRCREV = "600b5a955b5ef7b9d025e0c128432260d0c6a5f1"
SRC_URI = "\
    git://github.com/fluent/fluent-bit.git;branch=master;protocol=https \
    file://0001-lib-Do-not-use-private-makefile-targets-in-CMakelist.patch \
    file://0002-flb_info.h.in-Do-not-hardcode-compilation-directorie.patch \
    file://0003-CMakeLists.txt-Revise-init-manager-deduction.patch \
    file://0004-wasm-avoid-cmake-try_run-when-cross-compiling.patch \
    file://0005-cprof_encode_text.c-fix-wrong-pointer-assignment.patch \
"
SRC_URI:append:libc-musl = "\
    file://0004-chunkio-Link-with-fts-library-with-musl.patch \
    file://0005-Use-posix-strerror_r-with-musl.patch \
"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "\
    aws \
    binary \
    config-yaml \
    custom-calyptia \
    http-server \
    inotify \
    ipo \
    metrics \
    parser \
    prefer-system-libs \
    proxy-go \
    record-accessor \
    regex \
    release \
    signv4 \
    sqldb \
    stream-processor \
    tls \
    utf8-encoder \
"
# See https://github.com/fluent/fluent-bit/issues/7248#issuecomment-1631280496
PACKAGECONFIG:remove:toolchain-clang = "ipo"

# Use system libs
PACKAGECONFIG[prefer-system-libs] = "-DFLB_PREFER_SYSTEM_LIBS=Yes,-DFLB_PREFER_SYSTEM_LIBS=No, nghttp2 c-ares"
DEPENDS += " ${@bb.utils.contains('PACKAGECONFIG', 'prefer-system-libs backtrace', 'libbacktrace', '', d)}"
DEPENDS += " ${@bb.utils.contains('PACKAGECONFIG', 'prefer-system-libs jemalloc', 'jemalloc', '', d)}"
DEPENDS += " ${@bb.utils.contains('PACKAGECONFIG', 'prefer-system-libs luajit', 'luajit', '', d)}"

PACKAGECONFIG[all] = "-DFLB_ALL=Yes,-DFLB_ALL=No"
PACKAGECONFIG[arrow] = "-DFLB_ARROW=Yes,-DFLB_ARROW=No"
PACKAGECONFIG[avro-encoder] = "-DFLB_AVRO_ENCODER=Yes,-DFLB_AVRO_ENCODER=No"
PACKAGECONFIG[aws-error-reporter] = "-DFLB_AWS_ERROR_REPORTER=Yes,-DFLB_AWS_ERROR_REPORTER=No"
PACKAGECONFIG[aws] = "-DFLB_AWS=Yes,-DFLB_AWS=No"
PACKAGECONFIG[backtrace] = "-DFLB_BACKTRACE=Yes,-DFLB_BACKTRACE=No"
PACKAGECONFIG[binary] = "-DFLB_BINARY=Yes,-DFLB_BINARY=No"
PACKAGECONFIG[chunk-trace] = "-DFLB_CHUNK_TRACE=Yes,-DFLB_CHUNK_TRACE=No"
PACKAGECONFIG[config-yaml] = "-DFLB_CONFIG_YAML=Yes,-DFLB_CONFIG_YAML=No,libyaml"
PACKAGECONFIG[coverage] = "-DFLB_COVERAGE=Yes,-DFLB_COVERAGE=No"
PACKAGECONFIG[custom-calyptia] = "-DFLB_CUSTOM_CALYPTIA=Yes,-DFLB_CUSTOM_CALYPTIA=No"
PACKAGECONFIG[debug] = "-DFLB_DEBUG=Yes,-DFLB_DEBUG=No"
PACKAGECONFIG[enforce-alignment] = "-DFLB_ENFORCE_ALIGNMENT=Yes,-DFLB_ENFORCE_ALIGNMENT=No"
PACKAGECONFIG[examples] = "-DFLB_EXAMPLES=Yes,-DFLB_EXAMPLES=No"
PACKAGECONFIG[http-client-debug] = "-DFLB_HTTP_CLIENT_DEBUG=Yes,-DFLB_HTTP_CLIENT_DEBUG=No"
PACKAGECONFIG[http-server] = "-DFLB_HTTP_SERVER=Yes,-DFLB_HTTP_SERVER=No"
PACKAGECONFIG[inotify] = "-DFLB_INOTIFY=Yes,-DFLB_INOTIFY=No"
PACKAGECONFIG[ipo] = "-DFLB_IPO=Yes,-DFLB_IPO=no"
PACKAGECONFIG[jemalloc] = "-DFLB_JEMALLOC=Yes,-DFLB_JEMALLOC=No,jemalloc"
PACKAGECONFIG[luajit] = "-DFLB_LUAJIT=Yes,-DFLB_LUAJIT=No"
PACKAGECONFIG[metrics] = "-DFLB_METRICS=Yes,-DFLB_METRICS=No"
PACKAGECONFIG[mtrace] = "-DFLB_MTRACE=Yes,-DFLB_MTRACE=No"
PACKAGECONFIG[parser] = "-DFLB_PARSER=Yes,-DFLB_PARSER=No"
PACKAGECONFIG[posix-tls] = "-DFLB_POSIX_TLS=Yes,-DFLB_POSIX_TLS=No"
PACKAGECONFIG[proxy-go] = "-DFLB_PROXY_GO=Yes,-DFLB_PROXY_GO=No"
PACKAGECONFIG[record-accessor] = "-DFLB_RECORD_ACCESSOR=Yes,-DFLB_RECORD_ACCESSOR=No"
PACKAGECONFIG[regex] = "-DFLB_REGEX=Yes,-DFLB_REGEX=No"
PACKAGECONFIG[release] = "-DFLB_RELEASE=Yes,-DFLB_RELEASE=No"
PACKAGECONFIG[run-ldconfig] = "-DFLB_RUN_LDCONFIG=Yes,-DFLB_RUN_LDCONFIG=No"
PACKAGECONFIG[shared-lib] = "-DFLB_SHARED_LIB=Yes,-DFLB_SHARED_LIB=No"
PACKAGECONFIG[signv4] = "-DFLB_SIGNV4=Yes,-DFLB_SIGNV4=No"
PACKAGECONFIG[small] = "-DFLB_SMALL=Yes,-DFLB_SMALL=No"
PACKAGECONFIG[sqldb] = "-DFLB_SQLDB=Yes,-DFLB_SQLDB=No"
PACKAGECONFIG[stream-processor] = "-DFLB_STREAM_PROCESSOR=Yes,-DFLB_STREAM_PROCESSOR=No"
PACKAGECONFIG[tests-runtime] = "-DFLB_TESTS_RUNTIME=Yes,-DFLB_TESTS_RUNTIME=No"
PACKAGECONFIG[tls] = "-DFLB_TLS=Yes,-DFLB_TLS=No"
PACKAGECONFIG[trace] = "-DFLB_TRACE=Yes,-DFLB_TRACE=No"
PACKAGECONFIG[utf8-encoder] = "-DFLB_UTF8_ENCODER=Yes,-DFLB_UTF8_ENCODER=No"
PACKAGECONFIG[valgrind] = "-DFLB_VALGRIND=Yes,-DFLB_VALGRIND=No"
PACKAGECONFIG[wamrc] = "-DFLB_WAMRC=Yes,-DFLB_WAMRC=No"
PACKAGECONFIG[wasm-stack-protect] = "-DFLB_WASM_STACK_PROTECT=Yes,-DFLB_WASM_STACK_PROTECT=No"
PACKAGECONFIG[wasm] = "-DFLB_WASM=Yes,-DFLB_WASM=No"
PACKAGECONFIG[windows-defaults] = "-DFLB_WINDOWS_DEFAULTS=Yes,-DFLB_WINDOWS_DEFAULTS=No"

# Option to disable all Fluent Bit plugins by default. See cmake/plugins_options.cmake which
# individual plugins then to enable (e.g. using EXTRA_OECMAKE:append = " -DFLB_FOOBAR=ON")
PACKAGECONFIG[minimal] = "-DFLB_MINIMAL=Yes,-DFLB_MINIMAL=No"

# Without zstd dependency, kafka plugin build fails at link attempt against native libzstd.so
PACKAGECONFIG[in-kafka] = "-DFLB_IN_KAFKA=ON,-DFLB_IN_KAFKA=OFF,librdkafka zstd curl"
PACKAGECONFIG[out-kafka] = "-DFLB_OUT_KAFKA=ON,-DFLB_OUT_KAFKA=OFF,librdkafka zstd curl"

SYSTEMD_SERVICE:${PN} = "fluent-bit.service"

inherit cmake systemd pkgconfig

FULL_OPTIMIZATION:remove = "${@'-O2' if bb.data.inherits_class('clang', d) else ''}"
TARGET_CC_ARCH += "${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH:remove = "-D_FORTIFY_SOURCE=2"
EXTRA_OECMAKE += "-DCMAKE_DEBUG_SRCDIR=${TARGET_DBGSRC_DIR}/"
EXTRA_OECMAKE:append:mips = " -DCMAKE_C_STANDARD_LIBRARIES=-latomic"
EXTRA_OECMAKE:append:powerpc = " -DCMAKE_C_STANDARD_LIBRARIES=-latomic"
EXTRA_OECMAKE:append:riscv32 = " -DCMAKE_C_STANDARD_LIBRARIES=-latomic"
EXTRA_OECMAKE:append:riscv64 = " -DCMAKE_C_STANDARD_LIBRARIES=-latomic"
EXTRA_OECMAKE:append:x86 = " -DCMAKE_C_STANDARD_LIBRARIES=-latomic"

do_configure:prepend() {
    sed -i \
        -e 's#@INIT_MANAGER_IS_SYSTEMD@#'${@'TRUE' if d.getVar('INIT_MANAGER') == 'systemd' else 'FALSE'}'#' \
        -e 's#@INIT_MANAGER_IS_UPSTART@#'${@'TRUE' if d.getVar('INIT_MANAGER') == 'upstart' else 'FALSE'}'#' \
        ${S}/src/CMakeLists.txt
}

# flex hardcodes the input file in #line directives leading to TMPDIR contamination of debug sources.
do_compile:append() {
    find ${B} -name '*.c' -or -name '*.h' | xargs sed -i -e 's|${TMPDIR}|${TARGET_DBGSRC_DIR}/|g'
}
