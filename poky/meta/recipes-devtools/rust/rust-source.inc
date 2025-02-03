RUST_VERSION ?= "${@d.getVar('PV').split('-')[0]}"

SRC_URI += "https://static.rust-lang.org/dist/rustc-${RUST_VERSION}-src.tar.xz;name=rust \
            file://zlib-off64_t.patch;patchdir=${RUSTSRC} \
            file://rv32-rustix-libc-backend.patch;patchdir=${RUSTSRC} \
            file://rv32-cargo-rustix-0.38.28-fix.patch;patchdir=${RUSTSRC} \
            file://rustdoc-repro-issue-fix-cargo-config-for-codegenunits.patch;patchdir=${RUSTSRC} \
            file://rust-oe-selftest.patch;patchdir=${RUSTSRC} \
            file://repro-issue-fix-with-cc-crate-hashmap.patch;patchdir=${RUSTSRC} \
            file://oeqa-selftest-Increase-timeout-in-process-sigpipe-ru.patch;patchdir=${RUSTSRC} \
"
SRC_URI[rust.sha256sum] = "36217ef7e32f40a180e3d79bd666b4dfdaed49dd381023a5fb765fd12d0092ce"

RUSTSRC = "${WORKDIR}/rustc-${RUST_VERSION}-src"

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"

CVE_STATUS[CVE-2024-24576] = "not-applicable-platform: Issue only applies on Windows"
CVE_STATUS[CVE-2024-43402] = "not-applicable-platform: Issue only applies on Windows"
