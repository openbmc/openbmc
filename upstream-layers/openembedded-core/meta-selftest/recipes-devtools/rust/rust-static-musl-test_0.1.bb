# SPDX-License-Identifier: MIT
# Minimal Rust binary to test static musl linking (bug 16076)

SUMMARY = "Minimal Rust binary for static musl linking regression test"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://Cargo.toml \
           file://Cargo.lock \
           file://src/main.rs \
          "

S = "${UNPACKDIR}"
CARGO_SRC_DIR = ""

inherit cargo

COMPATIBLE_HOST = ".*-musl.*"

SSTATE_SKIP_CREATION = "1"
