SUMMARY = "Fast data collector for Embedded Linux"
HOMEPAGE = "http://fluentbit.io"
BUGTRACKER = "https://github.com/fluent/fluent-bit/issues"

SRC_URI = "http://fluentbit.io/releases/0.12/fluent-bit-${PV}.tar.gz \
           file://jemalloc.patch \
           "
SRC_URI[md5sum] = "7c8708312ac9122faacf9e2a4751eb34"
SRC_URI[sha256sum] = "23a81087edf0e2c6f2d49411c6a82308afc5224f67bbaa45729c057af62e9241"

S = "${WORKDIR}/fluent-bit-${PV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

DEPENDS = "zlib"
INSANE_SKIP_${PN}-dev += "dev-elf"

inherit cmake systemd

EXTRA_OECMAKE = "-DGNU_HOST=${HOST_SYS} -DFLB_ALL=ON -DFLB_TD=1"

# With Ninja it fails with:
# ninja: error: build.ninja:134: bad $-escape (literal $ must be written as $$)
OECMAKE_GENERATOR = "Unix Makefiles"

SYSTEMD_SERVICE_${PN} = "td-agent-bit.service"

TARGET_CC_ARCH_append = " ${SELECTED_OPTIMIZATION}"
