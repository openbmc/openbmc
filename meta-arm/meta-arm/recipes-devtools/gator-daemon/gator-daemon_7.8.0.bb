SUMMARY = "DS-5 Streamline Gator daemon"
DESCRIPTION = "Target-side daemon gathering data for ARM Streamline \
               Performance Analyzer."
HOMEPAGE = "https://github.com/ARM-software/gator"

# Note that Gator uses the Linux Perf API for
# most of its data collection. Check that your Kernel follow the
# configuration requirement specified here:
# https://github.com/ARM-software/gator#kernel-configuration

LICENSE = "GPL-2.0-only & LGPL-2.1-or-later & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://libsensors/COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://mxml/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://k/perf_event.h;endline=14;md5=e548bf30a60b2ed11ef2dcf7bfdac230 \
                   "

SRCREV = "6a944e7ee1f1c3ab9b2a57efd24c58503122db02"
SRC_URI = "git://github.com/ARM-software/gator.git;protocol=http;branch=main;protocol=https \
           file://0001-daemon-mxml-Define-_GNU_SOURCE.patch;striplevel=2 \
           file://0001-Include-missing-cstdint.patch;striplevel=2 \
          "

S = "${WORKDIR}/git/daemon"

COMPATIBLE_HOST = "aarch64.*-linux"

EXTRA_OEMAKE = "'CFLAGS=${CFLAGS} ${TARGET_CC_ARCH} -D_DEFAULT_SOURCE -DETCDIR=\"${sysconfdir}\"' \
                'LDFLAGS=${LDFLAGS} ${TARGET_CC_ARCH}' 'CROSS_COMPILE=${TARGET_PREFIX}' \
                'CXXFLAGS=${CXXFLAGS} ${TARGET_CC_ARCH} -fno-rtti' CC='${CC}' CXX='${CXX}' V=1"

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 ${S}/gatord ${D}${sbindir}/gatord
}
