SUMMARY = "Parsec Command Line Interface"
HOMEPAGE = "https://github.com/parallaxsecond/parsec-tool"
LICENSE = "Apache-2.0"

inherit cargo

SRC_URI += "crate://crates.io/parsec-tool/${PV} \
"

RDEPENDS:${PN} = "openssl-bin"

do_install() {
  install -d ${D}/${bindir}
  install -m 755 "${B}/target/${TARGET_SYS}/release/parsec-tool" "${D}${bindir}/parsec-tool"
  install -m 755 "${S}/tests/parsec-cli-tests.sh" "${D}${bindir}/parsec-cli-tests.sh"
}

require parsec-tool_${PV}.inc
