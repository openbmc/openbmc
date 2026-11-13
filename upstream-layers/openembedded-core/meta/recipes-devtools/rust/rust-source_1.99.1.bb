# rust-source_${PN}.bb - Shared source recipe for the Rust toolchain
#
# This recipe fetches, unpacks, and patches the rustc source tree into a
# work-shared location so that rust, cargo, libstd-rs, and their native/nativesdk
# variants all share a single copy of the source. This eliminates duplication
# (up to 11 copies of the ~4.2 GB source tree in multilib + nativesdk builds)
# and significantly reduces disk usage (~46.2 GB -> ~4.2 GB for rustc-src).
#
# Only do_fetch, do_unpack, and do_patch are meaningful here. All other
# tasks are disabled since this recipe exists solely to provide sources.

SUMMARY = "Rust sources"
HOMEPAGE = "http://www.rust-lang.org"
SECTION = "devel"
LICENSE = "Unicode-3.0 AND (Apache-2.0 OR MIT)"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=11a3899825f4376896e438c8c753f8dc"

# This recipe only provides source; disable build/install/packaging tasks
deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot
deltask do_populate_lic

# Prevent rm_work from removing the shared source tree
RM_WORK_EXCLUDE += "${PN}"

require rust-source.inc
require rust-snapshot.inc

inherit allarch nopackages

PN = "rust-source-${PV}"
BPN = "rust-source"

# Place WORKDIR under work-shared so all consumers reference the same location
WORKDIR = "${TMPDIR}/work-shared/rust-source-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:rust-source::${PV}:${PR}::${SSTATE_VERSION}:"

# Use work-shared stamp directory so sstate is shared across all architectures
STAMP = "${STAMPS_DIR}/work-shared/rust-source-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/rust-source-${PV}-*"

DEPENDS = ""
PACKAGES = ""
baselib = "lib"
PACKAGE_ARCH = "all"

B = "${WORKDIR}/build"

# rust-source.bb does not inherit rust-common.bbclass (it is allarch/nopackages
# and has no toolchain dependencies), so we define RUST_BUILD_ARCH locally for
# snapshot URL resolution in rust-snapshot.inc.
RUST_BUILD_ARCH = "${@{'ppc': 'powerpc', 'ppc64': 'powerpc64', 'ppc64le': 'powerpc64le', 'riscv64': 'riscv64gc'}.get(d.getVar('BUILD_ARCH'), d.getVar('BUILD_ARCH'))}"

EXCLUDE_FROM_WORLD = "1"

addtask do_update_snapshot after do_patch
do_update_snapshot[nostamp] = "1"

# Run with `bitbake rust-source-${PV} -c update_snapshot` to update `rust-snapshot.inc`
# with the checksums for the rust snapshot associated with this rustc-src tarball.
python do_update_snapshot() {
    import json
    import re
    import sys

    from collections import defaultdict

    key_value_pairs = {}
    with open(os.path.join(d.getVar("S"), "src", "stage0")) as f:
        for line in f:
            # Skip empty lines or comments
            if not line.strip() or line.startswith("#"):
                continue
            # Split the line into key and value using '=' as separator
            match = re.match(r'(\S+)\s*=\s*(\S+)', line.strip())
            if match:
                key = match.group(1)
                value = match.group(2)
                key_value_pairs[key] = value
    # Extract the required values from key_value_pairs
    config_dist_server = key_value_pairs.get('dist_server', '')
    compiler_date = key_value_pairs.get('compiler_date', '')
    compiler_version = key_value_pairs.get('compiler_version', '')

    src_uri = defaultdict(list)
    # Assuming checksums_sha256 is now a key-value pair like: checksum_key = checksum_value
    for k, v in key_value_pairs.items():
        # Match the pattern for checksums
        if "dist" in k and "tar.xz" in k:
            m = re.search(f"dist/{compiler_date}/(?P<component>.*)-{compiler_version}-(?P<arch>.*)-unknown-linux-gnu\\.tar\\.xz", k)
            if m:
                component = m.group('component')
                arch = m.group('arch')
                src_uri[arch].append(f"SRC_URI[{component}-snapshot-{arch}.sha256sum] = \"{v}\"")
    # Create the snapshot string with the extracted values
    snapshot = """\
## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "%s"

""" % compiler_version
    # Add the checksum components to the snapshot
    for arch, components in src_uri.items():
        snapshot += "\n".join(components) + "\n\n"
    # Add the additional snapshot URIs
    snapshot += """\
SRC_URI += " \\
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \\
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \\
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \\
"

RUST_DIST_SERVER = "%s"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
""" % config_dist_server
    # Write the updated snapshot information to the rust-snapshot.inc file
    with open(os.path.join(d.getVar("THISDIR"), "rust-snapshot.inc"), "w") as f:
        f.write(snapshot)
}
