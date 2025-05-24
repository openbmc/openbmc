RUST_VERSION ?= "${@d.getVar('PV').split('-')[0]}"

SRC_URI += "https://static.rust-lang.org/dist/rustc-${RUST_VERSION}-src.tar.xz;name=rust \
            file://rv32-cargo-rustix-0.38.38-fix.patch;patchdir=${RUSTSRC} \
            file://rust-oe-selftest.patch;patchdir=${RUSTSRC} \
	    file://repro-issue-fix-with-cc-crate-hashmap.patch;patchdir=${RUSTSRC} \
            file://oeqa-selftest-Increase-timeout-in-process-sigpipe-ru.patch;patchdir=${RUSTSRC} \
            file://0001-src-core-build_steps-tool.rs-switch-off-lto-for-rust.patch;patchdir=${RUSTSRC} \
	    file://revert-link-std-statically-in-rustc_driver-feature.patch;patchdir=${RUSTSRC} \
	    file://Zdual-proc-macros-additional-check.patch;patchdir=${RUSTSRC} \
	    file://fix-tidy-check-failure.patch;patchdir=${RUSTSRC} \
"
SRC_URI[rust.sha256sum] = "e23ec747a06ffd3e94155046f40b6664ac152c9ee3c2adfd90353a7ccff24226"

RUSTSRC = "${WORKDIR}/rustc-${RUST_VERSION}-src"

#After updating to Rust 1.85 we can revert this removal of "src/gcc"
remove_gcc_directory() {
    # Ensure the gcc directory exists
    gcc_dir="${RUSTSRC}/src/gcc"
    if [ -d "$gcc_dir" ]; then
        rm -rf "$gcc_dir"
    fi
}
# Add postfunc to the do_unpack task
do_unpack[postfuncs] += "remove_gcc_directory"

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"

CVE_STATUS[CVE-2024-24576] = "not-applicable-platform: Issue only applies on Windows"
CVE_STATUS[CVE-2024-43402] = "not-applicable-platform: Issue only applies on Windows"
