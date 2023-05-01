RUST_VERSION ?= "${@d.getVar('PV').split('-')[0]}"

SRC_URI += "https://static.rust-lang.org/dist/rustc-${RUST_VERSION}-src.tar.xz;name=rust \
            file://crossbeam_atomic.patch;patchdir=${RUSTSRC} \
            file://hardcodepaths.patch;patchdir=${RUSTSRC} \
            file://getrandom-open64.patch;patchdir=${RUSTSRC} \
            file://0001-Do-not-use-LFS64-on-linux-with-musl.patch;patchdir=${RUSTSRC} \
            file://zlib-off64_t.patch;patchdir=${RUSTSRC} \
"
SRC_URI[rust.sha256sum] = "5b8ea94085b65e75c1fa6310e2f90bd706fa80bfcb3544fe26f4037b911d9fb2"

RUSTSRC = "${WORKDIR}/rustc-${RUST_VERSION}-src"

# Used by crossbeam_atomic.patch
export TARGET_VENDOR

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"

# see recipes-devtools/gcc/gcc/0018-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch
# we need to link with ssp_nonshared on musl to avoid "undefined reference to `__stack_chk_fail_local'"
# when building MACHINE=qemux86 for musl
WRAPPER_TARGET_EXTRALD:libc-musl = "-lssp_nonshared"
