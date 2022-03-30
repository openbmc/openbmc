require rust-target.inc
require rust-source.inc
require rust-snapshot.inc

INSANE_SKIP:${PN}:class-native = "already-stripped"

do_compile () {
    rust_runx build --stage 2
}

rust_do_install() {
    rust_runx install
}

python () {
    pn = d.getVar('PN')

    if not pn.endswith("-native"):
        raise bb.parse.SkipRecipe("Rust recipe doesn't work for target builds at this time. Fixes welcome.")
}

