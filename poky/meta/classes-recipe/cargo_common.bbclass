#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

##
## Purpose:
## This class is to support building with cargo. It
## must be different than cargo.bbclass because Rust
## now builds with Cargo but cannot use cargo.bbclass
## due to dependencies and assumptions in cargo.bbclass
## that Rust & Cargo are already installed. So this
## is used by cargo.bbclass and Rust
##

# add crate fetch support
inherit rust-common

# Where we download our registry and dependencies to
export CARGO_HOME = "${WORKDIR}/cargo_home"

# The pkg-config-rs library used by cargo build scripts disables itself when
# cross compiling unless this is defined. We set up pkg-config appropriately
# for cross compilation, so tell it we know better than it.
export PKG_CONFIG_ALLOW_CROSS = "1"

# Don't instruct cargo to use crates downloaded by bitbake. Some rust packages,
# for example the rust compiler itself, come with their own vendored sources.
# Specifying two [source.crates-io] will not work.
CARGO_DISABLE_BITBAKE_VENDORING ??= "0"

# Used by libstd-rs to point to the vendor dir included in rustc src
CARGO_VENDORING_DIRECTORY ??= "${CARGO_HOME}/bitbake"

# The directory of the Cargo.toml relative to the root directory, per default
# assume there's a Cargo.toml directly in the root directory
CARGO_SRC_DIR ??= ""

# The actual path to the Cargo.toml
CARGO_MANIFEST_PATH ??= "${S}/${CARGO_SRC_DIR}/Cargo.toml"

# Path to Cargo.lock
CARGO_LOCK_PATH ??= "${@ os.path.join(os.path.dirname(d.getVar('CARGO_MANIFEST_PATH', True)), 'Cargo.lock')}"

