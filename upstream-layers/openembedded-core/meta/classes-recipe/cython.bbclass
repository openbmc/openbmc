DEPENDS:append = " python3-cython-native"

do_compile[postfuncs] = "strip_cython_metadata"
strip_cython_metadata() {
    # Remove the Cython Metadata headers that we don't need after the build, and
    # may contain build paths.
    find ${S} -name "*.c" -print0 | xargs --no-run-if-empty --null sed -i -e "/BEGIN: Cython Metadata/,/END: Cython Metadata/d"
}
