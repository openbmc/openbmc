require glibc-common.inc

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 \
                     file://${COMMON_LICENSE_DIR}/LGPL-2.1;md5=1a6d268fd218675ffea8be556788b780"

deltask do_fetch
deltask do_unpack
deltask do_patch
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install[depends] += "virtual/${MLPREFIX}libc:do_stash_locale"
