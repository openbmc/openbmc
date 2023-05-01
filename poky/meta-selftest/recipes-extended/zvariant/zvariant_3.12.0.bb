SUMMARY = "Provides API for encoding/decoding of data to/from D-Bus wire format"
DESCRIPTION = "This crate provides API for encoding/decoding of data to/from D-Bus wire format.\
This binary wire format is simple and very efficient and hence useful outside of D-Bus context as well.\
A modified form of this format, GVariant is very commonly used for efficient storage of arbitrary \
data and is also supported by this crate."
HOMEPAGE = "https://gitlab.freedesktop.org/dbus/zbus/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b377b220f43d747efdec40d69fcaa69d"

SRC_URI = " \
    git://gitlab.freedesktop.org/dbus/zbus;protocol=https;branch=main;subpath=zvariant \
    file://0001-Tweak-zvariant-crate-config.patch;striplevel=2 \
"

SRCREV = "07506776fab5f58e029760bb4b288f670c7eecd6"
S = "${WORKDIR}/zvariant"

python do_clean_lic_file_symlink() {
    bb.utils.remove("LICENCE")
}

addtask clean_lic_file_symlink after do_unpack before do_patch

inherit cargo cargo-update-recipe-crates

# Remove this when the recipe is reproducible
EXCLUDE_FROM_WORLD = "1"

require ${BPN}-crates.inc
require ${BPN}-git-crates.inc
