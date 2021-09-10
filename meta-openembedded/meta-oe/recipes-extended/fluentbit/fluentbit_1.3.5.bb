SUMMARY = "Fast Log processor and Forwarder"
DESCRIPTION = "Fluent Bit is a data collector, processor and  \
forwarder for Linux. It supports several input sources and \
backends (destinations) for your data. \
"

HOMEPAGE = "http://fluentbit.io"
BUGTRACKER = "https://github.com/fluent/fluent-bit/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
SECTION = "net"

SRC_URI = "http://fluentbit.io/releases/1.3/fluent-bit-${PV}.tar.gz \
           file://jemalloc.patch \
           file://cross-build-init-system-detection.patch \
           file://builtin-nan.patch \
           file://0001-ppc-Fix-signature-for-co_create-API.patch \
           file://0001-bin-fix-SIGSEGV-caused-by-using-flb_free-instead-of-.patch \
           file://0002-parser-Fix-SIGSEGV-caused-by-using-flb_free-instead-.patch \
           "
SRC_URI[md5sum] = "6eae6dfd0a874e5dd270c36e9c68f747"
SRC_URI[sha256sum] = "e037c76c89269c8dc4027a08e442fefd2751b0f1e0f9c38f9a4b12d781a9c789"

S = "${WORKDIR}/fluent-bit-${PV}"
DEPENDS = "zlib bison-native flex-native"
DEPENDS += "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

DEPENDS:append:libc-musl = " fts "

INSANE_SKIP:${PN}-dev += "dev-elf"

LTO = ""

# Use CMake 'Unix Makefiles' generator
OECMAKE_GENERATOR ?= "Unix Makefiles"

# Fluent Bit build options
# ========================

# Host related setup
EXTRA_OECMAKE += "-DGNU_HOST=${HOST_SYS} -DFLB_ALL=ON -DFLB_TD=1"

# Disable LuaJIT and filter_lua support
EXTRA_OECMAKE += "-DFLB_LUAJIT=Off -DFLB_FILTER_LUA=Off "

# Disable Library and examples
EXTRA_OECMAKE += "-DFLB_SHARED_LIB=Off -DFLB_EXAMPLES=Off "

EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES','systemd','-DFLB_SYSTEMD=On','',d)}"

EXTRA_OECMAKE:append:riscv64 = " -DFLB_DEPS='atomic'"
EXTRA_OECMAKE:append:riscv32 = " -DFLB_DEPS='atomic'"

# Kafka Output plugin (disabled by default): note that when
# enabling Kafka output plugin, the backend library librdkafka
# requires 'openssl' as a dependency.
#
# DEPENDS += "openssl "
# EXTRA_OECMAKE += "-DFLB_OUT_KAFKA=On "

inherit cmake systemd

CFLAGS += "-fcommon"

SYSTEMD_SERVICE:${PN} = "td-agent-bit.service"
TARGET_CC_ARCH:append = " ${SELECTED_OPTIMIZATION}"
