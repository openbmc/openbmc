SUMMARY = "Parsec Command Line Interface"
HOMEPAGE = "https://github.com/parallaxsecond/parsec-tool"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit cargo cargo-update-recipe-crates

SRC_URI += "\
  crate://crates.io/parsec-tool/${PV} \
  file://0001-parsec-cli-tests.sh-adapt-to-new-serialNumber-output.patch \
"
SRC_URI[parsec-tool-0.7.0.sha256sum] = "76afb4416d04c5af9f81285dfff390b09c6926aabd6b4ee20dc07470a9698732"

B = "${CARGO_VENDORING_DIRECTORY}/${BP}"

do_install() {
  install -d ${D}/${bindir}
  install -m 755 "${B}/target/${CARGO_TARGET_SUBDIR}/parsec-tool" "${D}${bindir}/parsec-tool"
  install -m 755 "${S}/tests/parsec-cli-tests.sh" "${D}${bindir}/parsec-cli-tests.sh"
}

require parsec-tool-crates.inc

RDEPENDS:${PN} = "openssl-bin"

# The QA check has been temporarily disabled. An issue has been created
# upstream to fix this.
# https://github.com/parallaxsecond/parsec-tool/issues/94
INSANE_SKIP:${PN}-dbg += "buildpaths"
