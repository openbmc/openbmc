SUMMARY = "Rust compiler and runtime libaries (cross-canadian for ${TARGET_ARCH} target)"
PN = "rust-cross-canadian-${TRANSLATED_TARGET_ARCH}"
CVE_PRODUCT = "rust"

inherit rust-target-config
inherit rust-common

LICENSE = "MIT"

MODIFYTOS = "0"

DEPENDS += "virtual/${SDK_PREFIX}gcc virtual/nativesdk-libc virtual/nativesdk-${SDK_PREFIX}compilerlibs"

SRC_URI += "file://target-rust-ccld.c"
LIC_FILES_CHKSUM = "file://target-rust-ccld.c;md5=af4e0e29f81a34cffe05aa07c89e93e9;endline=7"
S = "${WORKDIR}"

# As per NVD, this CVE only affects to Windows platform
# Link: https://nvd.nist.gov/vuln/detail/CVE-2024-43402
CVE_STATUS[CVE-2024-43402] = "not-applicable-platform: Issue only applies on Windows"

# Need to use our SDK's sh here, see #14878
create_sdk_wrapper () {
        file="$1"
        shift
        cat <<- EOF > "${file}"
		#!/bin/sh
		\$$1 \$@
		EOF

        chmod +x "$file"
}

do_install () {
    # Rust requires /usr/lib to contain the libs.
    # The required structure is retained for simplicity.
    SYS_LIBDIR=$(dirname ${D}${libdir})
    SYS_BINDIR=$(dirname ${D}${bindir})
    RUSTLIB_DIR=${SYS_LIBDIR}/${TARGET_SYS}/rustlib

    install -d ${RUSTLIB_DIR}
    install -m 0644 "${RUST_TARGETS_DIR}/${RUST_HOST_SYS}.json" "${RUSTLIB_DIR}"
    install -m 0644 "${RUST_TARGETS_DIR}/${RUST_TARGET_SYS}.json" "${RUSTLIB_DIR}"

    # Uses SDK's CC as linker so linked binaries works out of box.
    # We have a problem as rust sets LD_LIBRARY_PATH and this will break running host
    # binaries (even /bin/sh) in the SDK as they detect a newer glibc from the SDK
    # in those paths and we hit symbol errors. We saw particular problems with symbol
    # mismatch on ubuntu1804 during development. To avoid this we have an SDK built
    # binary which unsets LD_LIBRARY_PATH, which can then call the wrapper script
    # where the context is easier to do the env maniupations needed
    install -d ${SYS_BINDIR}
    outfile="${SYS_BINDIR}/target-rust-ccld"
    ${CC} ${WORKDIR}/target-rust-ccld.c -o $outfile
    chmod +x "$outfile"
    create_sdk_wrapper "${SYS_BINDIR}/target-rust-ccld-wrapper" "CC"

    ENV_SETUP_DIR=${D}${base_prefix}/environment-setup.d
    mkdir "${ENV_SETUP_DIR}"
    RUST_ENV_SETUP_SH="${ENV_SETUP_DIR}/${RUST_TARGET_SYS}_rust.sh"

    RUST_TARGET_TRIPLE=`echo ${RUST_TARGET_SYS} | tr '[:lower:]' '[:upper:]' | sed 's/-/_/g'`

    cat <<- EOF > "${RUST_ENV_SETUP_SH}"
	export CARGO_TARGET_${RUST_TARGET_TRIPLE}_RUSTFLAGS="--sysroot=\$OECORE_TARGET_SYSROOT/usr -C link-arg=--sysroot=\$OECORE_TARGET_SYSROOT"
	export RUST_TARGET_PATH="\$OECORE_NATIVE_SYSROOT/usr/lib/${TARGET_SYS}/rustlib"
	EOF

    chown -R root.root ${D}

}

FILES:${PN} += "${base_prefix}/environment-setup.d"

