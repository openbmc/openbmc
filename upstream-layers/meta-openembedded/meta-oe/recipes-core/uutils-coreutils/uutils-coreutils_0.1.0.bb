# Copyright (c) 2022, Snap Inc.
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "coreutils ~ GNU coreutils (updated); implemented as universal (cross-platform) utils, written in Rust"
HOMEPAGE = "https://github.com/uutils/coreutils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e74349878141b240070458d414ab3b64"

inherit cargo cargo-update-recipe-crates

SRC_URI += "git://github.com/uutils/coreutils.git;protocol=https;branch=main \
    file://0001-do-not-compile-stdbuf.patch \
    file://0002-Bump-onig-from-6.4.0-to-6.5.1.patch \
"

SRCREV = "18b963ed6f612ac30ebca92426280cf4c1451f6a"

require ${BPN}-crates.inc

PROVIDES = "coreutils"
RPROVIDES:${PN} = "coreutils"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[selinux] = "--features feat_selinux,,clang-native libselinux-native libselinux"

CARGO_BUILD_FLAGS += "--features unix"

# The code which follows is strongly inspired from the GNU coreutils bitbake recipe:

# df mktemp nice printenv base32 base64 get a special treatment and are not included in this
bindir_progs = "[ arch basename cksum comm csplit cut dir dircolors dirname du \
                env expand expr factor fmt fold groups head hostid id install \
                join link logname md5sum mkfifo nl nohup nproc od paste pathchk \
                pinky pr printf ptx readlink realpath seq sha1sum sha224sum sha256sum \
                sha384sum sha512sum shred shuf sort split sum tac tail tee test timeout \
                tr truncate tsort tty unexpand uniq unlink uptime users vdir wc who whoami yes"

bindir_progs += "${@bb.utils.contains('PACKAGECONFIG', 'selinux', 'chcon runcon', '', d)}"

base_bindir_progs = "cat chgrp chmod chown cp date dd echo false hostname kill ln ls mkdir \
                     mknod mv pwd rm rmdir sleep stty sync touch true uname stat"

sbindir_progs = "chroot"

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

do_compile:prepend() {
    # In principle this is supposed to be exported by the project's .cargo/config.toml file, but for some reason it's not working
    export PROJECT_NAME_FOR_VERSION_STRING="uutils coreutils"
}
