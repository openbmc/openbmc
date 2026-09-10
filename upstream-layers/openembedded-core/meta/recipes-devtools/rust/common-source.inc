# common-source.inc - Disable source tasks for recipes using shared rust-source
#
# Recipes that include this file rely on rust-source-${PV} for fetching,
# unpacking, and patching the Rust source tree. This avoids duplicating the
# ~4.2 GB rustc-src tree per recipe variant (rust, cargo, libstd-rs and their
# native/nativesdk counterparts).

# Disable local fetch/unpack/patch since rust-source-${PV} handles them
do_fetch() {
        :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

# Clear SRC_URI to prevent fetcher warnings from stale URIs inherited via .inc
SRC_URI = ""

# Ensure the shared source is fully ready before dependent tasks run
do_configure[depends] += "rust-source-${PV}:do_patch"
do_populate_lic[depends] += "rust-source-${PV}:do_unpack"
do_create_spdx[depends] += "rust-source-${PV}:do_patch"

# The SPDX class uses is_work_shared_spdx() to detect shared workdirs.
# Since we redirect S rather than WORKDIR, the default detection fails.
# Override to always return True so SPDX processing works correctly.
def is_work_shared_spdx(d):
    return True
