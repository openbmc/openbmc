SUMMARY = "Open Telemetry Library"
DESCRIPTION = "An Observability framework to create and manage telemetry data \
such as traces, metrics, and logs."
HOMEPAGE = "https://github.com/open-telemetry/opentelemetry-cpp"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS = "nlohmann-json"

SRC_URI = "git://github.com/open-telemetry/opentelemetry-cpp.git;protocol=https;branch=main \
           file://0001-Fix-build-with-clang-19.patch"
SRCREV = "fa0a5200ddbe9ae6f487a228e658aedc102dab56"

S = "${WORKDIR}/git"
inherit cmake pkgconfig lib_package

LDFLAGS:append:riscv32 = " -latomic"

PACKAGECONFIG ?= "opentelemety_install otlp_api"

PACKAGECONFIG[opentelemety_install]="-DOPENTELEMETRY_INSTALL=ON,-DOPENTELEMETRY_INSTALL=OFF"
PACKAGECONFIG[build_package]="-DBUILD_PACKAGE=ON,-DBUILD_PACKAGE=OFF"
PACKAGECONFIG[otlp_api]="-DWITH_OTLP_API=ON,-DWITH_OTLP_API=OFF"
PACKAGECONFIG[otlp_grpc]="-DWITH_OTLP_GRPC=ON,-DWITH_OTLP_GRPC=OFF"
PACKAGECONFIG[otlp_http]="-DWITH_OTLP_HTTP=ON,-DWITH_OTLP_HTTP=OFF"
PACKAGECONFIG[otlp_prometheus]="-DWITH_PROMETHEUS=ON,-DWITH_PROMETHEUS=OFF"
PACKAGECONFIG[benchmark]="-DWITH_BENCHMARK=ON,-DWITH_BENCHMARK=OFF"
PACKAGECONFIG[testing]="-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF"
