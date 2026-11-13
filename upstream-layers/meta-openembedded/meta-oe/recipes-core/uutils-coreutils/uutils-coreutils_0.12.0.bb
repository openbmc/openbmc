# Copyright (c) 2022, Snap Inc.
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "Cross-platform Rust reimplementation of the GNU coreutils"
HOMEPAGE = "https://github.com/uutils/coreutils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e74349878141b240070458d414ab3b64"

inherit cargo cargo-update-recipe-crates

SRC_URI = " \
    git://github.com/uutils/coreutils.git;protocol=https;branch=main;tag=${PV} \
"

SRCREV = "dc1efd89948a9ca4c78c3a4b9a6ac891019a8c69"

require ${BPN}-crates.inc

# Only claim coreutils when selected, separately for each of the target, native
# and nativesdk variants. Unlike world, universe builds every recipe, so an
# unconditional PROVIDES makes both recipes provide coreutils in the same build.
PROVIDES = "${@'coreutils' if d.getVar('PREFERRED_PROVIDER_' + d.getVar('PN').replace('uutils-', '')) == d.getVar('PN') else ''}"
RPROVIDES:${PN} = "coreutils"

# This recipe is a drop-in replacement for coreutils and cannot coexist with it,
# so only build it in world/universe when it is the selected provider. Otherwise
# both recipes end up in the same task graph as explicit targets (PREFERRED_PROVIDER
# only disambiguates dependencies, not targets) and bitbake warns that multiple
# .bb files provide coreutils.
EXCLUDE_FROM_WORLD = "${@'0' if d.getVar('PREFERRED_PROVIDER_coreutils') == 'uutils-coreutils' else '1'}"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux systemd', d)}"
PACKAGECONFIG[selinux] = "--features feat_selinux,,clang-native libselinux-native libselinux"
PACKAGECONFIG[systemd] = "--features feat_systemd_logind,,systemd"

CARGO_BUILD_FLAGS += "--features unix --features feat_external_libstdbuf"

# The code which follows is strongly inspired from the GNU coreutils bitbake recipe:

# df mktemp nice printenv base32 base64 are not included in this, see base_bindir_progs_extra
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

# Like GNU coreutils, these are linked in ${base_bindir} rather than ${bindir}.
base_bindir_progs_extra = "base32 base64 df mktemp nice printenv"

ALTERNATIVE:${PN} = "${bindir_progs} ${base_bindir_progs} ${sbindir_progs} ${base_bindir_progs_extra}"

# Use the multicall binary named "coreutils" for symlinks
ALTERNATIVE_TARGET = "${bindir}/coreutils"

python __anonymous() {
    for prog in (d.getVar('base_bindir_progs') + ' ' + d.getVar('base_bindir_progs_extra')).split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_bindir'), prog))

    for prog in d.getVar('sbindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('sbindir'), prog))
}

BBCLASSEXTEND = "native nativesdk"

do_configure:prepend() {
    rm -f ${S}/.cargo/config.toml
}

do_compile:prepend() {
    export LIBSTDBUF_DIR="${libdir}/coreutils"
}

do_install:append() {
    mkdir -p ${D}${libdir}/coreutils
    cp ${B}/target/${CARGO_TARGET_SUBDIR}/deps/libstdbuf.so ${D}${libdir}/coreutils/libstdbuf.so
}
