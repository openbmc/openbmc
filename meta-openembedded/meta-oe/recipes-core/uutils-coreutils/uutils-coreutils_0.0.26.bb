# Copyright (c) 2022, Snap Inc.
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "coreutils ~ GNU coreutils (updated); implemented as universal (cross-platform) utils, written in Rust"
HOMEPAGE = "https://github.com/uutils/coreutils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e74349878141b240070458d414ab3b64"

inherit cargo cargo-update-recipe-crates

SRC_URI += "git://github.com/uutils/coreutils.git;protocol=https;branch=main"

# musl not supported because the libc crate does not support functions like "endutxent" at the moment,
# so src/uucore/src/lib/features.rs disables utmpx when targetting musl.
COMPATIBLE_HOST:libc-musl = "null"

SRCREV = "f95f363096610d7e5e1556d6d0a32b5018065c4c"
S = "${WORKDIR}/git"

require ${BPN}-crates.inc

PROVIDES = "coreutils"
RPROVIDES:${PN} = "coreutils"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"

PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"

CARGO_BUILD_FLAGS += "--features unix"
CARGO_BUILD_FLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'selinux', '--features feat_selinux', '', d)}"

DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'selinux', 'clang-native libselinux-native', '', d)}"

export LIBCLANG_PATH = "${WORKDIR}/recipe-sysroot-native${libdir}"
export SELINUX_LIB_DIR = "${WORKDIR}/recipe-sysroot-native${libdir}"
export SELINUX_INCLUDE_DIR = "${WORKDIR}/recipe-sysroot-native${includedir}"

# The code which follows is strongly inspired from the GNU coreutils bitbake recipe:

# [ df mktemp nice printenv base64 gets a special treatment and is not included in this
bindir_progs = "[ arch basename cksum comm csplit cut dir dircolors dirname du \
                env expand expr factor fmt fold groups head hostid id install \
                join link logname md5sum mkfifo nl nohup nproc od paste pathchk \
                pinky pr printf ptx readlink realpath seq sha1sum sha224sum sha256sum \
                sha384sum sha512sum shred shuf sort split sum tac tail tee test timeout \
                tr truncate tsort tty unexpand uniq unlink uptime users vdir wc who whoami yes"

bindir_progs += "${@bb.utils.contains('PACKAGECONFIG', 'selinux', 'chcon runcon', '', d)}"

base_bindir_progs = "cat chgrp chmod chown cp date dd echo false hostname kill ln ls mkdir \
                     mknod mv pwd rm rmdir sleep stty sync touch true uname stat"

sbindir_progs= "chroot"

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
