#
# This class helps make sure that Python extensions built with PyO3
# and setuptools_rust properly set up the environment for cross compilation
#

inherit cargo python3-dir siteinfo

export PYO3_CROSS="1"
export PYO3_CROSS_PYTHON_VERSION="${PYTHON_BASEVERSION}"
export PYO3_CROSS_LIB_DIR="${STAGING_LIBDIR}"
export CARGO_BUILD_TARGET="${HOST_SYS}"
export RUSTFLAGS
export PYO3_PYTHON="${PYTHON}"
export PYO3_CONFIG_FILE="${WORKDIR}/pyo3.config"

python_pyo3_do_configure () {
    cat > ${WORKDIR}/pyo3.config << EOF
implementation=CPython
version=${PYTHON_BASEVERSION}
shared=true
abi3=false
lib_name=${PYTHON_DIR}
lib_dir=${STAGING_LIBDIR}
pointer_width=${SITEINFO_BITS}
build_flags=WITH_THREAD
suppress_build_script_link_lines=false
EOF
}

EXPORT_FUNCTIONS do_configure
