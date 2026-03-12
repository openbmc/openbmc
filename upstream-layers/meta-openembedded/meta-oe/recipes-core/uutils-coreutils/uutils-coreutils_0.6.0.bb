# Copyright (c) 2022, Snap Inc.
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "coreutils ~ GNU coreutils (updated); implemented as universal (cross-platform) utils, written in Rust"
HOMEPAGE = "https://github.com/uutils/coreutils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e74349878141b240070458d414ab3b64"

inherit cargo cargo-update-recipe-crates

SRC_URI = " \
    git://github.com/uutils/coreutils.git;protocol=https;branch=main;tag=${PV} \
"

SRCREV = "2319a99057d11b0c9744c885df84806eea019166"

require ${BPN}-crates.inc

PROVIDES = "coreutils"
RPROVIDES:${PN} = "coreutils"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux systemd', d)}"
PACKAGECONFIG[selinux] = "--features feat_selinux,,clang-native libselinux-native libselinux"
PACKAGECONFIG[systemd] = "--features feat_systemd_logind,,systemd"

CARGO_BUILD_FLAGS += "--features unix --features feat_external_libstdbuf"

# The code which follows is strongly inspired from the GNU coreutils bitbake recipe:

# df mktemp nice printenv base32 base64 get a special treatment and are not included in this
bindir_progs = "[ arch basename cksum comm csplit cut dir dircolors dirname du \
                env expand expr factor fmt fold groups head hostid id install \
                join link logname md5sum mkfifo nl nohup nproc od paste pathchk \
                pinky pr printf ptx readlink realpath seq sha1sum sha224sum sha256sum \
                sha384sum sha512sum shred shuf sort split stdbuf sum tac tail tee test timeout \
                tr truncate tsort tty unexpand uniq unlink uptime users vdir wc who whoami yes"

bindir_progs += "${@bb.utils.contains('PACKAGECONFIG', 'selinux', 'chcon runcon', '', d)}"

base_bindir_progs = "cat chgrp chmod chown cp date dd echo false hostname kill ln ls mkdir \
                     mknod mv pwd rm rmdir sleep stty sync touch true uname stat"

sbindir_progs = "chroot"

FILES:${PN}:append = " ${libdir}/coreutils/libstdbuf.so"
inherit update-alternatives

# Higher than busybox (which uses 50)
ALTERNATIVE_PRIORITY = "100"

# Higher than net-tools (which uses 100)
ALTERNATIVE_PRIORITY[hostname] = "110"

ALTERNATIVE:${PN} = "${bindir_progs} ${base_bindir_progs} ${sbindir_progs} base32 base64 nice printenv mktemp df"

# Use the multicall binary named "coreutils" for symlinks
ALTERNATIVE_TARGET = "${bindir}/coreutils"

python __anonymous() {
    for prog in d.getVar('base_bindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_bindir'), prog))

    for prog in d.getVar('sbindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('sbindir'), prog))
}

BBCLASSEXTEND = "native nativesdk"

do_compile:prepend() {
    export LIBSTDBUF_DIR="${libdir}/coreutils"
}

do_install:append() {
    mkdir -p ${D}${libdir}/coreutils
    cp ${B}/target/${CARGO_TARGET_SUBDIR}/deps/libstdbuf.so ${D}${libdir}/coreutils/libstdbuf.so
}
