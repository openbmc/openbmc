inherit rust

RDEPENDS:${PN}:append:class-target = " ${RUSTLIB_DEP}"

RUSTC_ARCHFLAGS += "-C opt-level=3 -g -L ${STAGING_DIR_HOST}/${rustlibdir} -C linker=${RUST_TARGET_CCLD}"
EXTRA_OEMAKE += 'RUSTC_ARCHFLAGS="${RUSTC_ARCHFLAGS}"'

# Some libraries alias with the standard library but libstd is configured to
# make it difficult or imposisble to use its version. Unfortunately libstd
# must be explicitly overridden using extern.
OVERLAP_LIBS = "\
    libc \
    log \
    getopts \
    rand \
"
def get_overlap_deps(d):
    deps = d.getVar("DEPENDS").split()
    overlap_deps = []
    for o in d.getVar("OVERLAP_LIBS").split():
        l = len([o for dep in deps if (o + '-rs' in dep)])
        if l > 0:
            overlap_deps.append(o)
    return " ".join(overlap_deps)
OVERLAP_DEPS = "${@get_overlap_deps(d)}"

# Prevents multiple static copies of standard library modules
# See https://github.com/rust-lang/rust/issues/19680
RUSTC_PREFER_DYNAMIC = "-C prefer-dynamic"
RUSTC_FLAGS += "${RUSTC_PREFER_DYNAMIC}"

CRATE_NAME ?= "${@d.getVar('BPN').replace('-rs', '').replace('-', '_')}"
BINNAME ?= "${BPN}"
LIBNAME ?= "lib${CRATE_NAME}-rs"
CRATE_TYPE ?= "dylib"
BIN_SRC ?= "${S}/src/main.rs"
LIB_SRC ?= "${S}/src/lib.rs"

rustbindest ?= "${bindir}"
rustlibdest ?= "${rustlibdir}"
RUST_RPATH_ABS ?= "${rustlibdir}:${rustlib}"

def relative_rpaths(paths, base):
    relpaths = set()
    for p in paths.split(':'):
        if p == base:
            relpaths.add('$ORIGIN')
            continue
        relpaths.add(os.path.join('$ORIGIN', os.path.relpath(p, base)))
    return '-rpath=' + ':'.join(relpaths) if len(relpaths) else ''

RUST_LIB_RPATH_FLAGS ?= "${@relative_rpaths(d.getVar('RUST_RPATH_ABS', True), d.getVar('rustlibdest', True))}"
RUST_BIN_RPATH_FLAGS ?= "${@relative_rpaths(d.getVar('RUST_RPATH_ABS', True), d.getVar('rustbindest', True))}"

def libfilename(d):
    if d.getVar('CRATE_TYPE', True) == 'dylib':
        return d.getVar('LIBNAME', True) + '.so'
    else:
        return d.getVar('LIBNAME', True) + '.rlib'

def link_args(d, bin):
    linkargs = []
    if bin:
        rpaths = d.getVar('RUST_BIN_RPATH_FLAGS', False)
    else:
        rpaths = d.getVar('RUST_LIB_RPATH_FLAGS', False)
        if d.getVar('CRATE_TYPE', True) == 'dylib':
            linkargs.append('-soname')
            linkargs.append(libfilename(d))
    if len(rpaths):
        linkargs.append(rpaths)
    if len(linkargs):
        return ' '.join(['-Wl,' + arg for arg in linkargs])
    else:
        return ''

get_overlap_externs () {
    externs=
    for dep in ${OVERLAP_DEPS}; do
        extern=$(ls ${STAGING_DIR_HOST}/${rustlibdir}/lib$dep-rs.{so,rlib} 2>/dev/null \
                    | awk '{print $1}');
        if [ -n "$extern" ]; then
            externs="$externs --extern $dep=$extern"
        else
            echo "$dep in depends but no such library found in ${rustlibdir}!" >&2
            exit 1
        fi
    done
    echo "$externs"
}

do_configure () {
}

oe_runrustc () {
	export RUST_TARGET_PATH="${RUST_TARGET_PATH}"
	bbnote ${RUSTC} ${RUSTC_ARCHFLAGS} ${RUSTC_FLAGS} "$@"
	"${RUSTC}" ${RUSTC_ARCHFLAGS} ${RUSTC_FLAGS} "$@"
}

oe_compile_rust_lib () {
    rm -rf ${LIBNAME}.{rlib,so}
    local -a link_args
    if [ -n '${@link_args(d, False)}' ]; then
        link_args[0]='-C'
        link_args[1]='link-args=${@link_args(d, False)}'
    fi
    oe_runrustc $(get_overlap_externs) \
        "${link_args[@]}" \
        ${LIB_SRC} \
        -o ${@libfilename(d)} \
        --crate-name=${CRATE_NAME} --crate-type=${CRATE_TYPE} \
        "$@"
}
oe_compile_rust_lib[vardeps] += "get_overlap_externs"

oe_compile_rust_bin () {
    rm -rf ${BINNAME}
    local -a link_args
    if [ -n '${@link_args(d, True)}' ]; then
        link_args[0]='-C'
        link_args[1]='link-args=${@link_args(d, True)}'
    fi
    oe_runrustc $(get_overlap_externs) \
        "${link_args[@]}" \
        ${BIN_SRC} -o ${BINNAME} "$@"
}
oe_compile_rust_bin[vardeps] += "get_overlap_externs"

oe_install_rust_lib () {
    for lib in $(ls ${LIBNAME}.{so,rlib} 2>/dev/null); do
        echo Installing $lib
        install -D -m 755 $lib ${D}/${rustlibdest}/$lib
    done
}

oe_install_rust_bin () {
    echo Installing ${BINNAME}
    install -D -m 755 ${BINNAME} ${D}/${rustbindest}/${BINNAME}
}

do_rust_bin_fixups() {
    for f in `find ${PKGD} -name '*.so*'`; do
        echo "Strip rust note: $f"
        ${OBJCOPY} -R .note.rustc $f $f
    done
}
PACKAGE_PREPROCESS_FUNCS += "do_rust_bin_fixups"

