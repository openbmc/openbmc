SUMMARY = "A suite of basic system administration utilities"
HOMEPAGE = "https://en.wikipedia.org/wiki/Util-linux"
DESCRIPTION = "Util-linux includes a suite of basic system administration utilities \
commonly found on most Linux systems.  Some of the more important utilities include \
disk partitioning, kernel message management, filesystem creation, and system login."

SECTION = "base"

LICENSE = "GPL-1.0-or-later & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause & MIT"
LICENSE:${PN}-fcntl-lock = "MIT"
LICENSE:${PN}-fdisk = "GPL-1.0-or-later"
LICENSE:${PN}-libblkid = "LGPL-2.1-or-later"
LICENSE:${PN}-libfdisk = "LGPL-2.1-or-later"
LICENSE:${PN}-libmount = "LGPL-2.1-or-later"
LICENSE:${PN}-libsmartcols = "LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://README.licensing;md5=cc80239f106687ab39ef0271ff5cf4ba \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://Documentation/licenses/COPYING.GPL-2.0-or-later;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://Documentation/licenses/COPYING.LGPL-2.1-or-later;md5=4fbd65380cdd255951079008b364516c \
                    file://Documentation/licenses/COPYING.BSD-3-Clause;md5=58dcd8452651fc8b07d1f65ce07ca8af \
                    file://Documentation/licenses/COPYING.BSD-4-Clause-UC;md5=263860f8968d8bafa5392cab74285262 \
                    file://libuuid/COPYING;md5=6d2cafc999feb2c2de84d4d24b23290c \
                    file://libmount/COPYING;md5=7c7e39fb7d70ffe5d693a643e29987c2 \
                    file://libblkid/COPYING;md5=693bcbbe16d3a4a4b37bc906bc01cc04 \
                    file://libfdisk/COPYING;md5=693bcbbe16d3a4a4b37bc906bc01cc04 \
                    file://libsmartcols/COPYING;md5=693bcbbe16d3a4a4b37bc906bc01cc04 \
                    "

FILESEXTRAPATHS:prepend := "${THISDIR}/util-linux:"
MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
SRC_URI = "${KERNELORG_MIRROR}/linux/utils/util-linux/v${MAJOR_VERSION}/util-linux-${PV}.tar.xz \
           file://mit-license.patch \
           file://configure-sbindir.patch \
           file://runuser.pamd \
           file://runuser-l.pamd \
           file://ptest.patch \
           file://run-ptest \
           file://display_testname_for_subtest.patch \
           file://avoid_parallel_tests.patch \
           file://0001-login-utils-include-libgen.h-for-basename-API.patch \
           file://fcntl-lock.c \
           file://CVE-2024-28085-0001.patch \
           file://CVE-2024-28085-0002.patch \
           "

SRC_URI[sha256sum] = "7b6605e48d1a49f43cc4b4cfc59f313d0dd5402fa40b96810bd572e167dfed0f"
