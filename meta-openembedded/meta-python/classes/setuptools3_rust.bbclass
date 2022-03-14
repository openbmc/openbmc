inherit pyo3 setuptools3

DEPENDS += "python3-setuptools-rust-native"

setuptools3_rust_do_configure() {
    pyo3_do_configure
    cargo_common_do_configure
    setuptools3_do_configure
}

EXPORT_FUNCTIONS do_configure
