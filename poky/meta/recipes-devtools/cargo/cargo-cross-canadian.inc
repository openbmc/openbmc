SUMMARY = "Cargo, a package manager for Rust cross canadian flavor."

RUST_ALTERNATE_EXE_PATH = "${STAGING_LIBDIR_NATIVE}/llvm-rust/bin/llvm-config"

HOST_SYS = "${HOST_ARCH}-unknown-linux-gnu"
CARGO_RUST_TARGET_CCLD = "${RUST_BUILD_CCLD}"

require recipes-devtools/rust/rust-common.inc
require cargo.inc

CARGO = "${WORKDIR}/${CARGO_SNAPSHOT}/bin/cargo"
BASEDEPENDS:remove = "cargo-native"

export RUST_TARGET_PATH="${WORKDIR}/targets/"

RUSTLIB = " \
	-L ${STAGING_DIR_NATIVE}/${SDKPATHNATIVE}/usr/lib/${TARGET_SYS}/rustlib/${HOST_SYS}/lib \
"

DEPENDS += "rust-native \
            rust-cross-canadian-${TRANSLATED_TARGET_ARCH} \
            virtual/nativesdk-${HOST_PREFIX}compilerlibs \
            nativesdk-openssl nativesdk-zlib \
            virtual/nativesdk-libc \
"

inherit cross-canadian

PN = "cargo-cross-canadian-${TRANSLATED_TARGET_ARCH}"

python do_rust_gen_targets () {
    wd = d.getVar('WORKDIR') + '/targets/'

    rust_gen_target(d, 'BUILD', wd, "", "generic", d.getVar('BUILD_ARCH'))
    rust_gen_target(d, 'HOST', wd, "", "generic", d.getVar('HOST_ARCH'))
}

do_compile:prepend () {
	PKG_CONFIG_PATH="${RECIPE_SYSROOT_NATIVE}/usr/lib/pkgconfig:${PKG_CONFIG_PATH}"
}

do_install () {
    SYS_BINDIR=$(dirname ${D}${bindir})
    install -d "${SYS_BINDIR}"
    install -m 755 "${B}/target/${CARGO_TARGET_SUBDIR}/cargo" "${SYS_BINDIR}"
    for i in ${SYS_BINDIR}/*; do
	chrpath -r "\$ORIGIN/../lib" ${i}
    done

    ENV_SETUP_DIR=${D}${base_prefix}/environment-setup.d
    mkdir "${ENV_SETUP_DIR}"
    ENV_SETUP_SH="${ENV_SETUP_DIR}/cargo.sh"
    cat <<- EOF > "${ENV_SETUP_SH}"
	export CARGO_HOME="\$OECORE_TARGET_SYSROOT/home/cargo"
	mkdir -p "\$CARGO_HOME"
        # Init the default target once, it might be otherwise user modified.
	if [ ! -f "\$CARGO_HOME/config" ]; then
		touch "\$CARGO_HOME/config"
		echo "[build]" >> "\$CARGO_HOME/config"
		echo 'target = "'${TARGET_SYS}'"' >> "\$CARGO_HOME/config"
        fi

	# Keep the below off as long as HTTP/2 is disabled.
	export CARGO_HTTP_MULTIPLEXING=false

	export CARGO_HTTP_CAINFO="\$OECORE_NATIVE_SYSROOT/etc/ssl/certs/ca-certificates.crt"
	EOF
}

PKG_SYS_BINDIR = "${SDKPATHNATIVE}/usr/bin"
FILES:${PN} += "${base_prefix}/environment-setup.d ${PKG_SYS_BINDIR}"

