require recipes-devtools/rust/rust-source.inc
require recipes-devtools/rust/rust-snapshot.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/cargo-${PV}:"

require cargo-cross-canadian.inc
