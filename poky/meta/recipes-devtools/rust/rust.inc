SUMMARY = "Rust compiler and runtime libaries"
HOMEPAGE = "http://www.rust-lang.org"
SECTION = "devel"
LICENSE = "MIT | Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=93a95682d51b4cb0a633a97046940ef0"

inherit rust
inherit cargo_common

DEPENDS += "file-native python3-native"
DEPENDS:append:class-native = " rust-llvm-native"

S = "${RUSTSRC}"

# We generate local targets, and need to be able to locate them
export RUST_TARGET_PATH="${WORKDIR}/targets/"

export FORCE_CRATE_HASH="${BB_TASKHASH}"

RUST_ALTERNATE_EXE_PATH ?= "${STAGING_LIBDIR}/llvm-rust/bin/llvm-config"
export YOCTO_ALTERNATE_EXE_PATH = "${RUST_ALTERNATE_EXE_PATH}"
export YOCTO_ALTERNATE_MULTILIB_NAME = "/${BASELIB}"

# We don't want to use bitbakes vendoring because the rust sources do their
# own vendoring.
CARGO_DISABLE_BITBAKE_VENDORING = "1"

# We can't use RUST_BUILD_SYS here because that may be "musl" if
# TCLIBC="musl". Snapshots are always -unknown-linux-gnu
SNAPSHOT_BUILD_SYS = "${RUST_BUILD_ARCH}-unknown-linux-gnu"
setup_cargo_environment () {
    # The first step is to build bootstrap and some early stage tools,
    # these are build for the same target as the snapshot, e.g.
    # x86_64-unknown-linux-gnu.
    # Later stages are build for the native target (i.e. target.x86_64-linux)
    cargo_common_do_configure

    printf '[target.%s]\n' "${SNAPSHOT_BUILD_SYS}" >> ${CARGO_HOME}/config
    printf "linker = '%s'\n" "${RUST_BUILD_CCLD}" >> ${CARGO_HOME}/config
}

inherit rust-target-config

do_rust_setup_snapshot () {
    for installer in "${WORKDIR}/rust-snapshot-components/"*"/install.sh"; do
        "${installer}" --prefix="${WORKDIR}/rust-snapshot" --disable-ldconfig
    done

    # Some versions of rust (e.g. 1.18.0) tries to find cargo in stage0/bin/cargo
    # and fail without it there.
    mkdir -p ${RUSTSRC}/build/${BUILD_SYS}
    ln -sf ${WORKDIR}/rust-snapshot/ ${RUSTSRC}/build/${BUILD_SYS}/stage0

    # Need to use uninative's loader if enabled/present since the library paths
    # are used internally by rust and result in symbol mismatches if we don't
    if [ ! -z "${UNINATIVE_LOADER}" -a -e "${UNINATIVE_LOADER}" ]; then
        for bin in cargo rustc rustdoc; do
            patchelf-uninative ${WORKDIR}/rust-snapshot/bin/$bin --set-interpreter ${UNINATIVE_LOADER}
        done
    fi
}
addtask rust_setup_snapshot after do_unpack before do_configure
do_rust_setup_snapshot[dirs] += "${WORKDIR}/rust-snapshot"
do_rust_setup_snapshot[vardepsexclude] += "UNINATIVE_LOADER"

