SUMMARY = "Microsoft's terminal text editor"
HOMEPAGE = "https://github.com/microsoft/edit"
DESCRIPTION = "A simple, fast terminal text editor from Microsoft, \
               written in Rust."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=040b55c26aec8b690f86784e2ce5c674"

SRC_URI = "git://github.com/microsoft/edit.git;protocol=https;nobranch=1;tag=v${PV}"
SRCREV = "d3f86975dc3c1298bf7300dbdf409a1df8d8b2b7"

inherit cargo cargo-update-recipe-crates

DEPENDS = "icu"

# Let Yocto handle stripping instead of Cargo
CARGO_BUILD_FLAGS += " --config profile.release.strip=false"

# Bake the versioned ICU sonames into the binary at compile time by scanning
# the sysroot for the actual major-versioned libicuuc.so.<N> symlink.
do_compile:prepend() {
    ICU_SONAME_VER=$(ls ${STAGING_LIBDIR}/libicuuc.so.* | grep -E '\.so\.[0-9]+$' | head -1 | xargs basename | cut -d. -f3)
    export EDIT_CFG_ICUUC_SONAME="libicuuc.so.${ICU_SONAME_VER}"
    export EDIT_CFG_ICUI18N_SONAME="libicui18n.so.${ICU_SONAME_VER}"
}

require ${BPN}-crates.inc

# edit dlopen()s ICU at runtime for Unicode support; recommend the split
# library packages produced by the icu recipe.
RRECOMMENDS:${PN} = "libicuuc libicui18n"

