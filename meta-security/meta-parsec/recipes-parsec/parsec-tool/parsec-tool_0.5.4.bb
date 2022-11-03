SUMMARY = "Parsec Command Line Interface"
HOMEPAGE = "https://github.com/parallaxsecond/parsec-tool"
LICENSE = "Apache-2.0"

inherit cargo

SRC_URI += "crate://crates.io/parsec-tool/${PV} \
"

RDEPENDS:${PN} = "openssl-bin"

do_install() {
  install -d ${D}/${bindir}
  install -m 755 "${B}/target/${CARGO_TARGET_SUBDIR}/parsec-tool" "${D}${bindir}/parsec-tool"
  install -m 755 "${S}/tests/parsec-cli-tests.sh" "${D}${bindir}/parsec-cli-tests.sh"
}

require parsec-tool_${PV}.inc

# The QA check has been temporarily disabled. An issue has been created 
# upstream to fix this. 
# https://github.com/parallaxsecond/parsec-tool/issues/94
INSANE_SKIP:${PN}-dbg += "buildpaths"
