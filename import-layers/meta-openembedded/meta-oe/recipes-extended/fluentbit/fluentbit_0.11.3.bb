SUMMARY = "Fast data collector for Embedded Linux"
HOMEPAGE = "http://fluentbit.io"
BUGTRACKER = "https://github.com/fluent/fluent-bit/issues"

SRC_URI = "http://fluentbit.io/releases/0.11/fluent-bit-${PV}.tar.gz \
           file://0001-CMakeLists.txt-Add-AUTOCONF_HOST_OPT-to-help-cross-c.patch \
           file://0002-msgpack-Add-comment-for-intended-fallthrough.patch \
           "
SRC_URI[md5sum] = "9383262339412782b80cc49e7ad15609"
SRC_URI[sha256sum] = "eb8a85c656fa60682b0bf8dd1ad58d848cd251dab4f35a6777acd552c65b0511"

S = "${WORKDIR}/fluent-bit-${PV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

DEPENDS = "zlib"
INSANE_SKIP_${PN}-dev += "dev-elf"

inherit cmake systemd

EXTRA_OECMAKE = "-DGNU_HOST=${HOST_SYS}"

SYSTEMD_SERVICE_${PN} = "fluent-bit.service"
