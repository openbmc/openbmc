SUMMARY = "Parsec Command Line Interface"
HOMEPAGE = "https://github.com/parallaxsecond/parsec-tool"
LICENSE = "Apache-2.0"

inherit cargo

SRC_URI += "crate://crates.io/parsec-tool/${PV} \
"

do_install() {
  install -d ${D}/${bindir}
  install -m 755 "${B}/target/${TARGET_SYS}/release/parsec-tool" "${D}${bindir}/parsec-tool"
}

require parsec-tool_${PV}.inc
