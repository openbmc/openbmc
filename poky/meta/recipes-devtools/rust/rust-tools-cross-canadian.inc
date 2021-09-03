
require rust-cross-canadian-common.inc

RUST_TOOLS_CLIPPY_PN = "rust-tools-clippy-cross-canadian-${TRANSLATED_TARGET_ARCH}"
RUST_TOOLS_RUSTFMT_PN = "rust-tools-rustfmt-cross-canadian-${TRANSLATED_TARGET_ARCH}"
RUST_TOOLS_PKGS = "${RUST_TOOLS_CLIPPY_PN} ${RUST_TOOLS_RUSTFMT_PN}"
PN = "rust-tools-cross-canadian-${TRANSLATED_TARGET_ARCH}"

PACKAGES = "${RUST_TOOLS_CLIPPY_PN} ${RUST_TOOLS_RUSTFMT_PN} ${PN}"
RDEPENDS:${PN} += "${RUST_TOOLS_PKGS}"

do_compile () {
    rust_runx build --stage 2 src/tools/clippy
    rust_runx build --stage 2 src/tools/rustfmt
}

do_install () {
    SYS_BINDIR=$(dirname ${D}${bindir})

    install -d "${SYS_BINDIR}"
    cp build/${SNAPSHOT_BUILD_SYS}/stage2-tools-bin/* ${SYS_BINDIR}
    for i in ${SYS_BINDIR}/*; do
	chrpath -r "\$ORIGIN/../lib" ${i}
    done

    chown -R root.root ${D}
}

ALLOW_EMPTY:${PN} = "1"

PKG_SYS_BINDIR = "${SDKPATHNATIVE}/usr/bin"
FILES:${RUST_TOOLS_CLIPPY_PN} = "${PKG_SYS_BINDIR}/cargo-clippy ${PKG_SYS_BINDIR}/clippy-driver"
FILES:${RUST_TOOLS_RUSTFMT_PN} = "${PKG_SYS_BINDIR}/rustfmt"

SUMMARY:${PN} = "Rust helper tools"
SUMMARY:${RUST_TOOLS_CLIPPY_PN} = "A collection of lints to catch common mistakes and improve your Rust code"
SUMMARY:${RUST_TOOLS_RUSTFMT_PN} = "A tool for formatting Rust code according to style guidelines"

