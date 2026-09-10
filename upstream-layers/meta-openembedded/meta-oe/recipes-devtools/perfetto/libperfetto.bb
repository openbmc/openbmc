LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b06e43f2e368eb8663632d529232353"

require perfetto.inc

inherit meson

# The amalgamated C++ SDK (sdk/perfetto.{h,cc}) is no longer part of the git
# release branches as of v52 - it now ships only as a GitHub release asset.
SRC_URI:append = " \
    file://0001-meson-add-pc-file-for-lib_perfetto.patch \
    https://github.com/google/perfetto/releases/download/v${PV}/perfetto-cpp-sdk-src.zip;subdir=${BP}/sdk;name=sdk \
    "
SRC_URI[sdk.sha256sum] = "c6fa3d89aee30f7da39402c9cd178c9f2e344544fda5c2109fd8457e319c3a2f"

LDFLAGS += "-Wl,--as-needed -latomic -Wl,--no-as-needed"

FILES:${PN} += "${datadir}"

BBCLASSEXTEND = "native nativesdk"
