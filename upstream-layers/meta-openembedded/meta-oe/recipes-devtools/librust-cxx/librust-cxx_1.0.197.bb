SUMMARY = "Safe interoperability between Rust and C++"
HOMEPAGE = "https://crates.io/crates/cxx"
DESCRIPTION = "cxx is a library that enables safe and efficient interoperability \
               between Rust and C++ code. It defines the FFI boundary in a single \
               Rust module, allowing static analysis of types and function signatures \
               for compatibility and safety. The library generates the necessary Rust \
               and C++ bindings automatically during the build process. It provides near \
               zero-overhead integration and supports idiomatic use of common Rust and C++ \
               standard library types."

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE-MIT;md5=b377b220f43d747efdec40d69fcaa69d \
"

SRC_URI = "crate://crates.io/cxx/${PV};name=cxx"
SRC_URI[cxx.sha256sum] = "00424da159cc5adcb4eeea7e7b5cb1d96df41f5fa695ec596922181bdc36232a"

S = "${CARGO_VENDORING_DIRECTORY}/cxx-${PV}"

DEBUG_PREFIX_MAP += "-ffile-prefix-map=${CARGO_HOME}=${TARGET_DBGSRC_DIR}"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc

do_install () {
    install -d ${D}${rustlibdir}
    # The cxx deps directory also contains dependency files (.d) generated
    # during compilation. These files are only needed for incremental builds
    # and are not required when installing the cxx libraries.
    rm -f ${B}/target/${RUST_TARGET_SYS}/${BUILD_DIR}/deps/*.d
    cp ${B}/target/${RUST_TARGET_SYS}/${BUILD_DIR}/deps/* ${D}${rustlibdir}
}

BBCLASSEXTEND = "native"