python do_configure() {
    import json
    try:
        import configparser
    except ImportError:
        import ConfigParser as configparser

    # toml is rather similar to standard ini like format except it likes values
    # that look more JSON like. So for our purposes simply escaping all values
    # as JSON seem to work fine.

    e = lambda s: json.dumps(s)

    config = configparser.RawConfigParser()

    # [target.ARCH-poky-linux]
    target_section = "target.{}".format(d.getVar('TARGET_SYS', True))
    config.add_section(target_section)

    llvm_config = d.expand("${YOCTO_ALTERNATE_EXE_PATH}")
    config.set(target_section, "llvm-config", e(llvm_config))

    config.set(target_section, "cxx", e(d.expand("${RUST_TARGET_CXX}")))
    config.set(target_section, "cc", e(d.expand("${RUST_TARGET_CC}")))

    # If we don't do this rust-native will compile it's own llvm for BUILD.
    # [target.${BUILD_ARCH}-unknown-linux-gnu]
    target_section = "target.{}".format(d.getVar('SNAPSHOT_BUILD_SYS', True))
    config.add_section(target_section)

    config.set(target_section, "llvm-config", e(llvm_config))

    config.set(target_section, "cxx", e(d.expand("${RUST_BUILD_CXX}")))
    config.set(target_section, "cc", e(d.expand("${RUST_BUILD_CC}")))

    # [llvm]
    config.add_section("llvm")
    config.set("llvm", "static-libstdcpp", e(False))

    # [rust]
    config.add_section("rust")
    config.set("rust", "rpath", e(True))
    config.set("rust", "channel", e("stable"))

    # Whether or not to optimize the compiler and standard library
    config.set("rust", "optimize", e(True))

    # [build]
    config.add_section("build")
    config.set("build", "submodules", e(False))
    config.set("build", "docs", e(False))

    rustc = d.expand("${WORKDIR}/rust-snapshot/bin/rustc")
    config.set("build", "rustc", e(rustc))

    # Support for the profiler runtime to generate e.g. coverage report,
    # PGO etc.
    config.set("build", "profiler", e(True))

    cargo = d.expand("${WORKDIR}/rust-snapshot/bin/cargo")
    config.set("build", "cargo", e(cargo))

    config.set("build", "vendor", e(True))

    if not "targets" in locals():
        targets = [d.getVar("TARGET_SYS", True)]
    config.set("build", "target", e(targets))

    if not "hosts" in locals():
        hosts = [d.getVar("HOST_SYS", True)]
    config.set("build", "host", e(hosts))

    # We can't use BUILD_SYS since that is something the rust snapshot knows
    # nothing about when trying to build some stage0 tools (like fabricate)
    config.set("build", "build", e(d.getVar("SNAPSHOT_BUILD_SYS", True)))

    # [install]
    config.add_section("install")
    # ./x.py install doesn't have any notion of "destdir"
    # but we can prepend ${D} to all the directories instead
    config.set("install", "prefix",  e(d.getVar("D", True) + d.getVar("prefix", True)))
    config.set("install", "bindir",  e(d.getVar("D", True) + d.getVar("bindir", True)))
    config.set("install", "libdir",  e(d.getVar("D", True) + d.getVar("libdir", True)))
    config.set("install", "datadir", e(d.getVar("D", True) + d.getVar("datadir", True)))
    config.set("install", "mandir",  e(d.getVar("D", True) + d.getVar("mandir", True)))

    with open("config.toml", "w") as f:
        f.write('changelog-seen = 2\n\n')
        config.write(f)

    # set up ${WORKDIR}/cargo_home
    bb.build.exec_func("setup_cargo_environment", d)
}


rust_runx () {
    echo "COMPILE ${PN}" "$@"

    # CFLAGS, LDFLAGS, CXXFLAGS, CPPFLAGS are used by rust's build for a
    # wide range of targets (not just TARGET). Yocto's settings for them will
    # be inappropriate, avoid using.
    unset CFLAGS
    unset LDFLAGS
    unset CXXFLAGS
    unset CPPFLAGS

    oe_cargo_fix_env

    python3 src/bootstrap/bootstrap.py ${@oe.utils.parallel_make_argument(d, '-j %d')} "$@" --verbose
}
rust_runx[vardepsexclude] += "PARALLEL_MAKE"

do_compile () {
    rust_runx build
}

rust_do_install () {
    mkdir -p ${D}${bindir}
    cp build/${HOST_SYS}/stage2/bin/* ${D}${bindir}

    mkdir -p ${D}${libdir}/rustlib
    cp -pRd build/${HOST_SYS}/stage2/lib/* ${D}${libdir}
    # Remove absolute symlink so bitbake doesn't complain
    rm -f ${D}${libdir}/rustlib/src/rust
}

rust_install_targets() {
    # Install our custom target.json files
    local td="${D}${libdir}/rustlib/"
    install -d "$td"
    for tgt in "${WORKDIR}/targets/"* ; do
        install -m 0644 "$tgt" "$td"
    done
}


do_install () {
    rust_do_install
    rust_install_targets
}
# ex: sts=4 et sw=4 ts=8