CARGO_RUST_TARGET_CCLD ??= "${RUST_TARGET_CCLD}"
cargo_common_do_configure () {
	mkdir -p ${CARGO_HOME}/bitbake

	cat <<- EOF > ${CARGO_HOME}/config
	# EXTRA_OECARGO_PATHS
	paths = [
	$(for p in ${EXTRA_OECARGO_PATHS}; do echo \"$p\",; done)
	]
	EOF

	cat <<- EOF >> ${CARGO_HOME}/config

	# Local mirror vendored by bitbake
	[source.bitbake]
	directory = "${CARGO_VENDORING_DIRECTORY}"
	EOF

	if [ ${CARGO_DISABLE_BITBAKE_VENDORING} = "0" ]; then
		cat <<- EOF >> ${CARGO_HOME}/config

		[source.crates-io]
		replace-with = "bitbake"
		local-registry = "/nonexistent"
		EOF
	fi

	cat <<- EOF >> ${CARGO_HOME}/config

	[http]
	# Multiplexing can't be enabled because http2 can't be enabled
	# in curl-native without dependency loops
	multiplexing = false

	# Ignore the hard coded and incorrect path to certificates
	cainfo = "${STAGING_ETCDIR_NATIVE}/ssl/certs/ca-certificates.crt"

	EOF

	cat <<- EOF >> ${CARGO_HOME}/config

	# HOST_SYS
	[target.${RUST_HOST_SYS}]
	linker = "${CARGO_RUST_TARGET_CCLD}"
	EOF

	if [ "${RUST_HOST_SYS}" != "${RUST_BUILD_SYS}" ]; then
		cat <<- EOF >> ${CARGO_HOME}/config

		# BUILD_SYS
		[target.${RUST_BUILD_SYS}]
		linker = "${RUST_BUILD_CCLD}"
		EOF
	fi

	if [ "${RUST_TARGET_SYS}" != "${RUST_BUILD_SYS}" -a "${RUST_TARGET_SYS}" != "${RUST_HOST_SYS}" ]; then
		cat <<- EOF >> ${CARGO_HOME}/config

		# TARGET_SYS
		[target.${RUST_TARGET_SYS}]
		linker = "${RUST_TARGET_CCLD}"
		EOF
	fi

	# Put build output in build directory preferred by bitbake instead of
	# inside source directory unless they are the same
	if [ "${B}" != "${S}" ]; then
		cat <<- EOF >> ${CARGO_HOME}/config

		[build]
		# Use out of tree build destination to avoid polluting the source tree
		target-dir = "${B}/target"
		EOF
	fi

	cat <<- EOF >> ${CARGO_HOME}/config

	[term]
	progress.when = 'always'
	progress.width = 80
	EOF
}

python cargo_common_do_patch_paths() {
    import shutil

    cargo_config = os.path.join(d.getVar("CARGO_HOME"), "config")
    if not os.path.exists(cargo_config):
        return

    src_uri = (d.getVar('SRC_URI') or "").split()
    if len(src_uri) == 0:
        return

    patches = dict()
    workdir = d.getVar('WORKDIR')
    fetcher = bb.fetch2.Fetch(src_uri, d)
    for url in fetcher.urls:
        ud = fetcher.ud[url]
        if ud.type == 'git':
            name = ud.parm.get('name')
            destsuffix = ud.parm.get('destsuffix')
            if name is not None and destsuffix is not None:
                if ud.user:
                    repo = '%s://%s@%s%s' % (ud.proto, ud.user, ud.host, ud.path)
                else:
                    repo = '%s://%s%s' % (ud.proto, ud.host, ud.path)
                path = '%s = { path = "%s" }' % (name, os.path.join(workdir, destsuffix))
                patches.setdefault(repo, []).append(path)

    with open(cargo_config, "a+") as config:
        for k, v in patches.items():
            print('\n[patch."%s"]' % k, file=config)
            for name in v:
                print(name, file=config)

    if not patches:
        return

    # Cargo.lock file is needed for to be sure that artifacts
    # downloaded by the fetch steps are those expected by the
    # project and that the possible patches are correctly applied.
    # Moreover since we do not want any modification
    # of this file (for reproducibility purpose), we prevent it by
    # using --frozen flag (in CARGO_BUILD_FLAGS) and raise a clear error
    # here is better than letting cargo tell (in case the file is missing)
    # "Cargo.lock should be modified but --frozen was given"

    lockfile = d.getVar("CARGO_LOCK_PATH", True)
    if not os.path.exists(lockfile):
        bb.fatal(f"{lockfile} file doesn't exist")

    # There are patched files and so Cargo.lock should be modified but we use
    # --frozen so let's handle that modifications here.
    #
    # Note that a "better" (more elegant ?) would have been to use cargo update for
    # patched packages:
    #  cargo update --offline -p package_1 -p package_2
    # But this is not possible since it requires that cargo local git db
    # to be populated and this is not the case as we fetch git repo ourself.

    lockfile_orig = lockfile + ".orig"
    if not os.path.exists(lockfile_orig):
        shutil.copy(lockfile, lockfile_orig)

    newlines = []
    with open(lockfile_orig, "r") as f:
        for line in f.readlines():
            if not line.startswith("source = \"git"):
                newlines.append(line)

    with open(lockfile, "w") as f:
        f.writelines(newlines)
}
do_configure[postfuncs] += "cargo_common_do_patch_paths"

do_compile:prepend () {
        oe_cargo_fix_env
}

oe_cargo_fix_env () {
	export CC="${RUST_TARGET_CC}"
	export CXX="${RUST_TARGET_CXX}"
	export CFLAGS="${CFLAGS}"
	export CXXFLAGS="${CXXFLAGS}"
	export AR="${AR}"
	export TARGET_CC="${RUST_TARGET_CC}"
	export TARGET_CXX="${RUST_TARGET_CXX}"
	export TARGET_CFLAGS="${CFLAGS}"
	export TARGET_CXXFLAGS="${CXXFLAGS}"
	export TARGET_AR="${AR}"
	export HOST_CC="${RUST_BUILD_CC}"
	export HOST_CXX="${RUST_BUILD_CXX}"
	export HOST_CFLAGS="${BUILD_CFLAGS}"
	export HOST_CXXFLAGS="${BUILD_CXXFLAGS}"
	export HOST_AR="${BUILD_AR}"
}

EXTRA_OECARGO_PATHS ??= ""

EXPORT_FUNCTIONS do_configure

# The culprit for this setting is the libc crate,
# which as of Jun 2023 calls directly into 32 bit time functions in glibc,
# bypassing all of glibc provisions to choose the right Y2038-safe functions. As
# rust components statically link with that crate, pretty much everything
# is affected, and so there's no point trying to have recipe-specific
# INSANE_SKIP entries.
#
# Upstream ticket and PR:
# https://github.com/rust-lang/libc/issues/3223
# https://github.com/rust-lang/libc/pull/3175
INSANE_SKIP:append = " 32bit-time"
