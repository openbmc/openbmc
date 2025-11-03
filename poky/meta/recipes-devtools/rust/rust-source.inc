RUST_VERSION ?= "${@d.getVar('PV').split('-')[0]}"

SRC_URI += "https://static.rust-lang.org/dist/rustc-${RUST_VERSION}-src.tar.xz;name=rust \
            file://hardcodepaths.patch;patchdir=${RUSTSRC} \
            file://zlib-off64_t.patch;patchdir=${RUSTSRC} \
            file://0001-Revert-Map-source-absolute-paths-to-OUT_DIR-as-relat.patch;patchdir=${RUSTSRC} \
            file://rv32-missing-syscalls.patch;patchdir=${RUSTSRC} \
            file://rv32-rustix-libc-backend.patch;patchdir=${RUSTSRC} \
            file://rv32-cargo-rustix-0.38.19-fix.patch;patchdir=${RUSTSRC} \
            file://custom-target-cfg.patch;patchdir=${RUSTSRC} \
            file://rustc-bootstrap.patch;patchdir=${RUSTSRC} \
            file://target-build-value.patch;patchdir=${RUSTSRC} \
            file://0001-Handle-vendored-sources-when-remapping-paths.patch;patchdir=${RUSTSRC} \
            file://repro-issue-fix-with-v175.patch;patchdir=${RUSTSRC} \
            file://0001-cargo-do-not-write-host-information-into-compilation.patch;patchdir=${RUSTSRC} \
"
SRC_URI[rust.sha256sum] = "4526f786d673e4859ff2afa0bab2ba13c918b796519a25c1acce06dba9542340"

RUSTSRC = "${WORKDIR}/rustc-${RUST_VERSION}-src"

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"

CVE_STATUS[CVE-2024-24576] = "not-applicable-platform: Issue only applies on Windows"
CVE_STATUS[CVE-2024-43402] = "not-applicable-platform: Issue only applies on Windows"
