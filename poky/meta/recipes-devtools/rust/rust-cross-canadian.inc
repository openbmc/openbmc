PN = "rust-cross-canadian-${TRANSLATED_TARGET_ARCH}"

inherit rust-target-config
inherit rust-common

LICENSE = "MIT"

MODIFYTOS = "0"

# Need to use our SDK's sh here, see #14878
create_sdk_wrapper () {
        file="$1"
        shift

        cat <<- EOF > "${file}"
		#!${base_prefix}/bin/sh
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
    install -d ${SYS_BINDIR}
    create_sdk_wrapper "${SYS_BINDIR}/target-rust-ccld" "CC"

    ENV_SETUP_DIR=${D}${base_prefix}/environment-setup.d
    mkdir "${ENV_SETUP_DIR}"
    RUST_ENV_SETUP_SH="${ENV_SETUP_DIR}/rust.sh"

    cat <<- EOF > "${RUST_ENV_SETUP_SH}"
	export RUSTFLAGS="--sysroot=\$OECORE_TARGET_SYSROOT/usr -C link-arg=--sysroot=\$OECORE_TARGET_SYSROOT"
	export RUST_TARGET_PATH="\$OECORE_NATIVE_SYSROOT/usr/lib/${TARGET_SYS}/rustlib"
	EOF

    chown -R root.root ${D}

    CARGO_ENV_SETUP_SH="${ENV_SETUP_DIR}/cargo.sh"
    cat <<- EOF > "${CARGO_ENV_SETUP_SH}"
	export CARGO_HOME="\$OECORE_TARGET_SYSROOT/home/cargo"
	mkdir -p "\$CARGO_HOME"
        # Init the default target once, it might be otherwise user modified.
	if [ ! -f "\$CARGO_HOME/config" ]; then
		touch "\$CARGO_HOME/config"
		echo "[build]" >> "\$CARGO_HOME/config"
		echo 'target = "'${RUST_TARGET_SYS}'"' >> "\$CARGO_HOME/config"
		echo '# TARGET_SYS' >> "\$CARGO_HOME/config"
		echo '[target.'${RUST_TARGET_SYS}']' >> "\$CARGO_HOME/config"
		echo 'linker = "target-rust-ccld"' >> "\$CARGO_HOME/config"
    fi

	# Keep the below off as long as HTTP/2 is disabled.
	export CARGO_HTTP_MULTIPLEXING=false

	export CARGO_HTTP_CAINFO="\$OECORE_NATIVE_SYSROOT/etc/ssl/certs/ca-certificates.crt"
	EOF
}

FILES:${PN} += "${base_prefix}/environment-setup.d"

