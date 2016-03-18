# Based off of [1], appears to be MIT licensed [2].
#
# [1] https://github.com/dirtybit/gumstix-yocto/blob/master/meta/recipes-devtools/qemu/qemu_git.bb
# [2] https://github.com/dirtybit/gumstix-yocto/blob/master/meta/COPYING.MIT

require qemu.inc

SRCREV = "d1f8764099022bc1173f2413331b26d4ff609a0c"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
                    file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913"

SRC_URI = "git://github.com/openbmc/qemu.git;protocol=https"
S = "${WORKDIR}/git"

COMPATIBLE_HOST_class-target_mips64 = "null"

do_install_append() {
    # Prevent QA warnings about installed ${localstatedir}/run
    if [ -d ${D}${localstatedir}/run ]; then rmdir ${D}${localstatedir}/run; fi
}
