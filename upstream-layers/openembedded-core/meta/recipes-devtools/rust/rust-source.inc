RUST_VERSION ?= "${@d.getVar('PV').split('-')[0]}"

SRC_URI += "https://static.rust-lang.org/dist/rustc-${RUST_VERSION}-src.tar.xz;name=rust \
            file://0001-Define-more-ioctl-codes-on-riscv32gc-unknown-linux-g.patch;patchdir=${RUSTSRC} \
            file://rust-oe-selftest.patch;patchdir=${RUSTSRC} \
            file://repro-issue-fix-with-cc-crate-hashmap.patch;patchdir=${RUSTSRC} \
            file://0001-src-core-build_steps-tool.rs-switch-off-lto-for-rust.patch;patchdir=${RUSTSRC} \
            file://0001-riscv32-Define-plain-syscalls-as-their-time64-varian.patch;patchdir=${RUSTSRC} \
            file://0001-Update-call-llvm-intrinsics-test.patch;patchdir=${RUSTSRC} \
            file://0001-Fix-flaky-assertions-in-oneshot-tests.patch;patchdir=${RUSTSRC} \
            file://0001-Update-amdgpu-data-layout.patch;patchdir=${RUSTSRC} \
            file://0001-Adjust-loongarch-assembly-test.patch;patchdir=${RUSTSRC} \
            file://0001-Fix-multiple-option-or-permutations-test-for-big-end.patch;patchdir=${RUSTSRC} \
"
SRC_URI[rust.sha256sum] = "174fce10ce012317ca995810296d8af199318838180b03d68a853e0f02d4b571"

RUSTSRC = "${UNPACKDIR}/rustc-${RUST_VERSION}-src"

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"
