SUMMARY = "The build backend part of an 'An extremely fast Python package and project manager, written in Rust.'"
HOMEPAGE = "https://pypi.org/project/uv-build/"

LICENSE = "Apache-2.0 AND BSD-2-Clause AND MIT"
LIC_FILES_CHKSUM = "file://LICENSE-APACHE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://LICENSE-MIT;md5=45674e482567aa99fe883d3270b11184 \
                    file://crates/uv-build/LICENSE-APACHE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://crates/uv-build/LICENSE-MIT;md5=45674e482567aa99fe883d3270b11184 \
                    file://crates/uv-pep440/License-Apache;md5=e23fadd6ceef8c618fc1c65191d846fa \
                    file://crates/uv-pep440/License-BSD;md5=ef7a6027dc4c2389b9afad7e690274c7 \
                    file://crates/uv-pep508/License-Apache;md5=e23fadd6ceef8c618fc1c65191d846fa \
                    file://crates/uv-pep508/License-BSD;md5=ef7a6027dc4c2389b9afad7e690274c7"

SRC_URI[sha256sum] = "c83c0f1dd2c921091d1974f56d64221c010e173d9e6d52ec2a2582650c2d5197"

require ${BPN}-crates.inc

inherit pypi python_maturin cargo-update-recipe-crates pkgconfig

DEPENDS += "zstd"

PYPI_PACKAGE = "uv_build"

BBCLASSEXTEND = "native"
INSANE_SKIP:${PN} = "already-stripped"
